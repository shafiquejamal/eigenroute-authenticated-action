name := """eigenroute-authenticated-action"""

version := "0.0.2"
organization := "com.eigenroute"

scalaVersion := "2.11.7"
val resolver = Resolver.ssh("Eigenroute maven repo", "mavenrepo.eigenroute.com", 7835, "/home/mavenrepo/repo") withPermissions "0644"

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "com.pauldijou" %% "jwt-play-json" % "0.9.2",
  "com.typesafe.play" %% "play-json" % "2.5.10",
  "com.typesafe.play" %% "play" % "2.5.10",
  "com.eigenroute" %% "eigenroute-util" % "0.0.2",
  "com.eigenroute" %% "eigenroute-util-test" % "0.0.2" % Test,
  "com.typesafe" % "config" % "1.3.1",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

publishMavenStyle := true
publishTo := Some(resolver as ("mavenrepo", Path.userHome / ".ssh" / "id_rsa"))

publishArtifact in packageSrc := false

publishArtifact in packageDoc := false

