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
    case PingPongball(hops) => { ballsSeen += 1; sender ! PingPongball(hops+1)}
    case Basketball(hops)   => { ballsSeen += 1; sender ! Status("What a big ball, I am fainted..."); throw new Faint()}
    case Fireball(hops)     => { ballsSeen += 1; sender ! Status("Got fire, I am severely injured..."); throw new Injured()}
    case Mustketball(hops)  => { ballsSeen += 1; sender ! Status("Seriouly, a bullet? I am dead... :("); throw new Killed()}
    case BallsSeen          => { println(s"got balls seen"); sender ! Status(s"seen $ballsSeen balls") }
    case msg @ _            => throw new Exception(s"recieved unknown message $msg")
  }
}

object BasicPingPong {
  def props: Props = Props[BasicPingPong]
}
