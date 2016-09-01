package com.example.webservice.pingpong

import akka.actor.Actor
import akka.actor.Props
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy._
import akka.event.Logging

class SupervisedPingPong extends Actor with Protocols {
  val log = Logging(context.system, this)

  override val supervisorStrategy = OneForOneStrategy() {
    case _: Any => Restart
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
