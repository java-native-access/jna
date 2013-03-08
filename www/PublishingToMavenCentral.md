Publishing JNA to Maven Central
===============================

One Time
--------

* Set up your gpg keys as described [here](https://docs.sonatype.org/display/Repository/How+To+Generate+PGP+Signatures+With+Maven).
* Make sure you have a settings.xml file (in directory: ${user.home}/.m2/) as described at the bottom of 7a1 [here](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide#SonatypeOSSMavenRepositoryUsageGuide-7a.1.POMandsettingsconfig). For example:

        <settings>
        ...
            <servers>
            ...
                <!-- java.net repos for sync to maven central -->
                <server>
                    <id>snapshots.java.net</id>
                    <username>myjavanetuser</username>
                    <password>myjavanetpwd</password>
                </server>
                <server>
                    <id>staging.java.net</id>
                    <username>myjavanetuser</username>
                    <password>myjavanetpwd</password>
                </server>
            ...
            </servers>
        ...
        <settings>

  Because we are still deploying to maven repositories via java.net, see [Java.net Maven Repository Usage Guide](http://java.net/projects/maven2-repository/pages/MigrationAndCleanupRelatedDocumentation) for more info.

Every Time
----------

* Verify the &lt;version> tags in [pom-jna.xml](https://github.com/twall/jna/blob/master/pom-jna.xml) and [pom-platform.xml](https://github.com/twall/jna/blob/master/pom-platform.xml)
  match the version (jna.version) in [build.xml](https://github.com/twall/jna/blob/master/build.xml).
* Run `ant stage`. This uploads current checkout to maven central.
* Follow steps from [release it](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide#SonatypeOSSMavenRepositoryUsageGuide-8a.ReleaseIt)
* Email release notice to [jna-users Google group](http://groups.google.com/group/jna-users).
* After the release is finished, increment the version in build.xml for the next development iteration. Typically, this means increment "jna.revision" by one, and append "-SNAPSHOT" to the "jna.version" property. Create new section in CHANGES.md for 'Next Release (x.y.z)'. Commit and push.


Publish SNAPSHOT - In Between and in preparation for a Release
--------------------------------------------------------------

Before doing a full jna release, we can publish a development SNAPSHOT of the "next" release for people to test. The
SNAPSHOT will be published in the staging repository:

   https://maven.java.net/content/repositories/snapshots/

see: https://maven.java.net/content/repositories/snapshots/net/java/dev/jna/ for the various jars.

To publish a development SNAPSHOT do the following:

    * `git checkout --`
    * `ant deploy -DskipNative=true`

Note: unlike stable, unchanging releases, a SNAPSHOT may be re-published at any time (and it typically deleted after a
full release is performed).

