package com.example.webservice.pingpong

import spray.json.DefaultJsonProtocol
import scala.util.Try

case class Ping(color: String)
case class Pong(color: String, `type`: String = "pong")

trait Protocols extends DefaultJsonProtocol {
  implicit val pingFormat = jsonFormat1(Ping.apply)
  implicit val pongFormat = jsonFormat2(Pong.apply)
}
