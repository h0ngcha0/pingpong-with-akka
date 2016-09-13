package com.example.webservice

import scala.concurrent.ExecutionContextExecutor

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.{Config, ConfigFactory}

trait HttpService extends pingpong.Module
{
  override implicit val system: ActorSystem = ActorSystem()
  override implicit val config: Config = ConfigFactory.load()
  override implicit val executor: ExecutionContextExecutor = system.dispatcher
  override implicit val materializer: Materializer = ActorMaterializer()

  val routes = pingpongEndpoint.routes
}
