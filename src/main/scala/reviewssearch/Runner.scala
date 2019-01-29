package reviewssearch

import java.nio.file.Paths

import org.apache.spark.sql.functions._
import reviewssearch.storage.Settings
import reviewssearch.storage.algebra.ReviewsAlgebra
import reviewssearch.storage.model.{Review, WordCount}

object Runner {

  def main(args: Array[String]): Unit = {
    val resource = "/Reviews.csv"

    val path = Paths.get(getClass.getResource(resource).toURI).toString

    import reviewssearch.storage.Settings.session.implicits._

    val reviews = Settings.session.read
      .option("header", "true")
      .option("charset", "UTF8")
      .option("delimiter", ",")
      .schema(Review.schema)
      .csv(path)
      .as[Review]

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

    ReviewsAlgebra.findMost(_.ProfileName, reviews).show()
    ReviewsAlgebra.findMost(_.ProductId, reviews).show()

    ReviewsAlgebra
      .countUsedWords[WordCount](Review.Fields.Text, WordCount.Delimiter, reviews)
      .filter(not(col(WordCount.Fields.Word).isin(predicates: _*)))
      .filter(length(col(WordCount.Fields.Word)) > 5)
      .rdd

  }
}
