-- =====================================================
-- Base de données SmartWallet
-- =====================================================

-- Créer la base de données
CREATE DATABASE IF NOT EXISTS smartwallet;
USE smartwallet;

-- =====================================================
-- Table des utilisateurs
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telephone VARCHAR(20),
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table des dépenses
-- =====================================================
CREATE TABLE IF NOT EXISTS depenses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    montant DECIMAL(10, 2) NOT NULL,
    description VARCHAR(255),
    date_depense DATE NOT NULL,
    categorie VARCHAR(100),
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_date (user_id, date_depense)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table des budgets
-- =====================================================
CREATE TABLE IF NOT EXISTS budgets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    categorie VARCHAR(100) NOT NULL,
    montant_max DECIMAL(10, 2) NOT NULL,
    montant_actuel DECIMAL(10, 2) DEFAULT 0,
    mois INT NOT NULL,
    annee INT NOT NULL,
    description TEXT,
    date_creation DATE NOT NULL,
    date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_mois (user_id, mois, annee),
    UNIQUE KEY unique_budget (user_id, categorie, mois, annee)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table des plannings
-- =====================================================
CREATE TABLE IF NOT EXISTS plannings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    nom VARCHAR(150) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    mois INT NOT NULL,
    annee INT NOT NULL,
    revenu_prevu DECIMAL(10, 2) NOT NULL,
    epargne_prevue DECIMAL(10, 2) NOT NULL,
    pourcentage_epargne INT,
    statut VARCHAR(50),
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_mois (user_id, mois, annee)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table des catégories
-- =====================================================
CREATE TABLE IF NOT EXISTS categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    couleur VARCHAR(7),
    icone VARCHAR(50),
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Insérer les catégories par défaut
-- =====================================================
INSERT INTO categories (nom, description, couleur) VALUES
('Alimentation', 'Courses et repas', '#FF6B6B'),
('Transport', 'Carburant et transport', '#4ECDC4'),
('Logement', 'Loyer et charges', '#45B7D1'),
('Santé', 'Médicaments et consultations', '#96CEB4'),
('Loisirs', 'Divertissements et hobbies', '#FFEAA7'),
('Éducation', 'Cours et formations', '#DDA15E'),
('Autre', 'Autres dépenses', '#BC6C25');

-- =====================================================
-- Insérer un utilisateur de test
-- =====================================================
INSERT INTO users (nom, prenom, email, telephone) VALUES
('Dupont', 'Jean', 'jean.dupont@email.com', '+33612345678');

-- =====================================================
-- Insérer des dépenses de test
-- =====================================================
INSERT INTO depenses (user_id, montant, description, date_depense, categorie) VALUES
(1, 45.50, 'Courses supermarché', '2026-02-01', 'Alimentation'),
(1, 65.00, 'Carburant essence', '2026-02-02', 'Transport'),
(1, 1200.00, 'Loyer février', '2026-02-01', 'Logement'),
(1, 35.00, 'Médicaments pharmacie', '2026-02-05', 'Santé'),
(1, 89.99, 'Cinéma et popcorn', '2026-02-08', 'Loisirs'),
(1, 120.00, 'Cours de guitare', '2026-02-10', 'Éducation'),
(1, 52.30, 'Fruits et légumes', '2026-02-11', 'Alimentation'),
(1, 40.00, 'Ticket transports', '2026-02-12', 'Transport');

-- =====================================================
-- Insérer des budgets de test
-- =====================================================
INSERT INTO budgets (user_id, categorie, montant_max, montant_actuel, mois, annee, description, date_creation) VALUES
(1, 'Alimentation', 300.00, 97.80, 2, 2026, 'Budget alimentation février', '2026-02-01'),
(1, 'Transport', 150.00, 65.00, 2, 2026, 'Budget transport février', '2026-02-01'),
(1, 'Logement', 1200.00, 1200.00, 2, 2026, 'Budget logement février', '2026-02-01'),
(1, 'Loisirs', 200.00, 89.99, 2, 2026, 'Budget loisirs février', '2026-02-01'),
(1, 'Santé', 100.00, 35.00, 2, 2026, 'Budget santé février', '2026-02-01');

-- =====================================================
-- Insérer des plannings de test
-- =====================================================
INSERT INTO plannings (user_id, nom, description, type, mois, annee, revenu_prevu, epargne_prevue, pourcentage_epargne, statut) VALUES
(1, 'Planning février 2026', 'Plan financier pour février', 'Personnel', 2, 2026, 3500.00, 700.00, 20, 'En cours'),
(1, 'Épargne urgence', 'Fonds d''urgence annuel', 'Personnel', 2, 2026, 5000.00, 1000.00, 20, 'En cours');

-- =====================================================
-- Vérifier les données insérées
-- =====================================================
SELECT COUNT(*) as total_users FROM users;
SELECT COUNT(*) as total_depenses FROM depenses;
SELECT COUNT(*) as total_budgets FROM budgets;
SELECT COUNT(*) as total_plannings FROM plannings;

