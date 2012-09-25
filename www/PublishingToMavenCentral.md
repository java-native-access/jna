Publishing JNA to Maven Central
===============================

One Time
--------

* Set up your gpg keys as described [here](https://docs.sonatype.org/display/Repository/How+To+Generate+PGP+Signatures+With+Maven).
* Make sure you have a settings.xml as described at the bottom of 7a1 [here](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide#SonatypeOSSMavenRepositoryUsageGuide-7a.1.POMandsettingsconfig).

Every Time
----------

* Update version numbers in [pom-jna.xml](https://github.com/twall/jna/blob/master/pom-jna.xml) and [pom-platform.xml](https://github.com/twall/jna/blob/master/pom-platform.xml), if needed.
* Run `ant stage`. This uploads current checkout to maven central.
* Follow steps from [release it](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide#SonatypeOSSMavenRepositoryUsageGuide-8a.ReleaseIt)

