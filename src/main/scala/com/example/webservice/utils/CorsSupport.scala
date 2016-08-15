package com.example.webservice.utils

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ Directive0, Route }
import com.typesafe.config.ConfigFactory
import scalaz._
import Scalaz._

trait CorsSupport {
  lazy val allowedOriginHeader = {
    val config = ConfigFactory.load()
    val sAllowedOrigin = config.getString("http.allowedOrigin")

    (sAllowedOrigin == "*").option(`Access-Control-Allow-Origin`.*).getOrElse(
      `Access-Control-Allow-Origin`(HttpOrigin(sAllowedOrigin))
    )
  }

  private def addAccessControlHeaders: Directive0 = {
    mapResponseHeaders { headers0 =>
      // filter out the Access-Control-Allow-Origin header if any
      // before adding it
      val headers = headers0.filter { h => h match {
        case h: `Access-Control-Allow-Origin` => false
        case _ => true
      }}
      allowedOriginHeader +:
      `Access-Control-Expose-Headers`("Set-Authorization") +:
      `Access-Control-Allow-Credentials`(true) +:
      `Access-Control-Allow-Headers`(
        "Token", "Content-Type", "X-Requested-With", "If-Modified-Since",
        "X-Session-Token", "Authorization", "Set-Authorization"
      ) +: headers
    }
  }

  private def preflightRequestHandler: Route = options {
    complete(HttpResponse(200).withHeaders(
      `Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE)
    ))
  }

  def corsHandler(r: Route) = addAccessControlHeaders {
    preflightRequestHandler ~ r
  }
}
