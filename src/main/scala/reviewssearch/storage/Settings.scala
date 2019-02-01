package reviewssearch.storage

import org.apache.spark.sql.SparkSession

trait Settings extends AutoCloseable {
  val session: SparkSession
  val reviewsResource: String
  val csvOptions: Map[String, String]
}

class SettingsImp extends Settings {
  private val appName = "reviews-search"
  private val nodeConfig = "local[4]"

  override val reviewsResource: String =
    raw"D:/Projects/xelamanster/reviews-search/src/main/resources/Reviews.csv"

  override val session: SparkSession = SparkSession
    .builder()
    .appName(appName)
    .master(nodeConfig)
    .getOrCreate()

  session.sparkContext.setLogLevel("ERROR")

  override def close(): Unit = session.close()

  override val csvOptions: Map[String, String] = Map(
    "header" -> "true",
    "charset" -> "UTF8",
    "delimiter" -> ","
  )
}
