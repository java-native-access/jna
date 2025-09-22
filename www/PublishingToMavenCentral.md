Publishing JNA to Maven Central
===============================

One Time
--------

* Create an account for the maven central repository
  ([Register to Publish Via the Central Portal](https://central.sonatype.org/register/central-portal/))
* Get the account enabled for publishing to the net.java.dev.jna groupId
  (contact an existing uploader for this)
* Set up your gpg keys as described [here](http://central.sonatype.org/pages/working-with-pgp-signatures.html). Make sure you distribute your public key.
* Make sure you have a settings.xml file (in directory: ${user.home}/.m2/).
  For example (Replace *central-user* and *central-password* with the
  credentials you can create via "View Account" -> "Generate User Token"):
  ```xml
  <settings>
  ...
      <servers>
      ...
          <server>
              <id>sonatype-central-portal-snapshots</id>
              <username>central-user</username>
              <password>central-password</password>
          </server>
      ...
      </servers>
  ...
  <settings>
  ```

  These settings are only used for uploading SNAPSHOTs. See:
  [Publishing -SNAPSHOT Releases](https://central.sonatype.org/publish/publish-portal-snapshots/)
  for more info.

Publish Snapshot
----------------

*At any time and in preparation for a release*

Before doing a full jna release, we can publish a development SNAPSHOT of the "next" release for people to test. The
SNAPSHOT will be published in the staging repository:

   [https://central.sonatype.com/repository/maven-snapshots/](https://central.sonatype.com/repository/maven-snapshots/)

To publish a development SNAPSHOT do the following:

        git checkout -- .
        ant clean
        ant deploy

Note: Unlike stable, unchanging releases, a SNAPSHOT may be re-published at any time (and is typically deleted after a
full release is performed).

Publish Release
---------------

* Verify the &lt;version> tags in [pom-jna.xml](https://github.com/java-native-access/jna/blob/master/pom-jna.xml) 
  and [pom-jna-platform.xml](https://github.com/java-native-access/jna/blob/master/pom-jna-platform.xml)
  match the version (jna.version) in [build.xml](https://github.com/java-native-access/jna/blob/master/build.xml).
* Run `ant -Dmaven-release=true stage`. This builds JNA and creates a ZIP file `build/maven-artifacts.zip`.
  That file can be used to deploy to maven central using the manual
  [Publishing By Uploading a Bundle](https://central.sonatype.org/publish/publish-portal-upload/)
  process.
