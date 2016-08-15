package com.example.webservice.pingpong

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging

class BasicPingPong extends Actor with Protocols {
  val log = Logging(context.system, this)

  def receive = {
    case Ping(color) => sender ! Pong(color)
    case msg @ _     => log.info("recieved unknown message {}", msg)
  }
}

object BasicPingPong {
  def props: Props = Props[BasicPingPong]
}
