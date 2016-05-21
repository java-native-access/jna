Publishing JNA to Maven Central
===============================

One Time
--------

* Create an account in the [sonatype jira](https://issues.sonatype.org/secure/Signup!default.jspa)
* Get the account enabled for publishing to the net.java.dev.jna groupId
  (contact an existing uploader for this)
* Set up your gpg keys as described [here](http://central.sonatype.org/pages/working-with-pgp-signatures.html). Make sure you distribute your public key.
* Make sure you have a settings.xml file (in directory: ${user.home}/.m2/).
  For example (Replace *myossrhuser* and *myossrhpwd* with the account credentials):

        <settings>
        ...
            <servers>
            ...
                <server>
                    <id>oss.sonatype.org</id>
                    <username>myossrhuser</username>
                    <password>myossrhpwd</password>
                </server>
            ...
            </servers>
        ...
        <settings>

  The binaries are hosted in the Sonatype OSSRH (OSS Repository Hosting) system,
  and mirrored from there to maven central.

  See [OSSRH Guide](http://central.sonatype.org/pages/ossrh-guide.html) for more info.

Publish Snapshot
----------------

*At any time and in preparation for a release*

Before doing a full jna release, we can publish a development SNAPSHOT of the "next" release for people to test. The
SNAPSHOT will be published in the staging repository:

   [https://oss.sonatype.org/content/repositories/snapshots/](https://oss.sonatype.org/content/repositories/snapshots/)

see: [https://oss.sonatype.org/content/repositories/snapshots/net/java/dev/jna/](https://oss.sonatype.org/content/repositories/snapshots/net/java/dev/jna/) for the various jars.

To publish a development SNAPSHOT do the following:

        git checkout -- .
        ant deploy

Note: Unlike stable, unchanging releases, a SNAPSHOT may be re-published at any time (and is typically deleted after a
full release is performed).

Publish Release
---------------

* Verify the &lt;version> tags in [pom-jna.xml](https://github.com/java-native-access/jna/blob/master/pom-jna.xml) 
  and [pom-jna-platform.xml](https://github.com/java-native-access/jna/blob/master/pom-jna-platform.xml)
  match the version (jna.version) in [build.xml](https://github.com/java-native-access/jna/blob/master/build.xml).
* Run `ant -Dmaven-release=true stage`. This uploads current checkout to [oss.sonatype.org](https://oss.sonatype.org).
* Follow steps from [Releasing the Deployment](http://central.sonatype.org/pages/releasing-the-deployment.html).