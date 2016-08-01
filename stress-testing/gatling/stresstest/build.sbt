name := "be.ugent.vopro5.be.ugent.vopro.stresstest"

version := "1.0"

scalaVersion := "2.11.8"

enablePlugins(GatlingPlugin)

libraryDependencies ++= Seq(
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.0" % "test",
  "io.gatling"            % "gatling-test-framework"    % "2.2.0" % "test",
  "com.typesafe.play" % "play-json_2.11" % "2.5.3",
  "com.typesafe.play" % "play-functional_2.11" % "2.5.3"
)

    