package com.example.webservice.pingpong

import akka.actor.ActorSystem
import akka.actor.Props
import akka.util.Timeout
import akka.pattern.ask
import akka.event.LoggingAdapter
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import com.typesafe.config.Config
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}


class Endpoint()(
  implicit val system: ActorSystem,
  implicit val executor: ExecutionContextExecutor,
  implicit val materializer: Materializer,
  implicit val config: Config
) extends Protocols {
  implicit val timeout = Timeout(5.seconds)

  val basicPingPong = system.actorOf(BasicPingPong.props, "BasicPingPong")
  val supervisedPingPong = system.actorOf(SupervisedPingPong.props, "SupervisedPingPong")

  val basicPingRoute = path("basic" / "ping") {
    (post & entity(as[Ping])) {
      case msg @ Ping(color) => complete { (basicPingPong ? msg).mapTo[Pong]  }
      case _ => complete(StatusCodes.BadRequest)
    }
  }

  val supervisedRestartRoute = path("supervised" / "restart" / "ping") {
    (post & entity(as[Ping])) {
      case msg @ Ping(color) => complete { (supervisedPingPong ? msg).mapTo[Pong]  }
      case _ => complete(StatusCodes.BadRequest)
    }
  }


  def routes = {
    logRequestResult("pingpong-with-akka") {
      basicPingRoute ~
      supervisedRestartRoute
    }
  }
}
