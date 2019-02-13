package reviewssearch

import cats.effect.Resource
import monix.eval.Task
import org.apache.log4j.Logger
import reviewssearch.storage.model.WordCount
import reviewssearch.storage.{ReviewsReader, Settings, SettingsImp}
import utils.Timings

object Runner {
  private val log = Logger.getLogger(Runner.getClass)

  type QueryResult = (Timings.Timings, (Seq[(String, Long)], Seq[(String, Long)], Seq[WordCount]))

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
    import monix.execution.Scheduler.Implicits.global

    action().foreach {
      case (timings, (products, users, words)) =>
        log.info(timings.data.mapValues(_.toSeconds))
    }
  }

  def action(): Task[QueryResult] = {
    val settings = Task.evalOnce(new SettingsImp)
    Resource
      .fromAutoCloseable[Task, Settings](settings)
      .use(settings =>
        new ReviewsReader[Task](settings).acquire(numberOfResults, minWordLength, predicates))

  }
}
