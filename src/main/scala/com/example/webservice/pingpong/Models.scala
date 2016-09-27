package com.example.webservice.pingpong

import spray.json._

trait Payload
trait Ball extends Payload
case object PingPongball extends Ball
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
        case ("status", JsString(label))           => Status(label)
        case _ => deserializationError("expecting (kind, JsString), got " + json)
      }
      case _ => deserializationError("expecting JsString, got " + json)
    }

    def write(payload: Payload): JsValue = payload match {
      case PingPongball  => JsObject("type"   -> JsString("pingpongball"))
      case Status(label) => JsObject("status" -> JsString(label))
    }
  }
}
