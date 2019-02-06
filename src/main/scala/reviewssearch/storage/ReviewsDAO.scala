package reviewssearch.storage

import reviewssearch.storage.model.{Review, WordCount}
import reviewssearch.storage.ReviewsRepository._

import cats.effect.Sync
import cats.implicits._
import org.apache.spark.sql.Dataset

class ReviewsDAO[F[_]: Sync](settings: Settings, reviews: F[Dataset[Review]]) {
  import settings.session.implicits._

  def topProducts(n: Int): F[Seq[(String, Long)]] =
    reviews.flatMap(r => take(n, findTop(_.ProductId, r)))

  def topUsers(n: Int): F[Seq[(String, Long)]] =
    reviews.flatMap(r => take(n, findTop(_.ProfileName, r)))

  def topUsedWords(n: Int, minWordLength: Int, exclusions: Seq[String]): F[Seq[WordCount]] =
    reviews.flatMap(r => take(n, findTopUsedWords(r, minWordLength, exclusions)))

  private def take[T](n: Int, set: => Dataset[T]): F[Seq[T]] = Sync[F].delay(set.take(n))

}
