JNA Release Process
===================

* Bump version in CHANGES.md and build.xml, if not already.
  * Change JNI version in build.xml *only* if JNA's native API has been changed (i.e. md5 changed).
  * Bump JNA minor version if its Java API has changed significantly or incompatibly.

* If native changes have been made, run `ant native` target on each target
  platform, pushing the resulting target-specific jar (in lib/native) file to master.

* Run `ant -Drelease=true clean dist` target on a fully up-to-date checkout with no modifications.

* Update versioned links in `README.md` (search for old version and replace with new version)

* Update `CHANGES.md`: remove the `Next release` label and replace it with the final version number. While doing this also check if the version number matches the release: major version should incremented when API incompatible changes are made, minor version should be incremented when features are added, revision should be updated when bugfixes are done.

* Commit and push generated files in `dist`, `pom-*.xml`, `CHANGES.md`, `README.md` and `src/com/sun/jna/Version.java`.

* Tag
  * Tag using the new version number (e.g. `git tag 4.2.1`)
  * Push new tag to origin (`git push --tags`)

* Update Javadoc
  * Check out gh-pages branch
  * Copy latest doc/javadoc into {version}/javadoc
  * Commit and push

* [Release to Maven Central](PublishingToMavenCentral.md)

* Email release notice to [jna-users Google group](http://groups.google.com/group/jna-users).

* Increment the version in build.xml for the next development iteration
  * Increment "jna.revision" in build.xml by one
  * Create a new section in CHANGES.md for 'Next Release (x.y.z)'
  * Commit and push

