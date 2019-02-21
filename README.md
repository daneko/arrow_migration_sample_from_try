Migration Sample from Try to using Other DataType or TypeClass
 
* Will Arrow's Try remove?
  * Perhaps? [Arrow channel comment](https://kotlinlang.slack.com/archives/C5UPMM0A0/p1550322538098900?thread_ts=1550319070.097800&cid=C5UPMM0A0)
 

### content

* [diff `Try<A>` to `Either<Throwable, A>`](https://github.com/daneko/arrow_migration_sample_from_try/compare/base...Either) 
* [diff `Try<A>` to `DeferredK<A>`](https://github.com/daneko/arrow_migration_sample_from_try/compare/base...Effect) 
  * not recommend... please tell me a nice migration idea.
* [diff `Try<A>` to `EitherT`](https://github.com/daneko/arrow_migration_sample_from_try/compare/base...EitherT_IO)
* [diff `Try<A>` to `EitherT and Kind`](https://github.com/daneko/arrow_migration_sample_from_try/compare/base...EitherT_Kind)
  * [diff `EitherT(with IO)` to `EitherT and Kind`](https://github.com/daneko/arrow_migration_sample_from_try/compare/EitherT_IO...EitherT_Kind)
