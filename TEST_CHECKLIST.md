# 🧪 CHECKLIST DE TEST & VALIDATION

## ✅ PRÉ-LANCEMENT

### Configuration système
- [ ] Java JDK 21+ installé
- [ ] Maven 3.6+ installé
- [ ] MySQL 8.0+ démarré
- [ ] Scene Builder installé (optionnel)

### Configuration du projet
- [ ] Base de données `smartwallet` créée
- [ ] Fichier `DBConnection.java` configuré
- [ ] Mot de passe MySQL mis à jour
- [ ] Dépendances Maven téléchargées (`mvn clean install`)

---

## 🚀 TESTS DE LANCEMENT

### Application démarre correctement
- [ ] Aucune erreur au lancement
- [ ] Fenêtre s'affiche avec les 4 onglets
- [ ] Tableau de bord charge sans erreur
- [ ] Pas d'exception dans la console

### Structure UI correcte
- [ ] Onglet "Tableau de Bord" visible
- [ ] Onglet "Dépenses" visible
- [ ] Onglet "Budgets" visible
- [ ] Onglet "Plannings" visible
- [ ] Tous les onglets non-fermables

---

## 📊 TESTS MODULE DÉPENSES

### Test 1 : Ajouter une dépense
- [ ] Cliquer sur "Ajouter une dépense"
- [ ] Montant : `45.50`
- [ ] Description : `Courses supermarché`
- [ ] Date : Sélectionner une date
- [ ] Catégorie : `Alimentation`
- [ ] Cliquer "Ajouter"
- [ ] Dépense apparaît dans le tableau
- [ ] Total se met à jour

### Test 2 : Sélectionner une dépense
- [ ] Cliquer sur une ligne du tableau
- [ ] Les champs se remplissent avec les données
- [ ] Le bouton "Modifier" devient activé

### Test 3 : Modifier une dépense
- [ ] Sélectionner une dépense
- [ ] Changer la description
- [ ] Cliquer "Modifier"
- [ ] Les données s'actualisent dans le tableau

### Test 4 : Supprimer une dépense
- [ ] Sélectionner une dépense
- [ ] Cliquer "Supprimer"
- [ ] La dépense disparaît du tableau
- [ ] Total se met à jour

### Test 5 : Filtrer les dépenses
- [ ] Développer la section "Filtres"
- [ ] Sélectionner une catégorie
- [ ] Le tableau affiche uniquement cette catégorie
- [ ] Sélectionner un mois et une année
- [ ] Le tableau filtre par période

### Test 6 : Validation des données
- [ ] Montant vide → Message d'erreur
- [ ] Montant invalide → Message d'erreur
- [ ] Date vide → Message d'erreur
- [ ] Catégorie vide → Message d'erreur

---

## 💰 TESTS MODULE BUDGETS

### Test 1 : Créer un budget
- [ ] Cliquer sur "Ajouter un budget"
- [ ] Catégorie : `Alimentation`
- [ ] Montant Max : `300.00`
- [ ] Mois : Février
- [ ] Année : 2026
- [ ] Cliquer "Ajouter"
- [ ] Budget apparaît dans le tableau

### Test 2 : Afficher la progression
- [ ] Sélectionner un budget
- [ ] La barre de progression s'affiche
- [ ] Le pourcentage d'utilisation s'affiche

### Test 3 : Modifier un budget
- [ ] Sélectionner un budget
- [ ] Changer le montant max
- [ ] Cliquer "Modifier"
- [ ] Budget se met à jour

### Test 4 : Supprimer un budget
- [ ] Sélectionner un budget
- [ ] Cliquer "Supprimer"
- [ ] Budget disparaît du tableau

### Test 5 : Total des budgets
- [ ] Ajouter 2-3 budgets
- [ ] Label "Total" affiche la somme correcte

---

## 📅 TESTS MODULE PLANNING

### Test 1 : Créer un planning
- [ ] Nom : `Planning février 2026`
- [ ] Type : `Personnel`
- [ ] Revenu Prévu : `3500.00`
- [ ] Épargne Prévue : `700.00`
- [ ] % Épargne : `20`
- [ ] Statut : `En cours`
- [ ] Cliquer "Ajouter"
- [ ] Planning apparaît dans le tableau

### Test 2 : Modifier un planning
- [ ] Sélectionner un planning
- [ ] Changer le nom
- [ ] Cliquer "Modifier"
- [ ] Planning se met à jour

### Test 3 : Supprimer un planning
- [ ] Sélectionner un planning
- [ ] Cliquer "Supprimer"
- [ ] Planning disparaît

### Test 4 : Types de planning
- [ ] Vérifier les 4 types disponibles
- [ ] Personnel ✓
- [ ] Familial ✓
- [ ] Professionnel ✓
- [ ] Retraite ✓

### Test 5 : Statuts
- [ ] Vérifier tous les statuts
- [ ] En cours ✓
- [ ] Terminé ✓
- [ ] Suspendu ✓
- [ ] Annulé ✓

---

## 📊 TESTS TABLEAU DE BORD

### Test 1 : Cartes de statistiques
- [ ] Carte "Total des Dépenses" affiche un montant
- [ ] Carte "Dépenses ce Mois" affiche un montant
- [ ] Carte "Total des Budgets" affiche un montant
- [ ] Carte "Nombre de Plannings" affiche un nombre
- [ ] Carte "Budget Utilisé" affiche un montant

### Test 2 : Pie Chart (Dépenses par catégorie)
- [ ] Graphique s'affiche
- [ ] Toutes les catégories sont représentées
- [ ] Les parts sont proportionnelles

### Test 3 : Bar Chart (Dépenses par mois)
- [ ] Graphique s'affiche
- [ ] Les 12 mois sont affichés
- [ ] Les barres correspondent aux dépenses

### Test 4 : Line Chart (Évolution des dépenses)
- [ ] Graphique s'affiche
- [ ] La courbe représente l'évolution
- [ ] Les dates sont en ordre croissant

### Test 5 : Rafraîchissement des données
- [ ] Ajouter une dépense depuis l'onglet Dépenses
- [ ] Aller au tableau de bord
- [ ] Les nouvelles données apparaissent

---

## 🗄️ TESTS BASE DE DONNÉES

### Test 1 : Connexion
- [ ] Application se connecte à la base sans erreur
- [ ] Pas de timeout de connexion
- [ ] Les requêtes s'exécutent rapidement

### Test 2 : Intégrité des données
- [ ] Aucune dépense en double
- [ ] Les montants sont cohérents
- [ ] Les dates sont valides

### Test 3 : Transactions
- [ ] Ajouter une dépense → Enregistrée en DB
- [ ] Modifier une dépense → Mise à jour en DB
- [ ] Supprimer une dépense → Effacée de la DB

### Test 4 : Requêtes
- [ ] Filtrage par catégorie correct
- [ ] Filtrage par mois correct
- [ ] Totaux calculés correctement

---

## 🎨 TESTS INTERFACE UTILISATEUR

### Esthétique
- [ ] Couleurs cohérentes
- [ ] Polices lisibles
- [ ] Espacements corrects
- [ ] Boutons bien visibles

### Ergonomie
- [ ] Navigation fluide entre onglets
- [ ] Formulaires faciles à comprendre
- [ ] Tableaux faciles à lire
- [ ] Messages d'erreur clairs

### Responsivité
- [ ] Fenêtre redimensionnable
- [ ] Éléments s'adaptent à la taille
- [ ] Pas de débordement de texte

---

## 🔍 TESTS DE VALIDATION

### Montants
- [ ] Nombre positif ✓
- [ ] Nombre décimal ✓
- [ ] Montant vide → Erreur ✓
- [ ] Texte au lieu de nombre → Erreur ✓

### Textes
- [ ] Chaînes non-vides ✓
- [ ] Caractères spéciaux acceptés ✓
- [ ] Longueur maximale respectée ✓

### Dates
- [ ] Format jj/mm/aaaa ✓
- [ ] Date future possible ✓
- [ ] Date passée possible ✓

### Pourcentages
- [ ] 0 à 100 ✓
- [ ] > 100 → Erreur ✓
- [ ] Texte → Erreur ✓

---

## 🚨 TESTS D'ERREURS

### Gestion d'exceptions
- [ ] Pas de crash en cas d'erreur BD
- [ ] Messages d'erreur affichés
- [ ] Application reste stable
- [ ] Pas de mémoire perdue

### Cas limites
- [ ] BD vide → Pas de crash
- [ ] 0 dépense → Graphiques vides OK
- [ ] Grands montants → Affichage correct
- [ ] Très longues descriptions → Pas de débordement

---

## ⚡ TESTS DE PERFORMANCE

### Vitesse de chargement
- [ ] Application démarre < 5 secondes
- [ ] Onglets changent < 1 seconde
- [ ] Tableau avec 100 lignes s'affiche < 2 secondes

### Utilisation mémoire
- [ ] RAM stable au bout de 1 minute
- [ ] Pas de fuite mémoire après 10 min
- [ ] Pas de ralentissement progressif

---

## 🎓 TESTS D'APPRENTISSAGE

### Code
- [ ] Code bien commenté
- [ ] Noms de variables explicites
- [ ] Structure facile à suivre
- [ ] Patterns reconnaissables (DAO, MVC)

### Documentation
- [ ] README complet ✓
- [ ] QUICKSTART clair ✓
- [ ] SCENE_BUILDER_GUIDE utile ✓
- [ ] Exemples fournis ✓

---

## 📋 RAPPORT DE TEST FINAL

| Module | Status | Notes |
|--------|--------|-------|
| Dépenses | [ ] OK | |
| Budgets | [ ] OK | |
| Plannings | [ ] OK | |
| Tableau de Bord | [ ] OK | |
| Base de données | [ ] OK | |
| UI/UX | [ ] OK | |
| Performance | [ ] OK | |

---

## ✨ SIGN-OFF

- [ ] Tous les tests passent
- [ ] Aucun bug critique
- [ ] Documentation complète
- [ ] Code prêt pour production
- [ ] Application approuvée pour utilisation

---

**Date de test** : __________________  
**Testeur** : __________________  
**Status global** : [ ] ✅ APPROUVÉ | [ ] ⚠️ AMÉLIORATIONS REQUISES | [ ] ❌ REJÉTÉ

---

**Notes additionnelles :**
```
________________________________
________________________________
________________________________
```

---

**Merci d'avoir testé SmartWallet ! 🎉**

