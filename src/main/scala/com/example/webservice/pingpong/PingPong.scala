package com.example.webservice.pingpong

import akka.actor.{Actor, ActorSelection, Address, Props, RootActorPath}
import akka.cluster.{ Cluster, MemberStatus }
import akka.event.Logging
import akka.pattern.ask
import akka.persistence.PersistentActor
import akka.util.Timeout
import java.net.InetAddress

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}


class PingPong extends PersistentActor {
  val log = Logging(context.system, this)

  implicit val executionContext = context.dispatcher
  implicit val askTimeout = Timeout(5.second)

  val cluster = Cluster.get(context.system)
  val hostname = InetAddress.getLocalHost.getHostName

  var ballsSeen = 0
  override def persistenceId: String = hostname
  override def receiveCommand: Receive = {
    case ball: Ball => persist(ball) { _ =>
      ballsSeen += 1
      ball match {
        case PingPongball => sender ! PingPongball
        case Basketball   => sender ! Status("What a big ball, I am fainted..."); throw new Faint()
        case Fireball     => sender ! Status("Got fire, I am severely injured..."); throw new Injured()
        case Bullet       => sender ! Status("Seriouly, a bullet? I am dead... :("); throw new Killed()
      }
    }
    case BallsSeen  => sender ! Status(s"seen $ballsSeen balls", Some(hostname))

    case ToAll(msg) =>
      val s = sender
      sendToAll(msg).onComplete {
        case Success(statuses) => s ! statuses
        case Failure(ex)       => throw ex
      }
  }

  override def receiveRecover: Receive = {
    case ball: Ball => ballsSeen = ballsSeen + 1
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
