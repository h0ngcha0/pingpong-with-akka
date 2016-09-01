package com.example.webservice.pingpong

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging

class BasicPingPong extends Actor with Protocols {
  val log = Logging(context.system, this)
  val validColors = List("red", "blue", "green")

  override def preRestart(reason: Throwable, message: Option[Any]) = {
    log.info("basic pingpong is restarted.")
  }

  def receive: Receive = {
    case Ping(color) if validColors contains color => sender ! Pong(color)
    case Ping(color) => throw new Exception(s"can not handle $color color")
    case msg @ _     => throw new Exception(s"recieved unknown message $msg")
  }
}

object BasicPingPong {
  def props: Props = Props[BasicPingPong]
}
