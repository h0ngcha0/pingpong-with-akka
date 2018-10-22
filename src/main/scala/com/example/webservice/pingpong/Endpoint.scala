package com.example.webservice.pingpong

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.TextMessage
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout
import com.typesafe.config.Config
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import play.api.libs.json.{Json, Writes}
import Protocols._

import scala.concurrent._
import scala.concurrent.duration._

class Endpoint()(
  implicit val system: ActorSystem,
  implicit val executor: ExecutionContextExecutor,
  implicit val materializer: Materializer,
  implicit val config: Config
) extends PlayJsonSupport {
  implicit val timeout = Timeout(5.seconds)

  val availableBallsRoute = (path("available-balls") & get) {
    extractUpgradeToWebSocket { upgrade =>
      complete {
        val source = Source(Ball.all)
          .map(toJsonTextMessage[Ball])
          .throttle(1, 2.seconds)
        upgrade.handleMessagesWithSinkSource(Sink.ignore, source)
      }
    }
  }

  val routes = BasicPingPong.routes ~
    BasicPingPongSupervisor.routes ~
    ClusteredPingPongSupervisor.routes ~
    PersistentPingPongSupervisor.routes ~
    availableBallsRoute


  private def toJsonTextMessage[T: Writes](message: T): TextMessage.Strict = {
    val jsonValue = Json.toJson(message)
    TextMessage(Json.stringify(jsonValue))
  }
}