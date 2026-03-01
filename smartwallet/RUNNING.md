# SmartWallet - Application JavaFX

## Guide d'exécution

### Prérequis
- **Java 17+** (Java Development Kit)
- **Maven 3.6+**
- Accès à une connexion réseau (pour télécharger les dépendances)

### Vérification des prérequis

#### Vérifier Java
```bash
java -version
```

#### Vérifier Maven
```bash
mvn -version
```

### Méthode 1: Utiliser le script de démarrage (Recommandé)

#### Sous Windows (PowerShell)
```powershell
.\run.ps1
```

#### Sous Windows (Command Prompt)
```cmd
run.bat
```

#### Sous Linux/Mac
```bash
./run.sh
```

### Méthode 2: Compiler et exécuter manuellement

```bash
# Compilation
mvn clean compile

# Exécution
mvn javafx:run
```

### Méthode 3: Générer un JAR exécutable

```bash
mvn clean package
java -jar target/smartwallet-1.0-SNAPSHOT.jar
```

## Dépannage

### Erreur: "Des composants d'exécution JavaFX obligatoires pour exécuter cette application sont manquants"

**Solution:**
1. Assurez-vous que Java 17+ est installé
2. Vérifiez que Maven est correctement configuré
3. Exécutez: `mvn clean install -DskipTests`
4. Relancez l'application avec `mvn javafx:run`

### Erreur: "Maven not found"

**Solution:**
1. Installer Maven depuis https://maven.apache.org/download.cgi
2. Ajouter le répertoire `bin` de Maven au PATH de votre système
3. Redémarrer le terminal

### Erreur de connexion à la base de données

**Solution:**
1. Assurez-vous que MySQL est en cours d'exécution
2. Vérifiez les paramètres de connexion dans `MyDataBase.java`
3. Vérifiez que les identifiants de base de données sont corrects

## Architecture du projet

```
smartwallet/
├── src/main/java/
│   ├── Controllers/       # Contrôleurs JavaFX
│   ├── controller/        # Services de contrôle
│   ├── entities/          # Modèles de données
│   ├── services/          # Services métier
│   ├── api/              # API Server
│   ├── tests/            # Classes de test
│   └── utils/            # Utilitaires
├── src/main/resources/
│   ├── *.fxml            # Fichiers de mise en page
│   ├── css/              # Feuilles de style
│   └── icons/            # Icônes et images
└── pom.xml               # Configuration Maven
```

## Configuration de la base de données

Modifiez les paramètres dans `src/main/java/utils/MyDataBase.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/smartwallet";
private static final String USER = "root";
private static final String PASSWORD = "password";
```

## Dépendances principales

- **JavaFX 21.0.2** - Framework d'interface utilisateur
- **MySQL Connector 8.0.33** - Driver MySQL
- **Gson 2.10.1** - Parsing JSON
- **OkHttp3** - Client HTTP
- **Stripe API 28.4.0** - Intégration Stripe

## Support

Pour toute question ou bug, veuillez contacter l'équipe de développement.

## Licence

Propriétaire - SmartWallet

