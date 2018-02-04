javasnack
=========

Tinny Java Excersise, Experimental, Practices Programms.

## how to build and execute main()

requires:
- Java8 SDK

### 1. build myapi-impl(1|2).jar, "GreetingInterface" implementation

**(You can skip this step : already pre-built jar has been commited to src/main/resources/JCLDemo/)**

```
$ cd javasnack/

$ pushd subprojects/myapi-impl1
$ javac src/myapi/*.java
$ jar cvf myapi-impl1.jar -C src  .
$ cp myapi-impl1.jar ../../src/main/resources/JCLDemo/
$ popd

$ pushd subprojects/myapi-impl2
$ javac src/myapi/*.java
$ jar cvf myapi-impl2.jar -C src  .
$ cp myapi-impl2.jar ../../src/main/resources/JCLDemo/
$ popd
```

These jar files are used for demonstration of https://github.com/kamranzafar/JCL at [JCLDemo](src/main/java/javasnack/snacks/JCLDemo.java), [TestJCLDemoApis](src/test/java/javasnack/langspecs/TestJCLDemoApis.java).

### 2. build testjar1-1.0.jar

**(You can skip this step : already pre-built jar has been commited to repo/subprojects/)**

build jar file:
```
$ cd javasnack/subprojects/jar1
$ javac src/testjar1/*.java
$ jar cvfm testjar1-1.0.jar manifest.mf -C src/ testjar1/Greeting.class
$ java -jar testjar1-1.0.jar
Grood Morning, ABC.
Good Afternoon, DEF.
```

install jar to project local repository:
```
$ cd javasnack/
$ mvn install:install-file \
      -DlocalRepositoryPath=subprojects/repo \
      -DcreateChecksum=true \
      -Dpackaging=jar \
      -Dfile=subprojects/jar1/testjar1-1.0.jar \
      -DgroupId=subprojects \
      -DartifactId=testjar1 \
      -Dversion=1.0
```

This jar file are used for demonstration of pom.xml local repository usage at [pom.xml](pom.xml) and [LocalJarDemo](src/main/java/javasnack/snacks/LocalJarDemo.java).

see:
- java - Can I add jars to maven 2 build classpath without installing them? - Stack Overflow
  - http://stackoverflow.com/questions/364114/can-i-add-jars-to-maven-2-build-classpath-without-installing-them/
- Mavenプロジェクトで3rdパーティJARを扱う方法｜Ouobpo
  - http://ameblo.jp/ouobpo/entry-10051976866.html

### 3. `$HOME/.m2/setting.xml` mirror setting for local repository setup

This maven project includes demonstrating maven local file repository.
Check your `$HOME/.m2/setting.xml` and if `<mirror>` - `<mirrorOf>` setting is `*`, then fix it to `external:*`.

```
  <mirrors>
    <mirror>
      <id>nexus</id>
      <mirrorOf>*</mirrorOf>
      <url>http://your.mirroring.repository/nexus/content/groups/public/</url>
    </mirror>
  </mirrors>
```
to:
```
...
      <mirrorOf>external:*</mirrorOf>
...
```

If `<mirrorOf>` is `*`, then maven searches all dependencies to `<url>` repository, so, local file dependencies couldn't be resolved.

About `<mirrorOf>` setting, see :
- http://maven.apache.org/guides/mini/guide-mirror-settings.html
- http://stackoverflow.com/questions/17019308/maven-setup-another-repository-for-certain-dependency

### 4. build main project

```
$ cd javasnack/

$ ./mvnw package

$ java -jar target/javasnack-1.0-SNAPSHOT.jar
or
$ mvnw exec:java
```

## for Developers setup (Eclipse)

- JDK >= 1.8.0_xx
- Eclipse >= 4.5.2 (Mars.2 Release), "Eclipse IDE for Java EE Developers" package
- Maven >= 3.5.2 (download automatically by mvnw script. also works by 3.x)
- use UTF-8 for source code and other text files.

See https://github.com/msakamoto-sf/howto-eclipse-setup : README.md.
Refer following `setup-type1` setting:
- Eclipse install how-to
- Clean Up/Formatter configuration
- Required plugin : TestNG

