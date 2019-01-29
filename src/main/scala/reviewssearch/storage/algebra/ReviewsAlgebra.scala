package reviewssearch.storage.algebra

import org.apache.spark.sql.expressions.scalalang.typed.count
import org.apache.spark.sql.functions.{col, desc, explode, split}
import org.apache.spark.sql.{Dataset, Encoder}
import reviewssearch.storage.model.Review

object ReviewsAlgebra {
  private final val Count = "count"
  private final val Value = "value"
  private final val Word = "word"

  def findMost[F: Encoder](field: Review => F, in: Dataset[Review]): Dataset[(F, Long)] =
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
}
