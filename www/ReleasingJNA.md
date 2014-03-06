JNA Release Process
===================

* Bump version in CHANGES.md and build.xml. Change JNI version in build.xml *only* if
  JNA's native API has been changed (i.e. md5 changed).  Bump JNA minor version if its Java API
  has changed significantly or incompatibly.

* If native changes have been made, run `ant native` target on each target
  platform, pushing the resulting target-specific jar (in lib/native) file to master.

* Run `ant -Drelease=true clean dist` target on a fully up-to-date checkout with no modifications. Commit generated files in dist.
  You will also see differences in the pom-*.xml files and src/.../Native.java.  Do not commit these files.

* [Release to Maven Central](PublishingToMavenCentral.md)

* Update links in README.md, commit the changes.
  * download links
  * javadoc links

* Update Javadoc
  Check out gh-pages branch, copy latest doc/javadoc into <version>/javadoc,
  commit and push.

* Tag as &lt;version>, push all changes and new tag to origin.

