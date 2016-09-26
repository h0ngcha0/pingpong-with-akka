package com.example.webservice.pingpong

import spray.json._

trait Payload
trait Ball extends Payload
case class PingPongball(hops: Int = 0) extends Ball
case class Basketball(hops: Int = 0) extends Ball
case class Fireball(hops: Int = 0) extends Ball
case class Mustketball(hops: Int = 0) extends Ball
case class Status(status: String) extends Payload
case object BallsSeen
case object BallsSeenAll

class Killed extends Exception    // stop
class Faint extends Exception     // resumable
class Injured extends Exception   // recoverable

trait Protocols extends DefaultJsonProtocol {
  implicit val payloadFormat: RootJsonFormat[Payload] = new RootJsonFormat[Payload] {
    def read(json: JsValue): Payload = json match {
      case obj: JsObject => obj.fields.head match {
        case ("pingpongball", JsNumber(hops))  => PingPongball(hops.toInt)
        case ("basketball",   JsNumber(hops))  => Basketball(hops.toInt)
        case ("fireball",     JsNumber(hops))  => Fireball(hops.toInt)
        case ("mustketball",  JsNumber(hops))  => Mustketball(hops.toInt)
        case ("status",       JsString(label)) => Status(label)
        case _ => deserializationError("expecting (kind, JsString), got " + json)
      }
      case _ => deserializationError("expecting JsString, got " + json)
    }

    def write(payload: Payload): JsValue = payload match {
      case PingPongball(hops) => JsObject("pingpongball" -> JsNumber(hops))
      case Basketball(hops)   => JsObject("basketball"   -> JsNumber(hops))
      case Fireball(hops)     => JsObject("fireball"     -> JsNumber(hops))
      case Mustketball(hops)  => JsObject("mustketball"  -> JsNumber(hops))
      case Status(label)      => JsObject("status"       -> JsString(label))
    }
  }
}
