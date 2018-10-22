package com.example.webservice.pingpong

import akka.actor.{ActorRef, ActorSystem}
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

  val basicPingPong = system.actorOf(BasicPingPong.props, "BasicPingPong")
  val persistentPingPongSupervisor = system.actorOf(PersistentPingPongSupervisor.props, "PersistentPingPongSupervisor")
  val pingPongView = system.actorOf(PersistentPingPongView.props, "PersistentPingPongView")

  def pingRoutes(actor: ActorRef) = path("ping") {
    (post & entity(as[Ball])) { ball =>
      complete {
        (actor ? ball).mapTo[Payload]
      }
    } ~
      get {
        complete {
          (actor ? BallsSeen).mapTo[Payload]
        }
      }
  }

  def allRoutes(actor: ActorRef) = path("all" / "ping") {
    (post & entity(as[Ball])) { ball =>
      complete {
        (actor ? ToAll(ball)).mapTo[List[Payload]]
      }
    } ~
      get {
        complete { (actor ? ToAll(BallsSeen)).mapTo[List[Payload]] }
      }
  }

  val statsRoutes = path("stats") {
    get {
      complete { (pingPongView ? BallsSeen).mapTo[Payload] }
    }
  }

  // Basic Routes
  val basicPingRoutes = pathPrefix("basic") {
    pingRoutes(basicPingPong)
  }


  // Persistent Routes
  val persistentPingRoutes = pingRoutes(persistentPingPongSupervisor)
  val persistentAllRoutes = allRoutes(persistentPingPongSupervisor)
  val persistentRoute = pathPrefix("persistent") {
    persistentPingRoutes ~ persistentAllRoutes ~ statsRoutes
  }



  val routes = basicPingRoutes ~ persistentRoute
}
