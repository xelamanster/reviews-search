package reviewssearch.storage.model

import java.sql.Timestamp
import org.apache.spark.sql.types._

import Review.Fields._

object Review {
  object Fields {
    final val Id: String = "Id"
    final val ProductId: String = "ProductId"
    final val UserId: String = "UserId"
    final val ProfileName: String = "ProfileName"
    final val HelpfulnessNumerator: String = "HelpfulnessNumerator"
    final val HelpfulnessDenominator: String = "HelpfulnessDenominator"
    final val Score: String = "Score"
    final val Time: String = "Time"
    final val Summary: String = "Summary"
    final val Text: String = "Text"
  }

  val schema = StructType(
    Array(
      StructField(Id, LongType),
      StructField(ProductId, StringType),
      StructField(UserId, StringType),
      StructField(ProfileName, StringType),
      StructField(HelpfulnessNumerator, IntegerType),
      StructField(HelpfulnessDenominator, IntegerType),
      StructField(Score, IntegerType),
      StructField(Time, StringType),
      StructField(Summary, StringType),
      StructField(Text, StringType)
    )
  )
}

case class Review(
    Id: Option[Long],
    ProductId: String,
    UserId: String,
    ProfileName: String,
    HelpfulnessNumerator: Option[Int],
    HelpfulnessDenominator: Option[Int],
    Score: Option[Int],
    Time: Timestamp,
    Summary: String,
    Text: String
)

object WordCount {
  final val Delimiter = " "
  object Fields {
    final val Word: String = "word"
    final val Count: String = "count"
  }
}

case class WordCount(word: String, count: Long)
