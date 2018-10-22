package com.example.webservice.pingpong

import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, Props}
import akka.http.scaladsl.server.Directives._
import akka.actor.SupervisorStrategy._
import akka.pattern.ask

class ClusteredPingPongSupervisor extends Actor with ActorLogging {
  override val supervisorStrategy = OneForOneStrategy() {
    case _: Killed  => Stop
    case _: Faint   => Resume
    case _: Injured => Restart
  }

  val clusteredPingPong = context.actorOf(ClusteredPingPong.props, ClusteredPingPong.getClass.getSimpleName)

  def receive: Receive = {
    case msg @ _  =>
      log.info("forwarding message {} to child.", msg)
      clusteredPingPong forward msg
  }
}

object ClusteredPingPongSupervisor {
  def props: Props = Props[ClusteredPingPongSupervisor]

  def routes(implicit system: ActorSystem) = {
    val clusteredPingPongSupervisor = system.actorOf(ClusteredPingPongSupervisor.props, "ClusteredPingPongSupervisor")

    val clusteredPingRoutes = RouteHelper.pingRoutes(clusteredPingPongSupervisor)
    val clusteredAllRoutes = RouteHelper.allRoutes(clusteredPingPongSupervisor)

    pathPrefix("clustered") {
      clusteredPingRoutes ~ clusteredAllRoutes
    }
  }
}

