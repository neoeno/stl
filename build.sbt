name := "stl"

version := "1.0"

scalaVersion := "2.11.8"

// Main
libraryDependencies += "org.apache.poi" % "poi" % "3.14"
libraryDependencies += "org.apache.poi" % "poi-ooxml" % "3.14"

// Test
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"