scalaVersion := "2.12.6"

val catsEffectVersion = "1.0.0"
val circeVersion = "0.9.3"
val monocleVersion = "1.5.0-cats"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % catsEffectVersion,
  "com.squareup.okhttp3" % "okhttp" % "3.11.0",
  "com.github.julien-truffaut" %%  "monocle-core"  % monocleVersion,
  "com.github.julien-truffaut" %%  "monocle-macro" % monocleVersion,
  "org.scalaz" %% "scalaz-core" % "7.2.26", // for Monocle (even the -cats version has dependencies on scalaz)
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.circe" %% "circe-optics" % circeVersion
)
