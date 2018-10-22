package com.example.webservice.pingpong

import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, Props}
import akka.http.scaladsl.server.Directives._
import akka.actor.SupervisorStrategy._

class PersistentPingPongSupervisor extends Actor with ActorLogging {

  override val supervisorStrategy = OneForOneStrategy() {
    case _: Killed  => Stop
    case _: Faint   => Resume
    case _: Injured => Restart
  }

  val persistentPingPong = context.actorOf(PersistentPingPong.props, PersistentPingPong.getClass.getSimpleName)

  def receive: Receive = {
    case msg @ _  =>
      log.info("forwarding message {} to child.", msg)
      persistentPingPong forward msg
  }
}

object PersistentPingPongSupervisor {
  def props: Props = Props[PersistentPingPongSupervisor]

  def routes(implicit system: ActorSystem) = {
    val persistentPingPongSupervisor = system.actorOf(PersistentPingPongSupervisor.props, "PersistentPingPongSupervisor")
    val pingPongView = system.actorOf(PersistentPingPongView.props, "PersistentPingPongView")

    val persistentPingRoutes = RouteHelper.pingRoutes(persistentPingPongSupervisor)
    val persistentAllRoutes = RouteHelper.allRoutes(persistentPingPongSupervisor)
    val statsRoutes = RouteHelper.statsRoutes(pingPongView)

    pathPrefix("persistent") {
      persistentPingRoutes ~ persistentAllRoutes ~ statsRoutes
    }
  }
}
