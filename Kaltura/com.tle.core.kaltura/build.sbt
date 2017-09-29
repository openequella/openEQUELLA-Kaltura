lazy val Kaltura = config("kaltura") describedAs("kaltura jars")
lazy val CustomCompile = config("compile") extend Kaltura

libraryDependencies += "com.kaltura" % "kalturaClient" % "3.2.1" % Kaltura

excludeDependencies := Seq(
  "commons-logging" % "commons-logging",
  "commons-httpclient" % "commons-httpclient",
  "commons-codec" % "commons-codec",
  "junit" % "junit-dep",
  "log4j" % "log4j"
)

ivyConfigurations := overrideConfigs(Kaltura, CustomCompile)(ivyConfigurations.value)

jpfLibraryJars := Classpaths.managedJars(Kaltura, Set("jar"), update.value)
