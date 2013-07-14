

# 1. create test jar1:

cd javasnack/subprojects/jar1
jar cvfm testjar1-1.0.jar manifest.mf -C src/ testjar1/Greeting.class

java -jar testjar1-1.0.jar
->
Grood Morning, ABC.
Good Afternoon, DEF.

# 2. install jar to project local repository

cd javasnack/
mvn install:install-file -DlocalRepositoryPath=subprojects/repo -DcreateChecksum=true -Dpackaging=jar -Dfile=subprojects/jar1/testjar1-1.0.jar -DgroupId=subprojects -DartifactId=testjar1 -Dversion=1.0

see:
"java - Can I add jars to maven 2 build classpath without installing them? - Stack Overflow"
http://stackoverflow.com/questions/364114/can-i-add-jars-to-maven-2-build-classpath-without-installing-them/

"Mavenプロジェクトで3rdパーティJARを扱う方法｜Ouobpo"
http://ameblo.jp/ouobpo/entry-10051976866.html

