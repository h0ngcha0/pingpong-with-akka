package com.example.webservice

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.stream.{ActorMaterializer, Materializer}
import com.example.webservice.utils.CorsSupport
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.ExecutionContextExecutor

trait HttpService extends CorsSupport with pingpong.Module
{
  override implicit val system: ActorSystem = ActorSystem()
  override implicit val config: Config = ConfigFactory.load()
  override implicit val executor: ExecutionContextExecutor = system.dispatcher
  override implicit val materializer: Materializer = ActorMaterializer()

  val routes = corsHandler { pingpongEndpoint.routes }
}
