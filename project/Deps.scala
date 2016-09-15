import sbt._

object Deps {
  val akkaV       = "2.4.4"
  val scalaTestV  = "2.2.5"

  val akka = Seq(
    "com.typesafe.akka"          %% "akka-actor"                           % akkaV,
    "com.typesafe.akka"          %% "akka-stream"                          % akkaV,
    "com.typesafe.akka"          %% "akka-http-core"                       % akkaV,
    "com.typesafe.akka"          %% "akka-http-experimental"               % akkaV,
    "com.typesafe.akka"          %% "akka-http-spray-json-experimental"    % akkaV,
    "com.typesafe.akka"          %% "akka-cluster"                         % akkaV,
    "com.typesafe.akka"          %% "akka-cluster-tools"                   % akkaV,
    "com.typesafe.akka"          %% "akka-persistence"                     % akkaV,
    "com.typesafe.akka"          %% "akka-http-testkit"                    % akkaV
  )

  val test = Seq(
    "org.scalatest"              %% "scalatest"                            % scalaTestV % "test"
  )
}
