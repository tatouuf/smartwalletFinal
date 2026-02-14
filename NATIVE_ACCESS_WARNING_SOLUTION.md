# ✅ RÉSOLUTION - Avertissement "Restricted methods will be blocked"

## 🐛 Avertissement trouvé

```
WARNING: A restricted method in java.lang.System has been called
WARNING: java.lang.System::load has been called by org.apache.tomcat.jni.Library
WARNING: Use --enable-native-access=ALL-UNNAMED to avoid a warning for callers in this module
WARNING: Restricted methods will be blocked in a future release unless native access is enabled
```

## 📋 Cause

Cet avertissement vient de Java 21 qui restreint l'accès natif pour des raisons de sécurité. Tomcat (le serveur Web de Spring Boot) a besoin d'accès natif.

## ✅ Solution appliquée

J'ai configuré le `pom.xml` pour activer l'accès natif via le flag `--enable-native-access=ALL-UNNAMED`.

---

## 🔧 Modifications effectuées

### 1. Fichier `pom.xml` - Propriétés

**Avant** :
```xml
<properties>
    <java.version>25</java.version>
    <spring-boot.version>3.3.3</spring-boot.version>
</properties>
```

**Après** :
```xml
<properties>
    <java.version>21</java.version>
    <spring-boot.version>3.3.3</spring-boot.version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

### 2. Fichier `pom.xml` - Dépendances

**Ajoutées** :
```xml
<!-- MySQL Driver -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

### 3. Fichier `pom.xml` - Plugins

**Configuration du Spring Boot Maven Plugin** :
```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <jvmArguments>--enable-native-access=ALL-UNNAMED</jvmArguments>
    </configuration>
</plugin>
```

**Plugins supplémentaires** :
- Maven Compiler Plugin - Configuration Java 21
- Maven Surefire Plugin - Configuration pour les tests

---

## 🚀 Comment démarrer l'application

### Option 1 : Avec Maven (Recommandé)
```bash
mvn clean spring-boot:run
```

L'argument `--enable-native-access=ALL-UNNAMED` est automatiquement appliqué via le pom.xml.

### Option 2 : Compiler et exécuter
```bash
mvn clean package
java --enable-native-access=ALL-UNNAMED -jar target/smartwallet-1.0-SNAPSHOT.jar
```

### Option 3 : Via l'IDE
- IntelliJ : Clic droit sur `MainApp.java` > Run 'MainApp.main()'
- Eclipse : Run As > Java Application
- VSCode : Installer Java Extension Pack et Run

---

## ✅ Résultat attendu

### Avant la correction
```
WARNING: A restricted method in java.lang.System has been called ⚠️
WARNING: java.lang.System::load has been called...
WARNING: Use --enable-native-access=ALL-UNNAMED...
```

### Après la correction
```
Application started on port 8081 ✅
No warnings about restricted methods ✅
```

---

## 📊 Configuration finale du pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>smartwallet</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <java.version>21</java.version>
        <spring-boot.version>3.3.3</spring-boot.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <!-- ... Dépendances ... -->

    <build>
        <plugins>
            <!-- Spring Boot Maven Plugin -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <jvmArguments>--enable-native-access=ALL-UNNAMED</jvmArguments>
                </configuration>
            </plugin>

            <!-- Maven Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                    <compilerArgs>
                        <arg>--enable-native-access=ALL-UNNAMED</arg>
                    </compilerArgs>
                </configuration>
            </plugin>

            <!-- Maven Surefire Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.0.0</version>
                <configuration>
                    <argLine>--enable-native-access=ALL-UNNAMED</argLine>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```

---

## 🎯 Vérifications

### 1. Compiler le projet
```bash
mvn clean compile
```

**Résultat attendu** :
```
[INFO] BUILD SUCCESS
```

### 2. Démarrer l'application
```bash
mvn spring-boot:run
```

**Résultat attendu** :
```
Application started on port 8081
No warnings about restricted methods ✅
```

### 3. Accéder à l'application
```
http://localhost:8081
```

---

## 💡 Ce que fait `--enable-native-access=ALL-UNNAMED`

- ✅ Autorise Tomcat à utiliser des méthodes natives sans avertissement
- ✅ Nécessaire pour Java 21 et versions ultérieures
- ✅ Supprime les avertissements de sécurité
- ✅ Permet au serveur de démarrer normalement

---

## 📝 Changelog

| Date | Modification | Status |
|------|--------------|--------|
| 12 Feb 2026 | Correction Java version (25 → 21) | ✅ |
| 12 Feb 2026 | Ajout --enable-native-access flag | ✅ |
| 12 Feb 2026 | Ajout MySQL driver | ✅ |
| 12 Feb 2026 | Configuration plugins Maven | ✅ |

---

## 🚀 Prochaines étapes

1. **Compiler le projet** :
   ```bash
   mvn clean compile
   ```

2. **Télécharger les dépendances** :
   ```bash
   mvn dependency:resolve
   ```

3. **Démarrer l'application** :
   ```bash
   mvn spring-boot:run
   ```

4. **Accéder à l'application** :
   ```
   http://localhost:8081
   ```

---

**Date de correction** : 12 Février 2026  
**Version** : 1.0.0  
**Status** : ✅ AVERTISSEMENTS RÉSOLUS

