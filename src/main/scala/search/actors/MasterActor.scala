package search.actors

import akka.actor.{Actor, ActorLogging, Props}
import search.core.Settings
import search.storage.Persistence
import search.core.Settings.ReviewFields._

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

case object Start

private case object TaskFinished

class MasterActor extends Actor with ActorLogging {
  val printActor = context.actorOf(Props[StandardOutputActor])
  val serviceActor = context.actorOf(Props[ServiceActor])

  var tasksFinished = 0

  override def receive: Receive = {
    case Start => start()
    case TaskFinished => onTaskFinished()
    case Failure(t) => onError(t)
  }

  def start(): Unit = {
    findMostFrequent(ProfileName.raw)
    findMostFrequent(UserId.raw)
    findMostFrequent(Text.name)
  }

  def onError(t: Throwable): Unit = {
    printActor ! PrintError(t)
  }

  def findMostFrequent(fieldName: String): Unit = {
    Persistence.findMostFrequent(fieldName, Settings.resultsCount) onComplete {
      case Success(results) => printActor ! Print(fieldName, results); self ! TaskFinished
      case Failure(t) => onError(t)
    }
  }

  def onTaskFinished(): Unit = {
    tasksFinished += 1
    if(tasksFinished == Settings.tasksCount) serviceActor ! Terminate
  }
}
