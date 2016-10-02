package com.example.webservice.pingpong

import akka.actor.{Actor, ActorSelection, Address, Props, RootActorPath}
import akka.cluster.{ Cluster, MemberStatus }
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import java.net.InetAddress

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}


class PingPong extends Actor {
  val log = Logging(context.system, this)

  implicit val executionContext = context.dispatcher
  implicit val askTimeout = Timeout(5.second)

  val cluster = Cluster.get(context.system)
  val hostname = InetAddress.getLocalHost.getHostName

  var ballsSeen = 0

  override def receive: Receive = {
    case msg : Ball => ballsSeen += 1; sender ! msg
    case BallsSeen  => sender ! Status(s"seen $ballsSeen balls", Some(hostname))

    case ToAll(msg) =>
      val s = sender
      sendToAll(msg).onComplete {
        case Success(statuses) => s ! statuses
        case Failure(ex)       => throw ex
      }
  }

  private def sendToAll(msg: Payload): Future[List[Payload]] = {
    val nodeAddrs = cluster.state.members
      .filter(_.status == MemberStatus.Up)
      .map(_.address)
      .toList

    val responses = nodeAddrs.map { addr =>
      val nodePath = RootActorPath(addr)
      val actor = context.actorSelection(nodePath / "user" / "PingPong")
      log.info(s"sending to $actor")
      (actor ? msg).mapTo[Payload]
    }

    Future.sequence(responses)
  }
}

object PingPong {
  def props: Props = Props[PingPong]
}
