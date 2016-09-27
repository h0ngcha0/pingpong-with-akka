package com.example.webservice.pingpong

import scala.concurrent._
import scala.concurrent.duration._

import akka.actor.{Props, ActorSystem}
import akka.event.LoggingAdapter
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import com.typesafe.config.Config

class Endpoint()(
  implicit val system: ActorSystem,
  implicit val executor: ExecutionContextExecutor,
  implicit val materializer: Materializer,
  implicit val config: Config
) extends Protocols {
  implicit val timeout = Timeout(5.seconds)

  val pingPong = system.actorOf(SupervisedPingPong.props, "SupervisedPingPong")


  val routes = path("ping") {
    (post & entity(as[Payload])) {
      case msg : Ball => complete { (pingPong ? msg).mapTo[Payload] }
      case _ => complete(StatusCodes.BadRequest)
    } ~
    get {
      complete { (pingPong ? BallsSeen).mapTo[Payload] }
    }
  }
}
