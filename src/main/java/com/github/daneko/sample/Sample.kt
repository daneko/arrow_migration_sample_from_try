package com.github.daneko.sample

import arrow.Kind
import arrow.core.left
import arrow.core.right
import arrow.data.EitherT
import arrow.effects.DeferredK
import arrow.effects.ForDeferredK
import arrow.effects.deferredk.async.async
import arrow.effects.fix
import arrow.effects.typeclasses.Async
import arrow.effects.unsafeRunAsync
import arrow.instances.eithert.monad.binding
import arrow.typeclasses.MonadThrow

data class Hoge(val i: Int)
data class Fuga(val i: Int)

data class Result(private val i: Int)

sealed class MyError : Throwable() {
    data class HogeError(override val cause: Throwable) : MyError()
    data class FugaError(override val cause: Throwable) : MyError()
}

class HandleException : Exception("plz handling")

fun <A> realworld(f: () -> A): A = Math.random().let {
    when {
        it > 0.3 -> f()
        it > 0.01 -> throw HandleException()
        else -> throw RuntimeException("／(^o^)＼")
    }
}

interface Logic<F> : MonadThrow<F> {
    fun getHoge(i: Int): Kind<F, Hoge> = this.catch { realworld { Hoge(i) } }

    fun getFuga(i: Int): Kind<F, Fuga> = this.catch { realworld { Fuga(i) } }
}

interface Sample<F> : Async<F> {

    val logic: Logic<F>

    fun getResult(a: Int, b: Int): EitherT<F, MyError, Result> {

        return binding(this) {

            val hoge = EitherT(logic.getHoge(a)
                .map { it.right() }
                .handleErrorWith {
                    when (it) {
                        is HandleException -> this@Sample.just(MyError.HogeError(it).left())
                        else -> raiseError(it)
                    }
                })
                .bind()

            val fuga = EitherT(logic.getFuga(a)
                .map { it.right() }
                .handleErrorWith {
                    when (it) {
                        is HandleException -> this@Sample.just(MyError.FugaError(it).left())
                        else -> raiseError(it)
                    }
                }).bind()

            Result(hoge.i + fuga.i)
        }
    }
}

fun main(args: Array<String>) {
    val instance = DeferredK.async()

    @Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
    val logic = object : Logic<ForDeferredK>, MonadThrow<ForDeferredK> by instance {}

    @Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
    val sample = object : Sample<ForDeferredK>, Async<ForDeferredK> by instance {
        override val logic: Logic<ForDeferredK> = logic
    }

    for (it in 1..10) {
        sample.getResult(it, it * 2).value().fix().unsafeRunAsync {
            println(it)
        }
    }
}
