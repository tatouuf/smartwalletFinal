#!/bin/bash

# Script de démarrage de SmartWallet pour Linux/Mac
# Cette application nécessite Java 17+ et les composants JavaFX

echo ""
echo "========================================="
echo "   SmartWallet - AI-Powered Wallet"
echo "========================================="
echo ""

# Vérifier si Maven est installé
if ! command -v mvn &> /dev/null; then
    echo "Erreur: Maven n'est pas installé ou non présent dans le PATH"
    echo "Veuillez installer Maven et ajouter son répertoire bin au PATH"
    exit 1
fi

# Compiler et exécuter l'application
echo "Compilation et exécution de l'application..."
echo ""

cd "$(dirname "$0")"
mvn clean javafx:run

if [ $? -ne 0 ]; then
    echo ""
    echo "Erreur lors de l'exécution de l'application"
    exit 1
fi

echo ""
echo "Application terminée avec succès"

