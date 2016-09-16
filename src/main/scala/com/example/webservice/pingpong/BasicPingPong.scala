package com.example.webservice.pingpong

import akka.actor.{Actor, ActorSelection, Address, Props, RootActorPath}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class BasicPingPong extends Actor {
  val log = Logging(context.system, this)

  implicit val executionContext = context.dispatcher
  implicit val askTimeout = Timeout(5.second)

  val cluster = Cluster.get(context.system)

  override def preStart() = {
    cluster.join(Address("akka.tcp", "MySystem", "seed", 2551))
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent])
  }

  override def preRestart(reason: Throwable, message: Option[Any]) = {
    log.info(s"basic pingpong is restarted because of $reason")
  }

  var ballsSeen = 0

  def balls: Receive = {
    case PingPongball(hops) => ballsSeen += 1; sender ! PingPongball(hops+1)
    case BallsSeen          => sender ! Status(s"seen $ballsSeen balls")

    case BallsSeenAll       =>
      val responses = nodes.map { addr =>
        val nodePath = RootActorPath(addr)
        val actor = context.actorSelection(nodePath / "user" / "BasicPingPong")
        log.info(s"sending to $actor")
        (actor ? BallsSeen).mapTo[Status]
      }

      Future.sequence(responses).onComplete {
        case Success(statuses) => sender ! Status(statuses map (_.status) mkString "\n")
        case Failure(ex)       => sender ! Status(ex.toString)
      }

  }

  var nodes = Set.empty[Address]

  def members: Receive = {
    case MemberUp(member)         => nodes += member.address; log.info("member up: {} -- {}", member, nodes)
    case MemberRemoved(member, _) => nodes -= member.address; log.info("member down: {} -- {}", member, nodes)
    case _: MemberEvent           => // ignore
  }

  def receive = balls orElse members

}

object BasicPingPong {
  def props: Props = Props[BasicPingPong]
}
