import sbt._

object Deps {
  val akkaV       = "2.4.4"
  val scalaTestV  = "2.2.5"
  val scalazV     = "7.1.3"

  val akka = Seq(
    "com.typesafe.akka"          %% "akka-actor"                           % akkaV,
    "com.typesafe.akka"          %% "akka-stream"                          % akkaV,
    "com.typesafe.akka"          %% "akka-http-core"                       % akkaV,
    "com.typesafe.akka"          %% "akka-http-experimental"               % akkaV,
    "com.typesafe.akka"          %% "akka-http-spray-json-experimental"    % akkaV,
    "com.typesafe.akka"          %% "akka-http-testkit"                    % akkaV
  )

  val scalaz = Seq(
    "org.scalaz"                 %% "scalaz-core"           % scalazV,
    "org.scalaz"                 %% "scalaz-effect"         % scalazV
  )

  val logging = Seq(
    "com.typesafe.scala-logging" %% "scala-logging-slf4j"   % "2.1.2",
    "org.slf4j"                  % "log4j-over-slf4j"       % "1.7.21",
    "ch.qos.logback"             % "logback-classic"        % "1.1.7"
  )

  val test = Seq(
    "org.scalaz"                 %% "scalaz-scalacheck-binding"            % scalazV % "test",
    "org.scalatest"              %% "scalatest"                            % scalaTestV % "test",
    "org.mockito"                % "mockito-core" % "1.10.19"              % "test"
  )
}
