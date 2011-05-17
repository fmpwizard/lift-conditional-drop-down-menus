import sbt._

class LiftProject(info: ProjectInfo) extends DefaultWebProject(info) {
  val liftVersion = "2.3"

  // uncomment the following if you want to use the snapshot repo
   val scalatoolsSnapshot = ScalaToolsSnapshots

  override def compileOptions = super.compileOptions ++ Seq(Unchecked)

  // If you're using JRebel for Lift development, uncomment
  // this line
  // override def scanDirectories = Nil

  System.setProperty("h2.bindAddress", "127.0.0.1")

  override def libraryDependencies = Set(
    "cc.co.scala-reactive" %% "reactive-web" % "0.0.1-SNAPSHOT",
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-json" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-util" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-actor" % liftVersion % "compile->default",
    "org.mortbay.jetty" % "jetty" % "6.1.22" % "test->default",
    "ch.qos.logback" % "logback-classic" % "0.9.26",
    "com.h2database" % "h2" % "1.2.138"
  ) ++ super.libraryDependencies
}
