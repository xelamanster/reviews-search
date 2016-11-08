package search.actors

import akka.actor.{Actor, ActorLogging}

case object Terminate

class ServiceActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case Terminate => context.system.terminate()
  }
}