package com.example.webservice.pingpong

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging

class BasicPingPong extends Actor {
  val log = Logging(context.system, this)

  var ballsSeen = 0
  override def preRestart(reason: Throwable, message: Option[Any]) = {
    log.info(s"basic pingpong is restarted because of $reason")
  }

  def receive: Receive = {
    case ball: Ball => {
      ballsSeen += 1
      ball match {
        case PingPongball => sender ! PingPongball
        case Basketball   => sender ! Status("What a big ball, I am fainted..."); throw new Faint()
        case Fireball     => sender ! Status("Got fire, I am severely injured..."); throw new Injured()
        case Bullet       => sender ! Status("Seriouly, a bullet? I am dead... :("); throw new Killed()
      }
    }
    case BallsSeen    => { sender ! Status(s"seen $ballsSeen balls") }
    case msg @ _      => throw new Exception(s"recieved unknown message $msg")
  }


}

object BasicPingPong {
  def props: Props = Props[BasicPingPong]
}
