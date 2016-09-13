package com.example.webservice.pingpong

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging

class BasicPingPong extends Actor {
  val log = Logging(context.system, this)
  val validColors = List("red", "blue", "green")

  override def preRestart(reason: Throwable, message: Option[Any]) = {
    log.info("basic pingpong is restarted.")
  }

  def receive: Receive = {
    case PingPongball(hops) => { sender ! PingPongball(hops+1)}
    case Basketball(hops)   => { sender ! Status("Ball too big, I am fainted."); throw new Faint()}
    case Fireball(hops)     => { sender ! Status("Got fire, I am seriously injured."); throw new Injured()}
    case Mustketball(hops)  => { sender ! Status("Shot, I am dead.. :("); throw new Killed()}
    case msg @ _            => throw new Exception(s"recieved unknown message $msg")
  }
}

object BasicPingPong {
  def props: Props = Props[BasicPingPong]
}
