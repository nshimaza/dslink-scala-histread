import sbt._

object Dependencies {
  lazy val dsLink = "org.iot-dsa" % "dslink" % "0.20.1"
//  lazy val json = "net.liftweb" %% "lift-json" % "3.3.0"
  lazy val json = "org.json4s" %% "json4s-native" % "3.6.5"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
}
