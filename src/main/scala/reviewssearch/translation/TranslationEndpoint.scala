package reviewssearch.translation

import cats.effect.Sync

trait TranslationEndpoint[F[_]] {
  def translate(request: TranslationRequest): F[TranslationRespond]
}

class TranslationEndpointFake[F[_]: Sync] extends TranslationEndpoint[F] {
  override def translate(request: TranslationRequest): F[TranslationRespond] =
    Sync[F].pure(TranslationRespond(s"Translated: ${request.text}"))
}
