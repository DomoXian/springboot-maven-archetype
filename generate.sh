#!/usr/bin/env bash
mvn archetype:generate \
-DgroupId=com.homer.services \
-DartifactId=simple \
-Dpackage=com.homer.services.simple \
-Dversion=0.0.1-SNAPSHOT \
-DarchetypeGroupId=com.homer \
-DarchetypeArtifactId=springboot-maven-archetype \
-DarchetypeVersion=0.0.1-SNAPSHOT
