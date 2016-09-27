package com.example.webservice.pingpong

import akka.actor.{Actor, ActorSelection, Address, Props, RootActorPath}
import akka.cluster.{ Cluster, MemberStatus }
import akka.cluster.ClusterEvent._
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class ClusteredPingPong extends Actor {
  val log = Logging(context.system, this)

  implicit val executionContext = context.dispatcher
  implicit val askTimeout = Timeout(5.second)

  val cluster = Cluster.get(context.system)

  override def preStart() = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent])
  }

  var ballsSeen = 0

  def receive: Receive = {
    case PingPongball(hops) => ballsSeen += 1; sender ! PingPongball(hops+1)
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
}

object ClusteredPingPong {
  def props: Props = Props[ClusteredPingPong]
}
