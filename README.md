# openEquella-Kaltura Plugin

The Kaltura dependencies are not licensed in a way that is appropriate to publish with the core Equella repo.  However, the functionality exists to integrate openEQUELLA with Kaltura, and is appropriate for a user to build into their install of openEQUELLA.

## Build
Before running _sbt compile_ on the core Equella code base, do the following
1. git clone the [openEquella-Kaltura](https://github.com/equella/Equella-Kaltura) repo.
1. Copy the Kaltura directory from the {Equella-Kaltura} cloned directory into your Equella Plugins into the core Equella Plugins directory {Equella-core-repo}/Source/Plugins

Proceed with the normal build process for openEquella.  Kaltura integration will be enabled.
