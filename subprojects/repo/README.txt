Maven project local repository directory.

## ShaniXmlParser manual install from shanixmlparser-v1.4.17.zip:

SHANI_JARS=ShaniXmlParser/CommonLibraries/lib/xml

mvn install:install-file \
    -DlocalRepositoryPath=subprojects/repo \
    -DcreateChecksum=true \
    -Dpackaging=jar \
    -Dfile=$SHANI_JARS/jaxen-1.1.1.jar \
    -DgroupId=shani-xml-parser \
    -DartifactId=jaxen \
    -Dversion=1.1.1

mvn install:install-file \
    -DlocalRepositoryPath=subprojects/repo \
    -DcreateChecksum=true \
    -Dpackaging=jar \
    -Dfile=$SHANI_JARS/shani-parser-v1.4.17.jar \
    -DgroupId=shani-xml-parser \
    -DartifactId=shani-parser \
    -Dversion=1.4.17

mvn install:install-file \
    -DlocalRepositoryPath=subprojects/repo \
    -DcreateChecksum=true \
    -Dpackaging=jar \
    -Dfile=$SHANI_JARS/xml-apis.jar \
    -DgroupId=shani-xml-parser \
    -DartifactId=xml-apis \
    -Dversion=1.4.17

