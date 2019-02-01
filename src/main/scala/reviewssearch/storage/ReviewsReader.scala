package reviewssearch.storage

import reviewssearch.storage.model.{Review, WordCount}
import utils.Timings

import cats.effect.Sync

import utils.Timings._
import ReviewsReader._

object ReviewsReader {
  private val TopProducts = "topProducts"
  private val TopUsers = "topUsers"
  private val TopUsedWords = "topUsedWords"
}

class ReviewsReader[F[_]: Sync](settings: Settings) {
  import settings.session.implicits._

  def acquire(numberOfResults: Int, minWordLength: Int, exclusions: Seq[String])
    : F[(Timings.Timings, (Seq[(String, Long)], Seq[(String, Long)], Seq[WordCount]))] = {

    val timedResult = for {
      topProducts <- dao.topProducts(numberOfResults).timed(TopProducts)
      topUsers <- dao.topUsers(numberOfResults).timed(TopUsers)
      topUsedWords <- dao
        .topUsedWords(numberOfResults, minWordLength, exclusions)
        .timed(TopUsedWords)
    } yield (topProducts, topUsers, topUsedWords)
    timedResult.run
  }

  private def read() =
    Sync[F].delay(
      settings.session.read
        .options(settings.csvOptions)
        .schema(Review.schema)
        .csv(settings.reviewsResource)
        .as[Review])

  private val dao = new ReviewsDAO[F](settings, read())
}
