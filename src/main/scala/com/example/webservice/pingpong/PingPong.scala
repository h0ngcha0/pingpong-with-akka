package com.example.webservice.pingpong

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging

class PingPong extends Actor {
  val log = Logging(context.system, this)

  var ballsSeen = 0

  override def receive: Receive = {
    case PingPongball => ballsSeen = ballsSeen+1; sender ! PingPongball
    case BallsSeen    => sender ! Status(s"seen $ballsSeen balls")
  }

}

object PingPong {
  def props: Props = Props[PingPong]
}
