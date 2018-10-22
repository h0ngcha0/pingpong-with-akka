package com.example.webservice.pingpong

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import com.example.webservice.pingpong.Protocols._
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport

import scala.concurrent.duration._

object RouteHelper extends PlayJsonSupport {
  implicit val timeout = Timeout(5.seconds)

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

  def statsRoutes(actor: ActorRef) = path("stats") {
    get {
      complete { (actor ? BallsSeen).mapTo[Payload] }
    }
  }
}
