javasnack
=========

Tinny Java Excersise, Experimental, Practices Programms.

## for Developers setup (Eclipse/Spring Tool Suite)

- OpenJDK >= 11
- Eclipse >= 201903 (v4.10), "Eclipse IDE for Java EE Developers" package
  - or Spring Tools 4 (STS4 >= 4.2.1)
- Maven >= 3.6.1 (download automatically by mvnw script. also works by 3.x)
- use UTF-8 for source code and other text files.

to setup Eclipse/STS4, see `setup-type2` in https://github.com/msakamoto-sf/howto-eclipse-setup and follow:
- Eclipse install how-to
- Clean Up/Formatter configuration
- Recommended plugin:
  - [Eclipse Checkstyle Plugin](https://checkstyle.org/eclipse-cs/)
  - [SpotBugs Eclipse plugin](https://spotbugs.readthedocs.io/en/latest/eclipse.html)

### Eclipse/STS checkstyle plugin configuration

import checkstyle configuration xml into eclipse:

1. import xml into eclipse global configuration.
   1. Window -> Preferences -> Checkstyle -> Global Check Configurations : click "New"
   2. Select: "Project Relative Configuration"
   3. Location: click "Browse" -> then select project -> select `javasnack/checkstyle-settings/google_checks-8.18_custom.xml`
   4. Name: set proper name
   5. Apply and Close
2. setup project specific setting
   1. right-click project -> Properties (ALT + Enter)
   2. Checkstyle -> check "Checkstyle active for this project"
   3. select imported xml configration
   4. Apply and Close
3. run first checkstyle
   1. right-click project -> Checkstyle -> "Check Code with Checkstyle"

reload updated xml to Eclipse/STS: (unstable way)
1. some operations in Window -> Preferences -> Checkstyle config then "Apply and Click". (e.g. checkbox on <> off)
2. project setting -> switch checkstyle activate / deactivate some times.
3. rebuild project.

### Eclipse/STS SpotBugs plugin configuration

1. import project specific include/exclude filter.
   1. right-click project -> Properties -> SpotBugs -> check "Enable project specific settings"
   2. at filter file tab, add "spotbugs-exclude-filter.xml" to exclude filter file, add "spotbugs-include-filter.xml" to include filter file.
2. SpotBugs plugin setting (see pom.xml configuration)
   1. set effort (分析力) to "Default"
   2. set max rank (報告する最小ランク) to 15
   3. set threshold (レポートする最低の信頼度) to "Medium"
   4. check below categories (see spotbugs-include-filter.xml)
      1. "Bad practice"
      2. "Malicious code vulnerability"
      3. "Correctness"
      4. "Performance"
      5. "Security"
      6. "Dodgy code"
      7. "Multithreaded correctness"
      8. "Internationalization"
   5. do NOT check "Experimental" category.
3. run SpotBugs check manually:
   1. right-click project -> SpotBugs -> click "Find Bugs"

If you edited exclude/include xml filter, then right-click project -> SpotBugs -> clear markers and Find Bugs again.

SpotBugs reference:

- https://spotbugs.github.io/
- https://spotbugs.github.io/spotbugs-maven-plugin/
- https://spotbugs.readthedocs.io/en/latest/
- https://spotbugs.readthedocs.io/ja/latest/

## how to build and execute main()

javasnack demonstrates classloading from jar in resources and referencing project related local repository.

1. build jar library and install to src/main/resources/.
2. build jar library and deploy to project related path. (NOT to `~/.m2/repository/`)
3. setup `~/.m2/settings.xml` to refer project related path.
4. build main project.

### 1. build jar library and install to src/main/resources/.

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

### 2. build jar library and deploy to project related path. (NOT to `~/.m2/repository/`)

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

### 3. setup `~/.m2/settings.xml` to refer project related path.

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

### 4. build main project.

```
$ cd javasnack/

$ ./mvnw package

$ java -jar target/javasnack-(version).jar
or
$ ./mvnw exec:java
```

### 5. test and reports.

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

generate report:

```
$ ./mvnw site
```

all:

```
$ ./mvnw clean integration-test site
```

## references

jdk11:

- `JDK 11 Documentation - Home`
  - https://docs.oracle.com/en/java/javase/11/
- `JDK 11ドキュメント - ホーム`
  - https://docs.oracle.com/javase/jp/11/
- API JavaDoc(en)
  - https://docs.oracle.com/en/java/javase/11/docs/api/index.html
- API JavaDoc(ja)
  - https://docs.oracle.com/javase/jp/11/docs/api/index.html

javadoc:

- `Javadoc ドキュメンテーションコメントの書き方 - Qiita`
  - https://qiita.com/maku77/items/6410c67ce95e08d8d1bd
- `Javadocメモ(Hishidama's Javadoc Memo)`
  - http://www.ne.jp/asahi/hishidama/home/tech/java/javadoc.html
- `Javadocの記述`
  - https://www.javadrive.jp/javadoc/

