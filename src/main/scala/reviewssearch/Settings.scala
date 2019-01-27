package reviewssearch

import org.apache.spark.sql.SparkSession

object Settings {
  private val appName = "reviews-search"
  private val nodeConfig = "local[4]"

  val session: SparkSession =
    SparkSession
      .builder()
      .appName(appName)
      .master(nodeConfig)
      .getOrCreate()
}
