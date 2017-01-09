name := """eigenroute-authenticated-action"""

version := "0.0.1"
organization := "com.eigenroute"

scalaVersion := "2.11.7"
// resolvers += "Eigenroute maven repo" at "http://mavenrepo.eigenroute.com/"

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "com.pauldijou" %% "jwt-play-json" % "0.9.2",
  "com.typesafe.play" %% "play-json" % "2.5.10",
  "com.typesafe.play" %% "play" % "2.5.10",
  "eigenroute-util" %% "eigenroute-util" % "0.0.1",
  "com.typesafe" % "config" % "1.3.1",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

isSnapshot := true
publishMavenStyle := true
val resolver = Resolver.ssh("Eigenroute maven repo", "mavenrepo.eigenroute.com", 7835, "/home/mavenrepo/repo") withPermissions "0644"
publishTo := Some(resolver as ("mavenrepo", Path.userHome / ".ssh" / "id_rsa"))

publishArtifact in packageSrc := false

publishArtifact in packageDoc := false

