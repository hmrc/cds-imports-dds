resolvers += Resolver.url("HMRC Sbt Plugin Releases", url("https://dl.bintray.com/hmrc/sbt-plugin-releases"))(
  Resolver.ivyStylePatterns)

resolvers += "HMRC Releases" at "https://dl.bintray.com/hmrc/releases"

resolvers += Resolver.typesafeRepo("releases")


addSbtPlugin("uk.gov.hmrc" % "sbt-auto-build" % "2.2.0")

addSbtPlugin("uk.gov.hmrc" % "sbt-git-versioning" % "2.0.0")

addSbtPlugin("uk.gov.hmrc" % "sbt-artifactory" % "1.0.0")

addSbtPlugin("uk.gov.hmrc" % "sbt-distributables" % "1.6.0")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.20")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
