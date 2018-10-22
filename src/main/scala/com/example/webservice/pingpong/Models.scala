package com.example.webservice.pingpong

sealed trait Payload
sealed trait Ball extends Payload

case object PingPongball extends Ball
case object Basketball extends Ball
case object Fireball extends Ball
case object Bullet extends Ball

object Ball {
  val all = Set(PingPongball, Basketball, Fireball, Bullet)
  def fromString(str: String): Option[Ball] = {
    all.find(_.productPrefix.toLowerCase == str.toLowerCase)
  }
}

case class Status(status: String, from: Option[String] = None) extends Payload
case object BallsSeen extends Payload
case class ToAll(message: Payload) extends Payload

class Killed extends Exception    // stop
class Faint extends Exception     // resumable
class Injured extends Exception   // recoverable
