package search.actors

import akka.actor.{Actor, ActorLogging}

case class Print(field: String, frequency: Map[String, Long])
case class PrintError(t: Throwable)

class StandardOutputActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case Print(field, map) => printResult(field, map)
    case PrintError(t) => log.error(t, "Something going wrong")
  }

  def printResult(field: String, map: Map[String, Long]): Unit = {
    log.info(s"${map.size} most frequent occurrences in column $field")
    map.foreach { case (key, count) => log.info(key)}
  }
}