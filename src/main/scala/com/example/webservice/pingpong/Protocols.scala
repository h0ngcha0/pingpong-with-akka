package com.example.webservice.pingpong

import spray.json._

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
      case PingPongball        => JsObject("type"   -> JsString("pingpongball"))
      case Basketball          => JsObject("type"   -> JsString("basketball"))
      case Fireball            => JsObject("type"   -> JsString("fireball"))
      case Bullet              => JsObject("type"   -> JsString("bullet"))
      case Status(label, from) => from match {
        case None         => JsObject("status" -> JsString(label))
        case Some(origin) => JsObject(
          "status" -> JsString(label),
          "from" -> JsString(origin)
        )
      }
    }
  }
}
