javasnack
=========

Tinny Java Excersise, Experimental, Practices Programms.

## $ setup development env

base requirements:

- OpenJDK >= 21
- Maven >= 3.9.9 (download automatically by mvnw script. also works by 3.9.x)
- use UTF-8 for source code and other text files.

### $$ setup command line build env

1. install JDK (>= JDK 21)
   - i.e. Temurin JDK : https://adoptium.net/temurin/releases
2. install Apache Maven (>= 3.9.9)
   - https://maven.apache.org/

### $$ setup eclipse env

1. install "Eclipse IDE for Enterprise Java and Web Developers" package
   - https://www.eclipse.org/downloads/packages/release/2025-06/r/eclipse-ide-enterprise-java-and-web-developers
   - recommended version: >= 2025-06 (4.36.x)
2. add JDK (installed at command line build env setup) to Eclipse
   - `Window -> Preferences -> [Java] -> [Installed JREs] -> Add -> Standard VM`
3. [setup eclipse workspace environments](./config-files-eclipse/eclipse-workspace-prefs-ja.md)
4. add lombok plugin
   1. download lombok jar from: https://projectlombok.org/
   2. `java -jar lombok.jar`
   3. select eclipse installed directory and install lombok
   4. restart eclipse
5. import this project
   1. `File -> Import`
   2. `Maven -> Existing Maven Projects`
   3. `Root Directory` : select project root directory, import pom.xml.
6. install linter plugins, then import configuration files:
   1. [Checkstyle](./config-files-linter/checkstyle-00-README.md)
   2. [PMD](./config-files-linter/pmd-00-README.md)
   3. [SpotBugs](./config-files-linter/spotbugs-00-README.md)

## $ how to build and execute main()

javasnack demonstrates classloading from jar in resources and referencing project related local repository.

1. build jar library and install to src/main/resources/.
2. build jar library and deploy to project related path. (NOT to `~/.m2/repository/`)
3. setup `~/.m2/settings.xml` to refer project related path.
4. build main project.

### $$ 1. build jar library and install to src/main/resources/.

**(You can skip this step : already pre-built jar has been commited to src/main/resources/JCLDemo/)**

build myapi-impl(1|2).jar, "GreetingInterface" implementation:
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

### $$ 2. build jar library and deploy to project related path. (NOT to `~/.m2/repository/`)

**(You can skip this step : already pre-built jar has been commited to repo/subprojects/)**

build testjar1-1.0.jar:
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
$ ./mvnw install:install-file \
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

### $$ 3. setup `~/.m2/settings.xml` to refer project related path.

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

### $$ 4. build main project.

```
$ cd javasnack/

$ ./mvnw package

if you want to skip test running, but compile test code:
$ ./mvnw -DskipTests package

if you want to skip test compile and running either:
$ ./mvnw -Dmaven.test.skip=true package

$ java -jar target/javasnack-(version).jar
or
$ ./mvnw exec:java
```

### $$ 5. test and reports.

surefire test only (excluding `@Tag("junit5-tag-filter-2")` and `@MyJUnit5MetaAnnotation2` annotated test cases):

```
$ ./mvnw test
```

NOTE: some thread feature test cases may be fail cause of timing sensitivity.

all test cases (surefire + failsafe : including `@Tag("junit5-tag-filter-2")` and `@MyJUnit5MetaAnnotation2` annotated test cases):

```
$ ./mvnw integration-test
```

run checkstyle from command line:

```
$ ./mvnw checkstyle:check
```

run SpotBugs from command line:

```
$ ./mvnw spotbugs:check
```

run PMD from command line:

```
$ ./mvnw pmd:pmd
```

generate report:

```
$ ./mvnw site
```

all:

```
$ ./mvnw clean integration-test site
```

## $ references

JDK21:

- JDK 21 ドキュメント
  - en: https://docs.oracle.com/en/java/javase/21/
  - jp: https://docs.oracle.com/javase/jp/21/
- API JavaDoc
  - en: https://docs.oracle.com/en/java/javase/21/docs/api/
  - jp: https://docs.oracle.com/javase/jp/21/docs/api/

javadoc:

- `Javadoc ドキュメンテーションコメントの書き方 - Qiita`
  - https://qiita.com/maku77/items/6410c67ce95e08d8d1bd
- `Javadocメモ(Hishidama's Javadoc Memo)`
  - http://www.ne.jp/asahi/hishidama/home/tech/java/javadoc.html
- `Javadocの記述`
  - https://www.javadrive.jp/javadoc/

