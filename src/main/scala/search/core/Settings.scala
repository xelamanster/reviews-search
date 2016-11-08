package search.core

object Settings {
  val applicationName = "reviews-search"

  val dbAddress = "localhost" //TODO Hide db setting, e.g Use path variables
  val dbPort = 9300
  val dbScrollTimeout = 60000
  val dbScrollSize = 100

  val resultsCount = 1000

  val translationWorkersCount = 1000

  val tasksCount = 2

  object ReviewFields {
    val Id = Field("Id")
    val ProductId = Field("ProductId")
    val UserId = Field( "UserId")
    val ProfileName = Field("ProfileName")
    val HelpfulnessNumerator = Field("HelpfulnessNumerator")
    val HelpfulnessDenominator = Field("HelpfulnessDenominator")
    val Score = Field("Score")
    val Time = Field("Time")
    val Summary = Field("Summary")
    val Text = Field("Text")

    case class Field(name: String) {
      val raw = name + ".raw"
    }
  }
}