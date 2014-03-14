name := "Collectd Coda Hale Metrics Reporter"

version := "1.0"

organization := "org.bruceeddy"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
 "org.specs2" %% "specs2" % "1.14" % "test",
 "junit" % "junit" % "4.5" % "test",
 "org.mockito" % "mockito-core" % "1.9.0" % "test"  ,
 "com.codahale.metrics" % "metrics-jvm" % "3.0.1" ,
 "com.codahale.metrics" % "metrics-json" % "3.0.1",
 "com.github.jnr" % "jnr-unixsocket" % "0.3"
)
 


 

