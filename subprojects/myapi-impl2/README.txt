# create myapi-impl1.jar
cd javasnack/subprojects/myapi-impl2
javac src/myapi/*.java
jar cvf myapi-impl2.jar -C src  .
