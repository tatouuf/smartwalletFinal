#!/bin/bash
# Script pour nettoyer complètement IntelliJ et Maven

echo "======================================"
echo "Nettoyage complet du projet SmartWallet"
echo "======================================"

# 1. Supprimer le cache Maven
echo "1. Suppression du cache Maven..."
rm -rf ~/.m2/repository
echo "   ✓ Cache Maven supprimé"

# 2. Supprimer les fichiers de cache IntelliJ
echo "2. Suppression du cache IntelliJ..."
rm -rf .idea/libraries
rm -rf .idea/modules.xml
rm -f .idea/*.xml
echo "   ✓ Cache IntelliJ supprimé"

# 3. Supprimer les fichiers build
echo "3. Suppression des fichiers build..."
mvn clean
echo "   ✓ Build supprimé"

# 4. Télécharger les dépendances
echo "4. Téléchargement des dépendances..."
mvn -U dependency:resolve-plugins dependency:resolve
echo "   ✓ Dépendances téléchargées"

# 5. Compiler
echo "5. Compilation du projet..."
mvn compile
echo "   ✓ Compilation réussie"

echo ""
echo "======================================"
echo "✓ Nettoyage terminé"
echo "======================================"
echo "Próxima étape: Recharger le projet dans l'IDE"

