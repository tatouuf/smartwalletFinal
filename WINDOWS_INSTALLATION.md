# 🪟 GUIDE D'INSTALLATION WINDOWS - SmartWallet

Ce guide est spécifiquement pour l'installation sur Windows.

---

## ✅ ÉTAPE 1 : Installer Java JDK 21

### Télécharger
1. Aller sur https://www.oracle.com/java/technologies/downloads/#java21
2. Cliquer sur "Windows x64 Installer"
3. Accepter la licence
4. Télécharger le fichier `.msi`

### Installer
1. Double-cliquer sur le fichier `.msi`
2. Cliquer "Next" jusqu'à la fin
3. Accepter l'installation dans `C:\Program Files\Java\jdk-21`

### Vérifier l'installation
```cmd
java -version
```

Vous devriez voir quelque chose comme :
```
java version "21" 2023-09-19 LTS
```

---

## ✅ ÉTAPE 2 : Installer MySQL 8.0

### Télécharger
1. Aller sur https://dev.mysql.com/downloads/mysql/
2. Cliquer sur "Windows (x86, 64-bit), ZIP Archive"
3. Créer un compte Oracle ou se connecter
4. Télécharger le fichier ZIP

### Installer
1. Extraire le ZIP dans `C:\mysql-8.0.36`
2. Ouvrir PowerShell en Admin
3. Naviguer vers le dossier MySQL :
```powershell
cd C:\mysql-8.0.36\bin
```

4. Installer le service MySQL :
```powershell
mysqld --install MySQL80
```

5. Démarrer MySQL :
```powershell
net start MySQL80
```

### Vérifier l'installation
```cmd
mysql -u root
```

Vous devriez voir l'invite `mysql>`

---

## ✅ ÉTAPE 3 : Installer Maven 3.9

### Télécharger
1. Aller sur https://maven.apache.org/download.cgi
2. Télécharger "apache-maven-3.9.x-bin.zip"
3. Extraire dans `C:\apache-maven-3.9.5`

### Configurer la variable d'environnement
1. Appuyer sur `Win + X` > "System"
2. Cliquer sur "Advanced system settings" (Paramètres système avancés)
3. Cliquer sur "Environment Variables" (Variables d'environnement)
4. Sous "System variables", cliquer sur "New" (Nouveau)

**Nouvelle variable 1:**
- Variable name: `MAVEN_HOME`
- Variable value: `C:\apache-maven-3.9.5`

**Nouvelle variable 2:**
- Variable name: `JAVA_HOME`
- Variable value: `C:\Program Files\Java\jdk-21`

5. Modifier la variable `Path` :
   - Sélectionner `Path` > "Edit"
   - Ajouter `%MAVEN_HOME%\bin`
   - Cliquer OK

### Vérifier l'installation
```cmd
mvn --version
```

Vous devriez voir la version de Maven et Java.

---

## ✅ ÉTAPE 4 : Créer la base de données

### Démarrer MySQL
```cmd
mysql -u root -p
```

Appuyer sur Entrée si pas de mot de passe.

### Importer la base de données
1. Télécharger le fichier `database.sql`
2. Placer le fichier dans `C:\Users\[VotreNomUtilisateur]\Desktop\`
3. Ouvrir PowerShell
4. Exécuter :

```powershell
cd "C:\Program Files\MySQL\MySQL Server 8.0\bin"
mysql -u root < C:\Users\[VotreNomUtilisateur]\Desktop\database.sql
```

### Vérifier
```cmd
mysql -u root
USE smartwallet;
SHOW TABLES;
```

Vous devriez voir 5 tables.

---

## ✅ ÉTAPE 5 : Configurer le projet

### Cloner/Télécharger le projet
1. Télécharger le dossier `smartwallet`
2. Placer le dans `C:\Users\[VotreNomUtilisateur]\SmartWallet\`

### Configurer la connexion
1. Ouvrir le fichier :
   ```
   C:\Users\[VotreNomUtilisateur]\SmartWallet\smartwallet\src\main\java\dao\DBConnection.java
   ```

2. Modifier :
   ```java
   private static final String PASSWORD = ""; // Mettre votre mot de passe MySQL
   ```

3. Sauvegarder (Ctrl+S)

---

## ✅ ÉTAPE 6 : Installer les dépendances

### Ouvrir PowerShell
1. Appuyer sur `Win + X`
2. Sélectionner "Windows PowerShell"

### Naviguer vers le projet
```powershell
cd C:\Users\[VotreNomUtilisateur]\SmartWallet\smartwallet
```

### Installer
```powershell
mvn clean install
```

Attendre jusqu'à "BUILD SUCCESS" (peut prendre 5-10 minutes).

---

## ✅ ÉTAPE 7 : Lancer l'application

### Via Maven
```powershell
mvn javafx:run
```

### Via IntelliJ IDEA (Recommandé)
1. Ouvrir IntelliJ IDEA
2. File > Open
3. Sélectionner le dossier `smartwallet`
4. Attendre l'indexation
5. Clic droit sur `SmartWalletApp.java`
6. Sélectionner "Run 'SmartWalletApp'"

### Via Eclipse
1. Ouvrir Eclipse
2. File > Import > Maven > Existing Maven Projects
3. Sélectionner le dossier `smartwallet`
4. Clic droit sur le projet > Run As > Java Application
5. Sélectionner `SmartWalletApp`

---

## 🐛 Dépannage Windows

### Problème : "java n'est pas reconnu comme..."
**Solution :**
```powershell
# Ajouter Java au PATH
[Environment]::SetEnvironmentVariable("PATH","$ENV:PATH;C:\Program Files\Java\jdk-21\bin","User")
```

### Problème : "mvn n'est pas reconnu..."
**Solution :**
```powershell
# Redémarrer PowerShell après installation
# Ou exécuter :
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
```

### Problème : "Impossible de se connecter à MySQL"
**Solution :**
```cmd
# Vérifier que MySQL est démarré
net start MySQL80

# Ou redémarrer
net stop MySQL80
net start MySQL80
```

### Problème : "Port 3306 déjà utilisé"
**Solution :**
```powershell
# Trouver le processus
netstat -ano | findstr :3306

# Arrêter MySQL
net stop MySQL80
```

### Problème : "Database existe déjà"
**Solution :**
```sql
DROP DATABASE smartwallet;
-- Puis réimporter database.sql
```

---

## 📁 Chemins par défaut Windows

| Composant | Chemin |
|-----------|--------|
| Java JDK | `C:\Program Files\Java\jdk-21` |
| MySQL | `C:\Program Files\MySQL\MySQL Server 8.0` |
| Maven | `C:\apache-maven-3.9.5` |
| Projet | `C:\Users\[Utilisateur]\SmartWallet\smartwallet` |
| Base données | `C:\ProgramData\MySQL\MySQL Server 8.0\data` |

---

## 🚀 Commandes Windows rapides

### Ouvrir une PowerShell
```cmd
Win + X > PowerShell
```

### Naviguer vers un dossier
```powershell
cd "C:\chemin\vers\dossier"
```

### Éditer un fichier
```powershell
notepad chemin/vers/fichier.txt
```

### Vérifier les variables d'environnement
```powershell
echo $env:JAVA_HOME
echo $env:MAVEN_HOME
```

### Redémarrer les services
```powershell
# MySQL
net stop MySQL80
net start MySQL80

# Services Windows
Restart-Service MySQL80
```

---

## ✨ Conseils Windows

1. **Utiliser PowerShell en Admin** pour l'installation
2. **Redémarrer après l'installation** de Java et Maven
3. **Vérifier les chemins** sans espaces (utiliser des guillemets si nécessaire)
4. **Garder les logs** en cas de problème (Ctrl+A, Ctrl+C)

---

## 🎯 Vérification finale

Une fois tout installé :

```cmd
# Ouvrir PowerShell
java -version         # Doit afficher Java 21
mvn --version        # Doit afficher Maven
mysql -u root        # Doit connecter à MySQL
```

Si tout fonctionne, vous êtes prêt ! 🎉

```powershell
cd "C:\Users\[Utilisateur]\SmartWallet\smartwallet"
mvn javafx:run
```

---

## 📞 Support Windows

### Si Java ne fonctionne pas
- Réinstaller Java JDK 21 (version x64)
- Ajouter `C:\Program Files\Java\jdk-21\bin` au PATH

### Si MySQL ne démarre pas
- Vérifier que le port 3306 est libre
- Vérifier les droits administrateur
- Consulter le log : `C:\ProgramData\MySQL\MySQL Server 8.0\Data\[Hostname].err`

### Si Maven ne fonctionne pas
- Vérifier les variables d'environnement
- Redémarrer PowerShell
- Réinstaller Maven proprement

---

**Créé pour Windows 10/11**  
**Date** : Février 2026  
**Version** : 1.0

