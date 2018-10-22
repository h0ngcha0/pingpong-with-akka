package com.example.webservice.pingpong

import akka.actor.{Actor, ActorLogging, Props}
import java.net.InetAddress

import akka.NotUsed
import akka.persistence.query.{EventEnvelope, PersistenceQuery}
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.stream.scaladsl.Source


class PingPongView extends Actor with ActorLogging {
  private var pingpongBallSeen = 0
  private var basketBallSeen   = 0
  private var fireBallSeen     = 0
  private var bulletSeen       = 0

  private val persistenceId: String = InetAddress.getLocalHost.getHostName

  val queries = PersistenceQuery(context.system).readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)

  val eventSource: Source[EventEnvelope, NotUsed] = queries.eventsByPersistenceId(persistenceId)

  // We can run a reducer and store in some kind of database
  eventSource.map { self ! _.event }

  def buildBallStats: Receive = {
    case PingPongball => pingpongBallSeen = pingpongBallSeen + 1
    case Basketball   => basketBallSeen = basketBallSeen + 1
    case Fireball     => fireBallSeen = fireBallSeen + 1
    case Bullet       => bulletSeen = bulletSeen + 1
  }

  override def receive: Receive = buildBallStats orElse {
    case BallsSeen => sender ! Status(
      s"pingpongball: $pingpongBallSeen; basketball: $basketBallSeen; fireball: $fireBallSeen; bullet: $bulletSeen;"
    )
  }
}

object PingPongView {
  def props: Props = Props[PingPongView]
}
