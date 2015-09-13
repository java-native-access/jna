Publishing JNA to Maven Central
===============================

One Time
--------

* Set up your gpg keys as described [here](http://central.sonatype.org/pages/working-with-pgp-signatures.html). Make sure you distribute your public key.
* Make sure you have a settings.xml file (in directory: ${user.home}/.m2/). For example:

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

Publish Snapshot
----------------

*At any time and in preparation for a release*

Before doing a full jna release, we can publish a development SNAPSHOT of the "next" release for people to test. The
SNAPSHOT will be published in the staging repository:

   https://maven.java.net/content/repositories/snapshots/

see: https://maven.java.net/content/repositories/snapshots/net/java/dev/jna/ for the various jars.

To publish a development SNAPSHOT do the following:

        git checkout -- .
        ant deploy

Note: Unlike stable, unchanging releases, a SNAPSHOT may be re-published at any time (and is typically deleted after a
full release is performed).

Publish Release
---------------

* Verify the &lt;version> tags in [pom-jna.xml](https://github.com/java-native-access/jna/blob/master/pom-jna.xml) and [pom-jna-platform.xml](https://github.com/java-native-access/jna/blob/master/pom-jna-platform.xml)
  match the version (jna.version) in [build.xml](https://github.com/java-native-access/jna/blob/master/build.xml).
* Run `ant -Dmaven-release=true stage`. This uploads current checkout to [maven.java.net](https://maven.java.net).
* Follow steps from [Releasing the Deployment](http://central.sonatype.org/pages/releasing-the-deployment.html).
  Note that the releases are managed from [maven.java.net](https://maven.java.net).


