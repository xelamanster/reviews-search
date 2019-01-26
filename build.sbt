name := "reviews-search"

version := "0.2"

scalaVersion := "2.12.8"

scalastyleFailOnError := true

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
libraryDependencies += "org.scalamock" %% "scalamock" % "4.1.0" % Test
    