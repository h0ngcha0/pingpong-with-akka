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

  val basicPingPong = system.actorOf(BasicPingPong.props, "BasicPingPong")
  val supervisedPingPong = system.actorOf(SupervisedPingPong.props, "SupervisedPingPong")
  val clusteredPingPong = system.actorOf(ClusteredPingPong.props, "ClusteredPingPong")
  val persistentClusteredPingPong = system.actorOf(PersistentClusteredPingPong.props, "PersistentClusteredPingPong")

  val basicPingRoute = path("basic" / "ping") {
    (post & entity(as[Payload])) {
      case msg : Ball => complete { (supervisedPingPong ? msg).mapTo[Payload] }
      case _ => complete(StatusCodes.BadRequest)
    } ~
    get {
      complete { (supervisedPingPong ? BallsSeen).mapTo[Payload] }
    }
  }

  val supervisedRestartRoute = path("supervised" / "ping") {
    (post & entity(as[Payload])) {
      case msg : Ball => complete { (supervisedPingPong ? msg).mapTo[Payload] }
      case _ => complete(StatusCodes.BadRequest)
    } ~
    get {
      complete { (supervisedPingPong ? BallsSeen).mapTo[Payload] }
    }
  }

  val clusteredPingRoute = pathPrefix("clustered") {
    path("ping") {
      (post & entity(as[Payload])) {
        case msg : Ball => complete { (clusteredPingPong ? msg).mapTo[Payload] }
        case _ => complete(StatusCodes.BadRequest)
      } ~
      get {
        complete { (clusteredPingPong ? BallsSeen).mapTo[Payload] }
      }
    } ~
    path("all") {
      get {
        complete { (clusteredPingPong ? BallsSeenAll).mapTo[Payload] }
      }
    }
  }


  val persistentClusteredPingRoute = pathPrefix("persistent" / "clustered") {
    path("ping") {
      (post & entity(as[Payload])) {
        case msg : Ball => complete { (persistentClusteredPingPong ? msg).mapTo[Payload] }
        case _ => complete(StatusCodes.BadRequest)
      } ~
      get {
        complete { (persistentClusteredPingPong ? BallsSeen).mapTo[Payload] }
      }
    } ~
    path("all") {
      get {
        complete { (persistentClusteredPingPong ? BallsSeenAll).mapTo[Payload] }
      }
    }
  }

  def routes = {
    logRequestResult("pingpong-with-akka") {
      basicPingRoute ~
      supervisedRestartRoute ~
      clusteredPingRoute ~
      persistentClusteredPingRoute
    }
  }
}
