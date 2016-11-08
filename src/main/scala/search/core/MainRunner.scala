package search.core

import akka.actor.{ActorSystem, Props}
import search.actors.{MasterActor, Start}
import search.storage.Persistence

object MainRunner extends App {
  val actorSystem = ActorSystem(Settings.applicationName)
  val masterActor = actorSystem.actorOf(Props[MasterActor])

  Persistence.init(Settings.dbAddress, Settings.dbPort)
  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run() {
      Persistence.close()
    }
  })

  masterActor ! Start
}