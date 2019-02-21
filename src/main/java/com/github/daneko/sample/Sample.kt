package com.github.daneko.sample

import arrow.core.Either
import arrow.effects.DeferredK
import arrow.effects.unsafeAttemptSync
import arrow.instances.either.monad.binding

data class Hoge(val i: Int)
data class Fuga(val i: Int)

data class Result(private val i: Int)

sealed class MyError : Throwable() {
    data class HogeError(override val cause: Throwable) : MyError()
    data class FugaError(override val cause: Throwable) : MyError()
}

fun <A> realworld(f: () -> A): A = Math.random().let {
    if (it > 0.1) f()
    else throw RuntimeException("／(^o^)＼")
}

interface Sample {

    fun getHoge(i: Int): DeferredK<Hoge> = DeferredK { realworld { Hoge(i) } }

    fun getFuga(i: Int): DeferredK<Fuga> = DeferredK { realworld { Fuga(i) } }

    fun getResult(a: Int, b: Int): Either<MyError, Result> {

        return binding {

            // if remove Try, What will becomes of unsafeAttemptSync ?
            val hoge = getHoge(a).unsafeAttemptSync().toEither { MyError.HogeError(it) }.bind()

            val fuga = getFuga(b).unsafeAttemptSync().toEither { MyError.FugaError(it) }.bind()

            Result(hoge.i + fuga.i)
        }
    }
}

fun main(args: Array<String>) {
    val sample = object : Sample {}

    for (it in 1..10) {
        println(sample.getResult(it, it * 2))
    }
}
