package com.example.webservice.pingpong

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import java.net.InetAddress

class PingPong extends Actor {
  val log = Logging(context.system, this)

  val hostname = InetAddress.getLocalHost.getHostName

  var ballsSeen = 0
  override def preRestart(reason: Throwable, message: Option[Any]) = {
    log.info(s"pingpong is restarted because of $reason")
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
    case BallsSeen  => sender ! Status(s"seen $ballsSeen balls", Some(hostname))
  }


}

object PingPong {
  def props: Props = Props[PingPong]
}
