package search.actors

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, Props, Terminated}
import akka.http.scaladsl.Http
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import search.core.Settings
import search.core.Settings.ReviewFields._
import search.storage.Persistence
import spray.json.DefaultJsonProtocol._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

case object TranslateAllTexts
case class TranslationBundle(id: String, text: String)

class TranslateActor extends Actor with ActorLogging {
  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()

  var translationsInProgress = 0
  var router = {
    val routees = Vector.fill(Settings.translationWorkersCount) {
      val r = context.actorOf(Props[HttpTranslateActor])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case TranslateAllTexts => translateAllTexts()
    case translated: TranslationBundle => onBundleTranslated(translated)
    case Terminated(child) => onChildTerminated(child)
    case Failure(t) => onFailure(t)
  }

  def onFailure(t: Throwable): Unit = {
    context.parent ! Failure(t)
  }

  def translateAllTexts(): Unit = {
    Persistence.scroll(Id.name, Text.name) onComplete {
      case Success(results) => results.foreach(translate) //TODO
      case Failure(t) => onFailure(t)
    }
    context.parent ! TaskFinished
  }
  def translate(bundle: TranslationBundle): Unit ={
    translationsInProgress += 1
    router.route(bundle, self) //TODO check current translations count
  }

  def onBundleTranslated(bundle: TranslationBundle): Unit = {
    translationsInProgress -= 1
    //TODO implement
  }

  def onChildTerminated(child: ActorRef): Unit = {
    router = router.removeRoutee(child)
    val newChild = context.actorOf(Props[HttpTranslateActor])
    context watch newChild
    router = router.addRoutee(newChild)
  }
}

private case class Request(input_lang: String, output_lang: String, text: String)
private case class Respond(text: String)

private trait TradingApiSerialization extends SprayJsonSupport {
  val requestFormat = jsonFormat3(Request)
  implicit val respondFormat = jsonFormat1(Respond)
}

private class HttpTranslateActor extends Actor with ActorLogging with TradingApiSerialization {
  implicit val materializer = ActorMaterializer()

  def receive = {
    case TranslationBundle(id, text) => //processTranslation(id, text) //TODO mock
  }

  def processTranslation(id: String, text: String): Unit = {
    translate(Request("en", "fr", text)) onComplete { //TODO add language selection
      case Success(result) => context.parent ! TranslationBundle(id, result.text)
      case Failure(t) => context.parent ! Failure(t)
    }
  }
  def translate(jsRequest: Request)(implicit context: ActorContext): Future[Respond] = {
    val entity = requestFormat.write(jsRequest).compactPrint
    val source = Source.single(HttpRequest(HttpMethods.POST, Uri("/translate")).withEntity(ContentTypes.`application/json`, entity))
    val flow = Http(context.system).outgoingConnectionHttps("api.google.com").mapAsyncUnordered(1) { r =>
      Unmarshal(r.entity).to[Respond]
    }

    source.via(flow).runWith(Sink.head)
  }
}