# CORRECTION DE L'ERREUR ClassNotFoundException

## Problème identifié
L'erreur `ClassNotFoundException: com` indique que la configuration de run d'IntelliJ pointe vers une classe principale incorrecte ou incomplète.

## Solutions

### SOLUTION 1 : Via IntelliJ (RECOMMANDÉE)

1. **Ouvrez le menu** : `Run` → `Edit Configurations`
2. **Supprimez** toute configuration incorrecte nommée `esprit.tn.chayma` ou `com`
3. **Sélectionnez** la configuration `Launcher` (créée automatiquement)
4. **Vérifiez** que le champ `Main class:` contient exactement : `com.example.smartwallet.Launcher`
5. **Assurez-vous** que le module est défini à `smartwallet`
6. **Cliquez sur** `Apply` et `OK`
7. **Lancez** l'application avec `Shift + F10` (ou bouton Run)

### SOLUTION 2 : Via Maven (ALTERNATIVE)

Exécutez la commande suivante dans le terminal :
```bash
mvn clean javafx:run
```

### SOLUTION 3 : Via script batch
Exécutez le fichier : `run.bat`

## Fichiers modifiés
- ✅ `pom.xml` - Classe principale définie correctement
- ✅ `.idea/runConfigurations/Launcher.xml` - Configuration IntelliJ créée
- ✅ `.idea/runConfigurations/HelloApplication.xml` - Configuration alternative
- ✅ `src/main/java/module-info.java` - Corrigé
- ✅ `src/main/java/tests/services/Main.java` - Classe et package corrigés

## Important
Après avoir suivi la SOLUTION 1 dans IntelliJ :
- Appuyez sur `Ctrl + F5` pour nettoyer le cache
- Puis relancez l'application


