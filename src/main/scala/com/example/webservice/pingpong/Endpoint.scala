package com.example.webservice.pingpong

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import com.typesafe.config.Config
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
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

  val pingPongActor = system.actorOf(SupervisedPingPong.props, "PingPong")
  val pingPongView = system.actorOf(PingPongView.props, "PingPongView")

  val pingRoutes = path("ping") {
    (post & entity(as[Ball])) { ball =>
      complete {
        (pingPongActor ? ball).mapTo[Payload]
      }
    } ~
    get {
      complete {
        (pingPongActor ? BallsSeen).mapTo[Payload]
      }
    }
  }

  val allRoutes = path("all" / "ping") {
    (post & entity(as[Ball])) { ball =>
      complete {
        (pingPongActor ? ToAll(ball)).mapTo[List[Payload]]
      }
    } ~
    get {
      complete { (pingPongActor ? ToAll(BallsSeen)).mapTo[List[Payload]] }
    }
  }

  val statsRoutes = path("stats") {
    get {
      complete { (pingPongView ? BallsSeen).mapTo[Payload] }
    }
  }

  val routes = pingRoutes ~ allRoutes ~ statsRoutes
}
