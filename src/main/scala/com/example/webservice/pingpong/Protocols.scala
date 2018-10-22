package com.example.webservice.pingpong

import play.api.libs.json._

object Protocols {
  implicit val ballReads: Reads[Ball] = (JsPath \ "type").read[String].map { ballString =>
    Ball.fromString(ballString).getOrElse {
      throw new RuntimeException(s"Not a valid ball: $ballString")
    }
  }

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
