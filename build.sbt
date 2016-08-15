name         := """akka-ping-pong"""
organization := "com.example"
version      := "1.0.0"
scalaVersion := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  Seq("com.softwaremill.macwire" %% "macros" % "2.1.0" % "provided") ++
  Deps.akka ++
  Deps.scalaz ++
  Deps.logging ++
  Deps.test
}

initialCommands in console := "import scalaz._, Scalaz._"
