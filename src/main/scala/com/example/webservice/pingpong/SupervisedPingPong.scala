package com.example.webservice.pingpong

import akka.actor.{Props, OneForOneStrategy, Actor}
import akka.actor.SupervisorStrategy._
import akka.event.Logging

class SupervisedPingPong extends Actor {
  val log = Logging(context.system, this)

  // a few strategies
  override val supervisorStrategy = OneForOneStrategy() {
    case _: Killed  => Stop
    case _: Faint   => Resume
    case _: Injured => Restart
  }

  val basicPingPong = context.actorOf(BasicPingPong.props, "BasicPingPong")

  def receive = {
    case msg @ _  => {
      log.info("forwarding message {} to child.", msg)
      basicPingPong forward msg
    }
  }
}

object SupervisedPingPong {
  def props: Props = Props[SupervisedPingPong]
}
