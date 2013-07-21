# create myapi-impl1.jar
cd javasnack/subprojects/myapi-impl1
javac src/myapi/*.java
jar cvf myapi-impl1.jar -C src  .
