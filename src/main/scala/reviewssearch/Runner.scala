package reviewssearch

import cats.effect.Resource
import monix.eval.Task
import reviewssearch.storage.{ReviewsReader, Settings, SettingsImp}

object Runner {
  val numberOfResults = 1000
  val minWordLength = 5
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

  def main(args: Array[String]): Unit = {
    action()
  }

  def action(): Unit = {

    val settings = Task.evalOnce(new SettingsImp)
    val acquired =
      Resource
        .fromAutoCloseable[Task, Settings](settings)
        .use(settings =>
          new ReviewsReader[Task](settings).acquire(numberOfResults, minWordLength, predicates))

    import monix.execution.Scheduler.Implicits.global

    acquired.foreach {
      case (timings, (products, users, words)) =>
        println(timings.data.mapValues(_.toSeconds))
    }
  }
}
