name := "lambda-s3-dynamodb"

version := "1.0"

scalaVersion := "2.12.1"

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.125",
  "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.11.125",
  "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",
  "com.amazonaws" % "aws-lambda-java-events" % "1.3.0",
  "com.typesafe" % "config" % "1.3.1"
  //"org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

// Uncomment to use Akka
//libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.11"

//unmanagedClasspath in Runtime += baseDirectory.value / "conf"
