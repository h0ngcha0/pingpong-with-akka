package com.example.webservice.pingpong

import akka.actor.{Actor, ActorLogging, Props}

class BasicPingPong extends Actor with ActorLogging {

  var ballsSeen = 0

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info(s"BasicPingPong going to be restarted reason: $reason, message: $message")
  }

  override def receive: Receive = {
    case ball: Ball => {
      ballsSeen += 1
      ball match {
        case PingPongball => sender ! PingPongball
        case Basketball   => sender ! Status("What a big ball, I am fainted..."); throw new Faint()
        case Fireball     => sender ! Status("Got fire, I am severely injured..."); throw new Injured()
        case Bullet       => sender ! Status("Seriouly, a bullet? I am dead... :("); throw new Killed()
      }
    }
    case BallsSeen  => sender ! Status(s"seen $ballsSeen balls")
  }

}

object BasicPingPong {
  def props: Props = Props[BasicPingPong]
}