package com.github.daneko.sample

import arrow.core.left
import arrow.core.right
import arrow.data.EitherT
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.fix
import arrow.effects.handleErrorWith
import arrow.effects.instances.io.monad.monad
import arrow.instances.eithert.monad.binding

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

interface Sample {

    fun getHoge(i: Int): IO<Hoge> = IO { realworld { Hoge(i) } }

    fun getFuga(i: Int): IO<Fuga> = IO { realworld { Fuga(i) } }

    fun getResult(a: Int, b: Int): EitherT<ForIO, MyError, Result> {

        return binding(IO.monad()) {

            val hoge = EitherT(getHoge(a)
                .map { it.right() }
                .handleErrorWith {
                    when (it) {
                        is HandleException -> IO.just(MyError.HogeError(it).left())
                        else -> IO.raiseError(it)
                    }
                }).bind()

            val fuga = EitherT(getFuga(a)
                .map { it.right() }
                .handleErrorWith {
                    when (it) {
                        is HandleException -> IO.just(MyError.FugaError(it).left())
                        else -> IO.raiseError(it)
                    }
                }).bind()

            Result(hoge.i + fuga.i)
        }
    }
}

fun main(args: Array<String>) {
    val sample = object : Sample {}

    for (it in 1..10) {
        sample.getResult(it, it * 2).value().fix().unsafeRunAsync {
            println(it)
        }
    }
}
