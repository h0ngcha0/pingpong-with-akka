name         := """akka-ping-pong"""
organization := "com.example"
version      := "1.0.0"
scalaVersion := "2.12.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  Seq(
    "com.softwaremill.macwire" %% "macros" % "2.3.1" % "provided"
  ) ++
  Deps.akka ++
  Deps.akkaHttp ++
  Deps.leveldb ++
  Deps.test
}

resolvers ++= Seq(Resolver.bintrayRepo("minna-technologies", "maven"))
