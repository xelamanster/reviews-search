package utils

import cats.Monoid
import cats.data.WriterT
import cats.effect.Sync
import scala.concurrent.duration.FiniteDuration

import cats.implicits._
import scala.concurrent.duration._

object Timings {
  case class Timings(data: Map[String, FiniteDuration])

  implicit val timingsMonoid: Monoid[Timings] = new Monoid[Timings] {
    def empty: Timings = Timings(Map.empty)
    def combine(x: Timings, y: Timings): Timings = Timings(x.data ++ y.data)
  }

  implicit class WithTimings[A, F[_]: Sync](action: F[A]) {
    def timed(key: String): WriterT[F, Timings, A] =
      WriterT[F, Timings, A] {
        for {
          startTime <- Sync[F].delay(currentTime()): F[FiniteDuration]
          result <- action
          endTime <- Sync[F].delay(currentTime()): F[FiniteDuration]
        } yield (Timings(Map(key -> (endTime - startTime))), result)
      }
  }

  private def currentTime(): FiniteDuration = System.nanoTime().nanos
}
