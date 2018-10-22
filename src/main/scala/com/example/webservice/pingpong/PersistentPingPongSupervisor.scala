package com.example.webservice.pingpong

import akka.actor.{ Actor, ActorLogging, OneForOneStrategy, Props }
import akka.actor.SupervisorStrategy._
import akka.event.Logging

class PersistentPingPongSupervisor extends Actor with ActorLogging {

  override val supervisorStrategy = OneForOneStrategy() {
    case _: Killed  => Stop
    case _: Faint   => Resume
    case _: Injured => Restart
  }

  val basicPingPong = context.actorOf(PersistentPingPong.props, "PingPong")

  def receive = {
    case msg @ _  =>
      log.info("forwarding message {} to child.", msg)
      basicPingPong forward msg
  }
}

object PersistentPingPongSupervisor {
  def props: Props = Props[PersistentPingPongSupervisor]
}
