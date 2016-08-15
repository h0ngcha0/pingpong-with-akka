package com.example.webservice.pingpong

import akka.actor.ActorSystem
import scala.concurrent.{ExecutionContextExecutor, Future}
import com.typesafe.config.Config
import akka.stream.Materializer

trait Module {
  import com.softwaremill.macwire._

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer
  implicit def config: Config

  lazy val pingpongEndpoint = wire[Endpoint]
}
