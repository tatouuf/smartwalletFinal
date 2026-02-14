# 🔧 GUIDE DE DÉPANNAGE - ERREURS SPRING FRAMEWORK

## 🎯 Le Problème

```
ERROR: Cannot resolve symbol 'springframework'
ERROR: Cannot resolve symbol 'RestController'
ERROR: Cannot resolve symbol 'Autowired'
ERROR: Cannot resolve symbol 'PostMapping'
... etc
```

## ✅ Résolution

### Option 1: Rechargement Automatique (Recommandé)

**Dans IntelliJ IDEA:**
1. Appuyez sur: **Ctrl + Alt + Y** (ou Mac: Cmd + Shift + I)
2. Ou allez à: **Tools > Maven > Reload Projects**
3. Attendez quelques minutes...
4. Les erreurs disparaîtront automatiquement ✅

### Option 2: Invalidation Complète du Cache

**Dans IntelliJ IDEA:**
1. Allez à: **File > Invalidate Caches**
2. Sélectionnez: **Invalidate and Restart**
3. IntelliJ va redémarrer et réindexer tout ✅

### Option 3: Exécuter le Script de Réparation

**Dans PowerShell ou CMD:**
```bash
# Allez à la racine du projet
cd C:\Users\lolil\smartwalletFinal\smartwallet

# Exécutez le script de nettoyage
full-reindex.bat

# Puis dans IntelliJ: File > Reload All from Disk
```

### Option 4: Suppression Manuelle du Cache

1. **Fermez IntelliJ**
2. **Supprimez le dossier** `.idea` (attention: caché par défaut)
3. **Rouvrez le projet** dans IntelliJ
4. **Attendez** la réindexation (5-10 min)

## 🔍 Vérification du Problème

### Le fichier POM est correct? ✅

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

✅ OUI - Le pom.xml est correct!

### Les dépendances sont téléchargées?

Emplacement du cache Maven: `C:\Users\lolil\.m2\repository`

**Structure attendue:**
```
.m2\repository\org\springframework\boot\
├── spring-boot-starter-parent\
├── spring-boot-starter-web\
└── spring-boot-starter-data-jpa\
```

### IntelliJ reconnaît les repos Maven?

✅ OUI - Fichier `.idea\jarRepositories.xml` est configuré

```xml
<remote-repository>
    <option name="id" value="central" />
    <option name="url" value="https://repo.maven.apache.org/maven2" />
</remote-repository>
```

## 🚨 Si le Problème Persiste

### 1️⃣ Assurez-vous que...

- [ ] IntelliJ est complètement fermé
- [ ] Vous avez une connexion Internet (pour le téléchargement initial)
- [ ] Le dossier du projet est accessible en lecture/écriture
- [ ] Vous n'avez pas d'antivirus bloquant les opérations Maven

### 2️⃣ Essayez ceci...

```bash
# Terminal IntelliJ: Terminal tab
mvn clean install -DskipTests
```

Si Maven n'est pas installé, utilisez le Maven Wrapper:

```bash
# Windows CMD/PowerShell
.\mvnw clean install -DskipTests
```

### 3️⃣ Vérifiez les logs IDE

**Affichage des logs:**
1. Help > Show Log in Explorer
2. Cherchez les messages d'erreur ou d'avertissement
3. Partagez-les si vous avez besoin d'aide

## ✨ Après la Synchronisation

Une fois synchronisé, vous devriez voir:

✅ Les imports ne sont plus en rouge:
```java
import org.springframework.web.bind.annotation.*; // ✅ Vert
```

✅ Les annotations sont reconnues:
```java
@RestController      // ✅ Bleu
@RequestMapping      // ✅ Bleu
@Autowired          // ✅ Bleu
```

✅ Les symboles ne sont plus soulignés en rouge

## 📊 Statut du Projet

| Fichier | Erreurs | Statut | Action |
|---------|---------|--------|--------|
| PlanningDAO.java | 0 (corrigées) | ✅ OK | Aucune |
| DepenseController.java | 27 (Spring IDE) | ⏳ En attente | Sync Maven |
| BudgetController.java | 22 (Spring IDE) | ⏳ En attente | Sync Maven |
| PlanningController.java | 24 (Spring IDE) | ⏳ En attente | Sync Maven |

## 🎓 Pourquoi Cela Arrive?

**Scénario typique:**
1. ✅ Code téléchargé / copié
2. ❌ IDE ouvert avant que Maven finisse
3. ❌ IDE cache les symboles inexistants
4. ✅ **Solution:** Forcer le reindex

**C'est normal et facile à réparer!**

## 💬 Questions Supplémentaires?

- Consultez: `FIXES_APPLIED.md`
- Consultez: `CORRECTIONS_SUMMARY.md`
- Vérifiez: `pom.xml`

---

**Dernière mise à jour:** 13 Février 2026
**Version du Projet:** SmartWallet 1.0-SNAPSHOT

