package reviewssearch.translation

import cats.effect.Concurrent
import cats.effect.concurrent.Semaphore
import cats.effect.implicits._
import reviewssearch.storage.model.Review

class TranslationService[F[_]: Concurrent](endpoint: TranslationEndpoint[F],
                                           maxConcurrentRequests: Long,
                                           maxLength: Long) {
  private val semaphore = Semaphore[F](maxConcurrentRequests)

  def translate(r: Review): F[Review] = {
    ???
  }
}
