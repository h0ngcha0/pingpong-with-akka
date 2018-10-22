package com.example.webservice.pingpong

import akka.actor.{ Actor, ActorLogging, OneForOneStrategy, Props }
import akka.actor.SupervisorStrategy._

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
}

