# SmartWallet - Application de Gestion Financière Personnelle

## Description

SmartWallet est une application JavaFX complète de gestion financière permettant aux utilisateurs de :
- **Gérer les dépenses** : Enregistrer, modifier et supprimer des dépenses par catégorie
- **Configurer des budgets** : Définir des limites de dépenses par catégorie et mois
- **Planifier les finances** : Créer des plans financiers personnalisés
- **Visualiser les statistiques** : Consulter des tableaux de bord avec graphiques détaillés

## Architecture

### Structure du projet

```
smartwallet/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/example/smartwallet/
│   │   │   │   ├── SmartWalletApp.java           # Application principale
│   │   │   │   ├── controller/
│   │   │   │   │   ├── DashboardController.java
│   │   │   │   │   └── javafx/
│   │   │   │   │       ├── DepenseJavaFXController.java
│   │   │   │   │       ├── BudgetJavaFXController.java
│   │   │   │   │       ├── PlanningJavaFXController.java
│   │   │   │   │       └── DashboardJavaFXController.java
│   │   │   │   ├── model/
│   │   │   │   │   ├── Depense.java
│   │   │   │   │   ├── Budget.java
│   │   │   │   │   └── Planning.java
│   │   │   │   └── service/
│   │   │   ├── dao/
│   │   │   │   ├── DBConnection.java
│   │   │   │   ├── DepenseDAO.java
│   │   │   │   ├── BudgetDAO.java
│   │   │   │   └── PlanningDAO.java
│   │   └── resources/
│   │       └── com/example/smartwallet/
│   │           ├── depense-view.fxml
│   │           ├── budget-view.fxml
│   │           ├── planning-view.fxml
│   │           └── dashboard-view.fxml
├── database.sql                      # Script de création de la BD
└── pom.xml                          # Configuration Maven
```

## Prérequis

- **Java JDK 21+**
- **MySQL 8.0+**
- **Maven 3.6+**
- **JavaFX SDK 21.0.2**

## Installation

### 1. Installer les dépendances

```bash
mvn clean install
```

### 2. Configurer la base de données

#### Option 1 : Utiliser phpMyAdmin
1. Ouvrir phpMyAdmin
2. Importer le fichier `database.sql`
3. Vérifier que la base de données `smartwallet` est créée

#### Option 2 : Utiliser MySQL en ligne de commande
```bash
mysql -u root -p < database.sql
```

### 3. Configurer la connexion à la base de données

Éditer le fichier `src/main/java/dao/DBConnection.java` :

```java
private static final String URL = "jdbc:mysql://localhost:3306/smartwallet";
private static final String USER = "root";
private static final String PASSWORD = "votre_mot_de_passe"; // Remplacer par votre mot de passe MySQL
```

## Lancer l'application

### Via IDE (Eclipse, IntelliJ, VSCode)
1. Clic droit sur le projet > Run As > Java Application
2. Sélectionner `SmartWalletApp` comme classe principale

### Via Maven
```bash
mvn javafx:run
```

### Via ligne de commande
```bash
javac -p javafx-sdk/lib --add-modules javafx.controls,javafx.fxml SmartWalletApp.java
java -p javafx-sdk/lib --add-modules javafx.controls,javafx.fxml SmartWalletApp
```

## Fonctionnalités principales

### 1. Module Dépenses
- Ajouter une nouvelle dépense (montant, description, date, catégorie)
- Modifier une dépense existante
- Supprimer une dépense
- Filtrer par catégorie, mois et année
- Voir le total des dépenses

**Catégories disponibles :**
- Alimentation
- Transport
- Logement
- Santé
- Loisirs
- Éducation
- Autre

### 2. Module Budgets
- Créer un budget par catégorie et par mois
- Modifier un budget
- Supprimer un budget
- Visualiser la progression du budget avec une barre de progression
- Afficher le total des budgets

### 3. Module Planning
- Créer un plan financier (personnel, familial, professionnel, retraite)
- Fixer des objectifs de revenu et d'épargne
- Définir un pourcentage d'épargne
- Suivre l'état du planning (En cours, Terminé, Suspendu, Annulé)
- Modifier ou supprimer un planning

### 4. Tableau de Bord
- **Cartes de statistiques :**
  - Total des dépenses (tous les temps)
  - Dépenses du mois en cours
  - Total des budgets
  - Nombre de plannings
  - Budget utilisé ce mois

- **Graphiques :**
  - **Pie Chart** : Dépenses par catégorie
  - **Bar Chart** : Dépenses par mois (annuelle)
  - **Line Chart** : Évolution des dépenses (chronologique)

## Structure de la base de données

### Table `users`
```sql
id (INT, PK)
nom VARCHAR(100)
prenom VARCHAR(100)
email VARCHAR(150)
telephone VARCHAR(20)
date_creation DATETIME
date_modification DATETIME
```

### Table `depenses`
```sql
id (INT, PK)
user_id (INT, FK -> users)
montant DECIMAL(10,2)
description VARCHAR(255)
date_depense DATE
categorie VARCHAR(100)
date_creation DATETIME
```

### Table `budgets`
```sql
id (INT, PK)
user_id (INT, FK -> users)
categorie VARCHAR(100)
montant_max DECIMAL(10,2)
montant_actuel DECIMAL(10,2)
mois INT
annee INT
description TEXT
date_creation DATE
date_modification DATETIME
```

### Table `plannings`
```sql
id (INT, PK)
user_id (INT, FK -> users)
nom VARCHAR(150)
description TEXT
type VARCHAR(50)
mois INT
annee INT
revenu_prevu DECIMAL(10,2)
epargne_prevue DECIMAL(10,2)
pourcentage_epargne INT
statut VARCHAR(50)
date_creation DATETIME
date_modification DATETIME
```

## Technologies utilisées

- **Frontend** : JavaFX 21.0.2
- **Backend** : Java 21
- **Database** : MySQL 8.0
- **Build Tool** : Maven 3.6+
- **Patterns** : DAO (Data Access Object), MVC (Model-View-Controller)

## Améliorations futures

1. **Authentification** : Ajouter un système de login/signup
2. **Cartes bancaires** : Gérer plusieurs cartes bancaires
3. **Virements** : Implémenter les virements entre comptes
4. **Prêts** : Gérer les prêts entre amis
5. **Notifications** : Alertes quand un budget est dépassé
6. **Export** : Exporter les rapports en PDF
7. **Synchronisation cloud** : Sauvegarder les données en cloud
8. **Application mobile** : Créer une version Android/iOS

## Dépannage

### Problème : "Impossible de se connecter à la base de données"
**Solution :**
1. Vérifier que MySQL est démarré
2. Vérifier les identifiants dans `DBConnection.java`
3. Vérifier que la base de données `smartwallet` existe

### Problème : "Fichier FXML non trouvé"
**Solution :**
1. Vérifier que les fichiers FXML sont dans `src/main/resources/com/example/smartwallet/`
2. Vérifier les chemins dans `SmartWalletApp.java`

### Problème : "Module JavaFX non trouvé"
**Solution :**
1. Télécharger JavaFX SDK depuis https://gluonhq.com/products/javafx/
2. Configurer le chemin dans votre IDE
3. Ajouter les paramètres VM : `-p /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml`

## Support et Contact

Pour toute question ou problème, veuillez consulter la documentation ou contacter le support.

## Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

---

**Auteur :** Lolil  
**Date** : Février 2026  
**Version** : 1.0.0

