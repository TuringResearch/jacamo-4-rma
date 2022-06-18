#! /bin/sh
git pull

mvn clean -f embedded-cartago/pom.xml
mvn clean -f embedded-jason/pom.xml
mvn clean -f pom.xml

mvn install -f embedded-cartago/pom.xml
mvn install -f embedded-jason/pom.xml
mvn package -f pom.xml


#mkdir -p /tmp/x1

#cp distribution/target/rma-4-jacamo-1.0.0.zip /tmp/x1/
#cd /tmp/x1/
#unzip *
#mv *.jar /home/nilson/projetos/SMA-Recurso-Runtime/jacamo-0.8/libs/ --verbose
#cd


