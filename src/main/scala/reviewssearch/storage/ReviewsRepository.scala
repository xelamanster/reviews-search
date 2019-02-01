package reviewssearch.storage

import reviewssearch.storage.model.{Review, WordCount}
import org.apache.spark.sql.expressions.scalalang.typed.count
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Dataset, Encoder}

object ReviewsRepository {
  private final val Count = "count"
  private final val Value = "value"
  private final val Word = "word"

  def findTop[F: Encoder](field: Review => F, in: Dataset[Review]): Dataset[(F, Long)] =
    in.groupByKey(field)
      .agg(count[Review](_.Id).name(Count))
      .filter(col(Value).isNotNull)
      .sort(desc(Count))

  def countUsedWords[R: Encoder](
      fieldToSplit: String,
      pattern: String,
      in: Dataset[Review]
  ): Dataset[R] =
    in.withColumn(Word, explode(split(col(fieldToSplit), pattern)))
      .groupBy(col(Word))
      .count()
      .orderBy(desc(Count))
      .as[R]

  def findTopUsedWords(reviews: Dataset[Review], minWordLength: Int, exclusions: Seq[String])(
      implicit e: Encoder[WordCount]): Dataset[WordCount] =
    countUsedWords[WordCount](Review.Fields.Text, WordCount.Delimiter, reviews)
      .filter(not(col(WordCount.Fields.Word).isin(exclusions: _*)))
      .filter(length(col(WordCount.Fields.Word)) > minWordLength)
}
