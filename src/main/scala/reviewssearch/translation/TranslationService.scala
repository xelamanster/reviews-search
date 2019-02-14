package reviewssearch.translation

import reviewssearch.storage.model.Review

import cats.effect.{Concurrent, ContextShift}
import cats.effect.concurrent.Semaphore
import cats.Parallel
import cats.implicits._

class TranslationService[F[_]: Concurrent: ContextShift](
    endpoint: TranslationEndpoint[F],
    maxConcurrentRequests: Long,
    maxLength: Int)(implicit ev: Parallel[F, F]) {

  private val contextShift = implicitly[ContextShift[F]]
  private val semaphoreHolder = Semaphore[F](maxConcurrentRequests)

  def translate(r: Review, from: Lang, to: Lang): F[Review] =
    for {
      semaphore <- semaphoreHolder
      reviewParts <- splitOnRequests(r)
      translatedParts <- translateParts(reviewParts, from, to, semaphore)
      review <- combine(r, translatedParts)
    } yield review

  private def splitOnRequests(r: Review): F[Iterator[String]] =
    Concurrent[F].pure(r.Text.sliding(maxLength))

  private def translateParts(reviewParts: Iterator[String],
                             from: Lang,
                             to: Lang,
                             semaphore: Semaphore[F]): F[List[TranslationRespond]] =
    for {
      _ <- semaphore.acquireN(reviewParts.size)
      translated <- parTranslate(reviewParts, from, to)
      _ <- semaphore.releaseN(reviewParts.size)
    } yield translated

  private def parTranslate(requests: Iterator[String],
                           from: Lang,
                           to: Lang): F[List[TranslationRespond]] = {
    def toRequest(text: String) = TranslationRequest(from, to, text)

    requests.toList.map { text =>
      contextShift.shift *> endpoint.translate(toRequest(text))
    }.parSequence
  }

  private def combine(original: Review, translatedParts: List[TranslationRespond]): F[Review] =
    Concurrent[F].delay(original.copy(Text = translatedParts.mkString))
}
