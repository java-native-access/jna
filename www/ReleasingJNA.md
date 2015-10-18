JNA Release Process
===================

* Bump version in CHANGES.md and build.xml, if not already. 
  * Change JNI version in build.xml *only* if JNA's native API has been changed (i.e. md5 changed).  
  * Bump JNA minor version if its Java API has changed significantly or incompatibly.

* If native changes have been made, run `ant native` target on each target
  platform, pushing the resulting target-specific jar (in lib/native) file to master.

* Run `ant -Drelease=true clean dist` target on a fully up-to-date checkout with no modifications. Commit generated files in `dist`.
  * You will also see differences in the pom-*.xml files and src/.../Native.java.  Do not commit these files.

* Update Javadoc
  * Check out gh-pages branch
  * Copy latest doc/javadoc into {version}/javadoc
  * Commit and push

* Update versioned links
  * Switch back to master branch
  * Update links in README.md (download and javadoc)
  * Commit and push

* Tag
  * Tag using the new version number (e.g. `git tag 4.2.1`)
  * Push new tag to origin (`git push --tags`)

* [Release to Maven Central](PublishingToMavenCentral.md)

* Email release notice to [jna-users Google group](http://groups.google.com/group/jna-users).

* Increment the version in build.xml for the next development iteration
  * Increment "jna.revision" in build.xml by one
  * Create a new section in CHANGES.md for 'Next Release (x.y.z)'
  * Commit and push

