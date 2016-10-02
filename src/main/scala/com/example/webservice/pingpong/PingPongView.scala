package com.example.webservice.pingpong

import akka.actor.{ ActorLogging, Props }
import akka.persistence.PersistentView
import java.net.InetAddress


class PingPongView extends PersistentView with ActorLogging {
  var pingpongBallSeen = 0
  var basketBallSeen   = 0
  var fireBallSeen     = 0
  var bulletSeen       = 0

  override def persistenceId: String = InetAddress.getLocalHost.getHostName
  override def viewId: String = s"$persistenceId-view"

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
