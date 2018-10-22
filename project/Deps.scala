import sbt._

object Deps {
  val akkaV           = "2.5.17"
  val akkaHttpVersion = "10.1.5"

  val akka = Seq(
    "com.typesafe.akka"          %% "akka-actor"                           % akkaV,
    "com.typesafe.akka"          %% "akka-stream"                          % akkaV,
    "com.typesafe.akka"          %% "akka-cluster"                         % akkaV,
    "com.typesafe.akka"          %% "akka-cluster-tools"                   % akkaV,
    "com.typesafe.akka"          %% "akka-persistence"                     % akkaV,
    "com.typesafe.akka"          %% "akka-persistence-query"               % akkaV
  )

  val akkaHttp = Seq(
    "com.typesafe.akka"          %% "akka-http"                            % akkaHttpVersion,
    "com.typesafe.akka"          %% "akka-http-testkit"                    % akkaHttpVersion,
    "com.typesafe.akka"          %% "akka-http-core"                       % akkaHttpVersion,
    "com.typesafe.play"          %% "play-json"                            % "2.6.10",
    "de.heikoseeberger"          %% "akka-http-play-json"                  % "1.22.0",
    "tech.minna"                 %% "play-json-macros"                     % "1.0.1"
  )

  val leveldb = Seq(
    "org.iq80.leveldb"           %  "leveldb"                              % "0.7",
    "org.fusesource.leveldbjni"  %  "leveldbjni-all"                       % "1.8"
  )

  val test = Seq(
    "org.scalatest"              %% "scalatest"                            % "3.0.5" % "test"
  )
}
