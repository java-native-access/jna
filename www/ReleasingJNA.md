JNA Release Process
===================

* Bump version in CHANGES.md and build.xml. Remove the "-SNAPSHOT" suffix from "jna.version" property in build.xml. Change JNI version in build.xml *only* if
  JNA's native API has been changed (i.e. md5 changed).  Bump JNA minor version if its Java API
  has changed significantly or incompatibly.

* If native changes have been made, run `ant native` target on each target
  platform, pushing the resulting target-specific jar (in lib/native) file to master.

* Run `ant clean dist` target on a fully up-to-date checkout with no modifications.  Commit and push generated files in dist, except for a platform specific jar: "&lt;os>-&lt;arch>.jar".
  You will also see differences in the pom-*.xml files and src/.../Native.java. Commit and push these also.

* Update Javadoc
  Check out gh-pages branch, copy latest doc/javadoc into <version>/javadoc,
  commit and push.

* Update README.md, commit and push
  * download links
  * javadoc links

* [Release to Maven Central](https://github.com/twall/jna/blob/master/www/PublishingToMavenCentral.md)

* Tag as &lt;version>, push new tag to origin

