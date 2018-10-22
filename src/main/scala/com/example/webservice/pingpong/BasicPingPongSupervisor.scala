package com.example.webservice.pingpong

import akka.actor.{ Actor, ActorLogging, OneForOneStrategy, Props }
import akka.actor.SupervisorStrategy._

class BasicPingPongSupervisor extends Actor with ActorLogging {

  override val supervisorStrategy = OneForOneStrategy() {
    case _: Killed  => Stop
    case _: Faint   => Resume
    case _: Injured => Restart
  }

  val basicPingPong = context.actorOf(BasicPingPong.props, BasicPingPong.getClass.getSimpleName)

  def receive: Receive = {
    case msg @ _  =>
      log.info("forwarding message {} to child.", msg)
      basicPingPong forward msg
  }
}

object BasicPingPongSupervisor {
  def props: Props = Props[BasicPingPongSupervisor]
}
