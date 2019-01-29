package reviewssearch

import org.apache.spark.sql.functions._
import reviewssearch.storage.Settings
import reviewssearch.storage.algebra.ReviewsAlgebra
import reviewssearch.storage.model.{Review, WordCount}
import utils.ClosableWrapper

import java.nio.file.{FileSystems, Paths}
import java.util
import java.net.URI

import ReviewsAlgebra._

object Runner {

  def main(args: Array[String]): Unit = {
    val resource = raw"D:/Projects/xelamanster/reviews-search/src/main/resources/Reviews.csv"

    import reviewssearch.storage.Settings.session.implicits._

    val predicates =
      List(
        "the",
        "I",
        "and",
        "a",
        "to",
        " ",
        "of",
        "is",
        "it",
        "for",
        "in",
        "this",
        "that",
        "my",
        "have",
        "are",
        "was",
        "but"
      )

    ClosableWrapper(Settings.session) { s =>
      val reviews = s.read
        .option("header", "true")
        .option("charset", "UTF8")
        .option("delimiter", ",")
        .schema(Review.schema)
        .csv(resource)
        .as[Review]

      findTop(_.ProfileName, reviews).show()
      findTop(_.ProductId, reviews).show()

      countUsedWords[WordCount](Review.Fields.Text, WordCount.Delimiter, reviews)
        .filter(not(col(WordCount.Fields.Word).isin(predicates: _*)))
        .filter(length(col(WordCount.Fields.Word)) > 5)
        .show()
    }
  }
}
