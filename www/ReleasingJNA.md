JNA Release Process
===================

* Bump version in CHANGES.md and build.xml, if not already.
  * Change JNI version in build.xml *only* if JNA's native API has been changed (i.e. md5 changed).
  * Bump JNA minor version if its Java API has changed significantly or incompatibly.

* If native changes have been made, run `ant native` target on each target
  platform, pushing the resulting target-specific jar (in lib/native) file to master.

* Ensure the git repository is in a clean state (e.g. run `git clean -f -x -d`)

* Update versioned links in `README.md` (search for old version and replace with new version)

* Update `CHANGES.md`: remove the `Next release` label and replace it with the final version number. While doing this also check if the version number matches the release: major version should incremented when API incompatible changes are made, minor version should be incremented when features are added, revision should be updated when bugfixes are done.

* Run `ant -Drelease=true -Dmaven-release=true clean dist stage`

* Commit and push generated files in `dist`, `CHANGES.md` and `README.md`.

* Login to https://oss.sonatype.org and release the maven artifacts

* Tag
  * Tag using the new version number (e.g. `git tag 4.2.1`)
  * Push new tag to origin (`git push --tags`)

* Update Javadoc
  * Check out gh-pages branch
  * Copy latest doc/javadoc into {version}/javadoc
  * Commit and push

* Email release notice to [jna-users Google group](http://groups.google.com/group/jna-users).

* Increment the version in common.xml for the next development iteration
  * Increment "jna.minor" in common.xml by one
  * Create a new section in CHANGES.md for 'Next Release (x.y.z)'
  * Commit and push

For more nformation about the maven central release process see:
[Release to Maven Central](PublishingToMavenCentral.md)