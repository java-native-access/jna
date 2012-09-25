JNA Release Process
===================

* Bump version in CHANGES.md and build.xml.  Change JNI version in build.xml *only* if
  JNA's native API has been changed (i.e. md5 changed).  Bump JNA minor version if its Java API
  has changed significantly or incompatibly.

* If native changes have been made, run `ant dist` target on each target
  platform, pushing the resulting target-specific jar file to master.

* Run `ant dist` target.  Commit and push generated jar files in dist.

* Update Javadoc
  Check out gh-pages branch, copy latest doc/javadoc into <version>/javadoc,
  commit and push.

* Update README.md, commit and push
  * download links
  * javadoc links

* Tag as <version>, push new tag to origin

* [Release to Maven Central](https://github.com/twall/jna/blob/master/www/PublishingToMavenCentral.md)
