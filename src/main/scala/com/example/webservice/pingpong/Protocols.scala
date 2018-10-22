package com.example.webservice.pingpong

import play.api.libs.json._


trait Protocols {
  /*implicit val payloadFormat: RootJsonFormat[Payload] = new RootJsonFormat[Payload] {
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
  }*/

  implicit val payloadFormat: Format[Payload] = {
    val reads = {
      val ballReads: Reads[Payload] = (JsPath \ "type").read[String].map {
        case ballString if Ball.fromString(ballString).isDefined =>
          Ball.fromString(ballString).get
      }

      val statusReads: Reads[Payload] = (JsPath \ "status").read[String].map {
        case label => Status(label)
      }

      ballReads orElse statusReads orElse {
        case unexpected =>
          throw new RuntimeException(s"Recieved unexpected: $unexpected")
      }
    }

    val writes: Writes[Payload] = Writes[Payload] {
      case PingPongball        => JsObject(Map("type"   -> JsString("pingpongball")))
      case Basketball          => JsObject(Map("type"   -> JsString("basketball")))
      case Fireball            => JsObject(Map("type"   -> JsString("fireball")))
      case Bullet              => JsObject(Map("type"   -> JsString("bullet")))
      case Status(label, from) => from match {
        case None         => JsObject(Map("status" -> JsString(label)))
        case Some(origin) => JsObject(Map(
          "status" -> JsString(label),
          "from" -> JsString(origin)
        ))
      }
      case rest @ (_: ToAll | BallsSeen) =>
        throw new RuntimeException(s"Should not write ${rest.getClass.getSimpleName}")
    }

    Format(reads, writes)
  }
}
