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


class PersistentClusteredPingPong extends PersistentActor {
  val log = Logging(context.system, this)

  implicit val executionContext = context.dispatcher
  implicit val askTimeout = Timeout(5.second)

  val cluster = Cluster.get(context.system)

  var ballsSeen = 0
  override def persistenceId: String = InetAddress.getLocalHost.getHostName
  override def receiveCommand: Receive = {
    case msg @ PingPongball(hops) => persist(msg) { m =>
      ballsSeen += 1; sender ! PingPongball(hops+1)
    }
    case BallsSeen          => sender ! Status(s"seen $ballsSeen balls")
    case BallsSeenAll       =>
      val nodeAddrs = cluster.state.members.filter(_.status == MemberStatus.Up).map(_.address).toSeq
      val responses = nodeAddrs.map { addr =>
        val nodePath = RootActorPath(addr)
        val actor = context.actorSelection(nodePath / "user" / "ClusteredPingPong")
        log.info(s"sending to $actor")
        (actor ? BallsSeen).mapTo[Status]
      }

      val s = sender
      Future.sequence(responses).onComplete {
        case Success(statuses) => s ! Status(statuses map (_.status) mkString "\n")
        case Failure(ex)       => s ! Status(ex.toString)
      }

  }

  override def receiveRecover: Receive = {
    case event: PingPongball =>
      ballsSeen = ballsSeen + 1
      log.info("Replayed {}", event.getClass.getSimpleName)
  }

}

object PersistentClusteredPingPong {
  def props: Props = Props[PersistentClusteredPingPong]
}
