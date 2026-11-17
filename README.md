# IFT3913 — Tâche 3 : Qualité logicielle

Équipe : Thomas Côté et Mahdi Slimani


Note sur l'utilisation de l'IA :

Thomas : ChatGPT a été utilisé pour comprendre la structure des fichiers .yml ainsi que pour régler les problèmes issus des workflows GitHub (par exemple : au niveau du formattage des fichiers, comme un espace vide dans un fichier ou pour les chemins de path trop longs)


Note : Le build Linux JDK 17 échoue lors des tests d’intégration S3, car Docker ne parvient pas à télécharger l’image MinIO (401 UNAUTHORIZED), ce qui interrompt l’exécution et entraîne l’omission des vérifications suivantes, sans que celles-ci aient nécessairement échoué (mais cela a confirmé que le rickroll fonctionne bien!).
1. Vulnérabilités introduites 

1.1 Path traversal

Choix du fichier : création d'une classe dédiée dans tika-core/src/main/java/org/apache/tika/tach3securite/, afin d'isoler la vulnérabilité sans affecter le fonctionnement de Tika.

Vulnérabilité : le nom de fichier fourni par l'utilisateur est utilisé directement pour construire un chemin, sans validation. Un chemin contenant ../ pourrait permettre d'accéder à des fichiers hors du répertoire prévu.

Résultat Semgrep : 1 détection, règle java-unsafe-path-read, ligne 29.


1.2 OS Command Injection

Choix du fichier : création d'une classe dédiée pour illustrer une exécution de commande système non sécurisée.

Vulnérabilité : l'entrée utilisateur est concaténée à une commande exécutée par cmd.exe /c. Des caractères spéciaux pourraient permettre d'exécuter des commandes supplémentaires.

Résultat Semgrep : 1 détection, règle java-shell-command-injection, ligne 27.


1.3 SQL Injection 

Choix du fichier : création d'une classe dédiée pour illustrer une requête SQL construite à partir d'une entrée utilisateur.

Vulnérabilité : l'entrée utilisateur est concaténée directement à la requête SQL, sans requête préparée ni paramètres. Cela permet potentiellement de modifier la structure de la requête.

Résultat Semgrep : 2 détections, règles java-sql-string-concatenation (ligne 31) et formatted-sql-string (ligne 36).


1.4 

1.5

1.6

1.7


2. Semgrep 

2 analyses ont été effectuées avec Semgrep 1.185.0 en utilisant les mêmes 65 règles de sécurité. 
Avant : 324 fichiers analysés, 3 détections bloquantes
Après : 331 fichiers analysés, 11 détections bloquantes

Donc, les 3 détections initiales étaient déjà présentes dans Apache Tika et demeurent inchangées. Les 8 nouvelles correspondent  aux 7 types de vulnérabilités introduits : 1 pour chaque et 2 Injections SQL (même vulnérabilité détectée par 2 règles Semgrep différentes : 1 détectant la concaténation d'une entrée d'utilisateur dans une requête SQL et l'autre l'exécution d'uen requête SQL construite de manière non sécurisée).

3. Conception et validation du workflow

Le workflow .github/workflows/semgrep.yml exécute Semgrep 1.185.0 lors des pushs et des Pull Requests. Il utilise les règles Java officielles (p/java) ainsi que nos règles personnalisées (.semgrep/).

L'option --baseline-commit permet de comparer les modifications à une version de référence afin de détecter uniquement les nouvelles vulnérabilités. L'option --error provoque l'échec du workflow lorsqu'une nouvelle détection est trouvée.

Validation : la PR #1 a échoué avec 5 détections bloquantes. La PR #2, ne contenant aucune nouvelle vulnérabilité, a réussi. Cela confirme le fonctionnement du mécanisme de détection différentielle.

4. Rickroll 

Une action a été créée dans .github/actions/rickroll/actions.yml, affichant un message humoristique et un lien vers Never Gonna Give You Up dans les logs. Le workflow main-jdk-build.yml appelle cette action dans les jobs build et e2-tests. L'action est effectuée lorsque l'étape Maven correspondante échoue, qu'il s'agisse d'un échec de test ou d'une autre erreur Maven.


Welcome to Apache Tika  <https://tika.apache.org/>
=================================================

[![license](https://img.shields.io/github/license/apache/tika.svg?maxAge=2592000)](http://www.apache.org/licenses/LICENSE-2.0)
[![Jenkins](https://img.shields.io/jenkins/s/https/ci-builds.apache.org/job/Tika/job/tika-main-jdk17.svg?maxAge=3600)](https://ci-builds.apache.org/job/Tika/job/tika-main-jdk17/)
[![Jenkins tests](https://img.shields.io/jenkins/t/https/ci-builds.apache.org/job/Tika/job/tika-main-jdk17.svg?maxAge=3600)](https://ci-builds.apache.org/job/Tika/job/tika-main-jdk17/lastBuild/testReport/)
[![Maven Central](https://img.shields.io/maven-central/v/org.apache.tika/tika.svg?maxAge=86400)](http://search.maven.org/#search|ga|1|g%3A%22org.apache.tika%22)

Apache Tika(TM) is a toolkit for detecting and extracting metadata and structured text content from various documents using existing parser libraries.

Tika is a project of the [Apache Software Foundation](https://www.apache.org).

Apache Tika, Tika, Apache, the Apache feather logo, and the Apache Tika project logo are trademarks of The Apache Software Foundation.

Quick Start
===========

**Parse a file in Java:**

```java
import org.apache.tika.Tika;

Tika tika = new Tika();
String text = tika.parseToString(new File("document.pdf"));
System.out.println(text);
```

**From the command line:**

```bash
java -jar tika-app-*.jar --text document.pdf
```

**Maven dependency:**

```xml
<dependency>
    <groupId>org.apache.tika</groupId>
    <artifactId>tika-parsers-standard-package</artifactId>
    <version>4.x.y</version>
    <type>pom</type>
</dependency>
```

Getting Started
===============
Pre-built binaries of Apache Tika standalone applications are available
from https://tika.apache.org/download.html . Pre-built binaries of all the
Tika jars can be fetched from Maven Central or your favourite Maven mirror.

**Tika 2.X and support for Java 8 reached End of Life (EOL) in April, 2025. 
See [Tika Roadmap 2.x, 3.x and beyond](https://cwiki.apache.org/confluence/display/TIKA/Tika+Roadmap+--+2.x%2C+3.x+and+Beyond).** 

Tika is based on **Java 17** and uses the [Maven 3](https://maven.apache.org) build system.
**N.B.** [Docker](https://www.docker.com/products/personal) is used for tests in tika-integration-tests. If Docker is not installed, those tests are skipped.

To build Tika from source, use the following command in the main directory:

    ./mvnw clean install

The Maven wrapper (`mvnw`) is included in the repository and will automatically download
the correct Maven version if needed. On Windows, use `mvnw.cmd` instead.

The build consists of a number of components, including a standalone runnable jar that you can use to try out Tika features. You can run it like this:

    java -jar tika-app/target/tika-app-*.jar --help


To build a specific project (for example, tika-server-standard):

    ./mvnw clean install -am -pl :tika-server-standard

If the ossindex-maven-plugin is causing the build to fail because a dependency
has now been discovered to have a vulnerability:

    ./mvnw clean install -Dossindex.skip


Faster Builds
=============

**Fast profile** - Use `-Pfast` to skip tests, checkstyle, and spotless:

    ./mvnw clean install -Pfast

**Parallel builds** - Add `-T1C` to build with 1 thread per CPU core:

    ./mvnw clean install -Pfast -T1C

**Maven Daemon (mvnd)** - Keeps a warm JVM running for 2-3x faster rebuilds:

```bash
# Install: https://github.com/apache/maven-mvnd
# macOS: brew install mvndaemon/tap/mvnd

# Use exactly like mvn
mvnd clean install -Pfast
mvnd test -pl :tika-core
```

**Combine both** for maximum speed during development:

    mvnd clean install -Pfast -T1C


Reproducible Builds
===================

Apache Tika supports [reproducible builds](https://reproducible-builds.org/). This means
that building the same source code with the same JDK version should produce
byte-for-byte identical artifacts, regardless of the build machine or time.

Key configuration:
- `project.build.outputTimestamp` is set in `tika-parent/pom.xml`
- All Maven plugins are configured to produce deterministic output

To verify the build plan supports reproducibility:

    ./mvnw artifact:check-buildplan

To verify two builds produce identical artifacts:

    ./mvnw clean install -DskipTests
    mv ~/.m2/repository/org/apache/tika tika-build-1
    ./mvnw clean install -DskipTests
    diff -r tika-build-1 ~/.m2/repository/org/apache/tika


Maven Dependencies
==================

Apache Tika provides *Bill of Material* (BOM) artifact to align Tika module versions and simplify version management. 
To avoid convergence errors in your own project, import this
bom or Tika's parent pom.xml in your dependency management section.

If you use Apache Maven:

```xml
<project>
  <dependencyManagement>
    <dependencies>
      <dependency>
       <groupId>org.apache.tika</groupId>
       <artifactId>tika-bom</artifactId>
       <version>4.x.y</version>
       <type>pom</type>
       <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

  <dependencies>
    <dependency>
      <groupId>org.apache.tika</groupId>
      <artifactId>tika-parsers-standard-package</artifactId>
      <type>pom</type>
      <!-- version not required since BOM included -->
    </dependency>
  </dependencies>
</project>
```

For Gradle:

```kotlin
dependencies {
  implementation(platform("org.apache.tika:tika-bom:4.x.y"))

  // version not required since bom (platform in Gradle terms)
  implementation("org.apache.tika:tika-parsers-standard-package@pom")
}
```

Migrating to 4.x
================
TBD

Contributing
============
See [CONTRIBUTING.md](CONTRIBUTING.md) and https://tika.apache.org/contribute.html

[![contributors](https://contributors-img.web.app/image?repo=apache/tika)](https://github.com/apache/tika/graphs/contributors)

Building from a Specific Tag
============================
Let's assume that you want to build the 3.0.1 tag:
```
0. Download and install hub.github.com
1. git clone https://github.com/apache/tika.git
2. cd tika
3. git checkout 3.0.1
4. ./mvnw clean install
```

If a new vulnerability has been discovered between the date of the
tag and the date you are building the tag, you may need to build with:

```
4. ./mvnw clean install -Dossindex.skip
```

If a local test is not working in your environment, please notify
 the project at dev@tika.apache.org. As an immediate workaround,
 you can turn off individual tests with e.g.:

```
4. ./mvnw clean install -Dossindex.skip -Dtest=\!UnpackerResourceTest#testPDFImages
```

License (see also LICENSE.txt)
==============================

Collective work: Copyright 2011 The Apache Software Foundation.

Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with this work for additional information regarding copyright ownership.  The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at

<https://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.

Apache Tika includes a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the licenses listed in the LICENSE.txt file.

Export Control
==============

This distribution includes cryptographic software.  The country in which you currently reside may have restrictions on the import, possession, use, and/or re-export to another country, of encryption software.  BEFORE using any encryption software, please  check your country's laws, regulations and policies concerning the import, possession, or use, and re-export of encryption software, to  see if this is permitted.  See <http://www.wassenaar.org/> for more information.

The U.S. Government Department of Commerce, Bureau of Industry and Security (BIS), has classified this software as Export Commodity Control Number (ECCN) 5D002.C.1, which includes information security software using or performing cryptographic functions with asymmetric algorithms.  The form and manner of this Apache Software Foundation distribution makes it eligible for export under the License Exception ENC Technology Software Unrestricted (TSU) exception (see the BIS Export Administration Regulations, Section 740.13) for both object code and source code.

The following provides more details on the included cryptographic software:

Apache Tika uses the Bouncy Castle generic encryption libraries for extracting text content and metadata from encrypted PDF files.  See <http://www.bouncycastle.org/> for more details on Bouncy Castle.  

Mailing Lists
=============

* user@tika.apache.org - About using Tika
* dev@tika.apache.org - About developing Tika

Subscribe by sending a message to `{list}-subscribe@tika.apache.org`.

Issue Tracker
=============

https://issues.apache.org/jira/browse/TIKA

Security
========

See [SECURITY.md](SECURITY.md) and https://tika.apache.org/security.html
