package com.example.webservice.pingpong

import spray.json._

trait Payload
trait Ball extends Payload
case object PingPongball extends Ball
case object Basketball extends Ball
case object Fireball extends Ball
case object Bullet extends Ball
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
        case ("type",   JsString("pingpongball"))  => PingPongball
        case ("type",   JsString("basketball"))    => Basketball
        case ("type",   JsString("fireball"))      => Fireball
        case ("type",   JsString("bullet"))        => Bullet
        case ("status", JsString(label))           => Status(label)
        case _ => deserializationError("expecting (kind, JsString), got " + json)
      }
      case _ => deserializationError("expecting JsString, got " + json)
    }

    def write(payload: Payload): JsValue = payload match {
      case PingPongball  => JsObject("type"   -> JsString("pingpongball"))
      case Basketball    => JsObject("type"   -> JsString("basketball"))
      case Fireball      => JsObject("type"   -> JsString("fireball"))
      case Bullet        => JsObject("type"   -> JsString("bullet"))
      case Status(label) => JsObject("status" -> JsString(label))
    }
  }
}
