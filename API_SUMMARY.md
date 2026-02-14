# 📋 TABLEAU COMPLET DES API - SmartWallet

## 🔴 DEPENSE CONTROLLER - /api/depenses

| Méthode | Endpoint | Rôle | Paramètres | Retour |
|---------|----------|------|-----------|--------|
| POST | `/api/depenses` | Créer dépense | Depense | void |
| GET | `/api/depenses/user/{userId}` | Toutes dépenses | userId | List |
| GET | `/api/depenses/user/{userId}/categorie/{categorie}` | Par catégorie | userId, cat | List |
| GET | `/api/depenses/user/{userId}/mois/{mois}/annee/{annee}` | Par période | userId, mois, annee | List |
| GET | `/api/depenses/user/{userId}/total` | Total général | userId | double |
| GET | `/api/depenses/user/{userId}/total/mois/{mois}/annee/{annee}` | Total mensuel | userId, mois, annee | double |
| PUT | `/api/depenses` | Modifier | Depense | void |
| DELETE | `/api/depenses/{depenseId}` | Supprimer | depenseId | void |

---

## 🔵 BUDGET CONTROLLER - /api/budgets

| Méthode | Endpoint | Rôle | Paramètres | Retour |
|---------|----------|------|-----------|--------|
| POST | `/api/budgets` | Créer budget | Budget | void |
| GET | `/api/budgets/user/{userId}` | Tous budgets | userId | List |
| GET | `/api/budgets/user/{userId}/mois/{mois}/annee/{annee}` | Par période | userId, mois, annee | List |
| GET | `/api/budgets/user/{userId}/categorie/{categorie}/mois/{mois}/annee/{annee}` | Par catégorie | userId, cat, mois, annee | Budget |
| GET | `/api/budgets/user/{userId}/total` | Total alloué | userId | double |
| PUT | `/api/budgets` | Modifier | Budget | void |
| DELETE | `/api/budgets/{budgetId}` | Supprimer | budgetId | void |
| PUT | `/api/budgets/{budgetId}/update-amount/{montant}` | Tracker consommation | budgetId, montant | void |

---

## 🟢 PLANNING CONTROLLER - /api/plannings

| Méthode | Endpoint | Rôle | Paramètres | Retour |
|---------|----------|------|-----------|--------|
| POST | `/api/plannings` | Créer plan | Planning | void |
| GET | `/api/plannings/user/{userId}` | Tous plans | userId | List |
| GET | `/api/plannings/user/{userId}/mois/{mois}/annee/{annee}` | Par période | userId, mois, annee | List |
| GET | `/api/plannings/user/{userId}/count` | Nombre de plans | userId | int |
| PUT | `/api/plannings` | Modifier | Planning | void |
| DELETE | `/api/plannings/{planningId}` | Supprimer | planningId | void |
| POST | `/api/plannings/validate` | Valider | Planning | boolean |
| POST | `/api/plannings/savings-rate` | Taux épargne | Planning | double |
| POST | `/api/plannings/is-completed` | Vérifier complétude | Planning | boolean |
| POST | `/api/plannings/is-active` | Vérifier activité | Planning | boolean |

---

## 📊 RÉSUMÉ

- **Total endpoints:** 26
- **GET:** 13 (lectures)
- **POST:** 5 (créations/validations)
- **PUT:** 6 (modifications)
- **DELETE:** 2 (suppressions)

---

**Généré:** 13 Février 2026 | **Statut:** ✅ Complet

