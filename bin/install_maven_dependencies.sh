#! /bin/sh
mvn install:install-file -Dfile=../lib/jason-2.4.jar -DgroupId=jason-lang -DartifactId=jason -Dversion=2.4 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=../lib/cartago-2.4.jar -DgroupId=CArtAgO-lang -DartifactId=cartago -Dversion=2.4 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=../lib/javino_stable_1.2.jar -DgroupId=br.pro.turing -DartifactId=javino_stable -Dversion=1.2 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=../lib/contextnet-2.7-eSMA.jar -DgroupId=br.pucrio.inf.lac -DartifactId=contextnet-eSMA -Dversion=2.7 -Dpackaging=jar -DgeneratePom=true
