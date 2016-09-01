package com.example.webservice

import akka.http.scaladsl.Http

object Boot extends App with HttpService {
  val port = config.getInt("http.port")
  val interface = config.getString("http.interface")

  Http().bindAndHandle(routes, interface, port)
}
