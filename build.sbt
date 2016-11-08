name := """Arquisoft"""

version := "1.0-SNAPSHOT"
resolvers += Resolver.sonatypeRepo("snapshots")
lazy val root = (project in file(".")).enablePlugins(PlayJava,PlayEbean,LauncherJarPlugin)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.postgresql" % "postgresql" % "9.4-1206-jdbc42",
  "com.feth"      %% "play-authenticate" % "0.8.1-SNAPSHOT",
  "be.objectify"  %% "deadbolt-java"     % "2.5.0"
)


fork in run := false