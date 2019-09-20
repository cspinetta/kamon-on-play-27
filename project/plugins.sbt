resolvers += "Typesafe repository" at "https://dl.bintray.com/typesafe/maven-releases/"
resolvers += Resolver.typesafeRepo("releases")
resolvers += Resolver.bintrayIvyRepo("kamon-io", "sbt-plugins")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.7.0")
addSbtPlugin("io.kamon" % "sbt-kanela-runner-play-2.7" % "2.0.2")
