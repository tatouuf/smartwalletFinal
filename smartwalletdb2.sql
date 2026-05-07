-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : sam. 28 fév. 2026 à 21:42
-- Version du serveur : 10.4.28-MariaDB
-- Version de PHP : 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `smartwalletdb2`
--

-- --------------------------------------------------------

--
-- Structure de la table `amitie`
--

CREATE TABLE `amitie` (
  `id` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  `friendId` int(11) NOT NULL,
  `statut` enum('PENDING','ACCEPTED','BLOCKED') DEFAULT 'PENDING',
  `dateCreation` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `amitie`
--

INSERT INTO `amitie` (`id`, `userId`, `friendId`, `statut`, `dateCreation`) VALUES
(1, 1, 2, 'ACCEPTED', '2026-01-20 09:00:00'),
(2, 1, 3, 'ACCEPTED', '2026-01-21 10:30:00'),
(3, 2, 4, 'ACCEPTED', '2026-01-22 08:15:00'),
(4, 3, 5, 'PENDING', '2026-01-23 13:20:00'),
(5, 4, 6, 'ACCEPTED', '2026-01-24 15:45:00'),
(6, 5, 7, 'ACCEPTED', '2026-01-25 07:30:00'),
(7, 6, 8, 'BLOCKED', '2026-01-26 12:10:00'),
(8, 7, 9, 'PENDING', '2026-01-27 14:40:00'),
(9, 8, 10, 'ACCEPTED', '2026-01-28 09:50:00');

-- --------------------------------------------------------

--
-- Structure de la table `assurances`
--

CREATE TABLE `assurances` (
  `id` int(11) NOT NULL,
  `nom_assurance` varchar(100) NOT NULL,
  `type_assurance` enum('AUTO','SANTE','MAISON','VIE','HABITATION','AUTRE') NOT NULL,
  `description` text DEFAULT NULL,
  `prix` decimal(10,2) NOT NULL,
  `duree_mois` int(11) NOT NULL,
  `conditions` text DEFAULT NULL,
  `date_creation` timestamp NOT NULL DEFAULT current_timestamp(),
  `statut` enum('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
  `id_user` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `assurances`
--

INSERT INTO `assurances` (`id`, `nom_assurance`, `type_assurance`, `description`, `prix`, `duree_mois`, `conditions`, `date_creation`, `statut`, `id_user`) VALUES
(1, 'Assurance Auto Tous Risques - Tunis', 'AUTO', 'Couverture complète pour votre véhicule incluant collision, vol et incendie', 450.00, 12, 'Véhicule de moins de 5 ans, conducteur avec permis depuis plus de 2 ans', '2026-02-25 00:11:56', 'ACTIVE', 1),
(2, 'Assurance Santé Familiale - CNAM', 'SANTE', 'Couverture médicale complémentaire pour toute la famille', 680.00, 12, 'Couple avec enfants de moins de 18 ans, résidents en Tunisie', '2026-02-25 00:11:56', 'ACTIVE', 2),
(3, 'Assurance Habitation Premium - Ariana', 'HABITATION', 'Protection complète pour votre logement contre incendie, dégâts des eaux et vol', 320.00, 12, 'Logement principal avec système de sécurité à Tunis', '2026-02-25 00:11:56', 'ACTIVE', 3),
(4, 'Assurance Vie Épargne - Maghrebia', 'VIE', 'Contrat d\'assurance vie avec option épargne et garanties décès', 1200.00, 24, 'Âge entre 18 et 65 ans, examen médical requis en Tunisie', '2026-02-25 00:11:56', 'ACTIVE', 4),
(5, 'Assurance Maison Jardin - Sousse', 'MAISON', 'Couverture pour maison individuelle avec jardin et dépendances', 580.00, 12, 'Maison de moins de 200m² à Sousse ou Monastir', '2026-02-25 00:11:56', 'ACTIVE', 5),
(6, 'Assurance Auto Tiers - Sfax', 'AUTO', 'Couverture de base responsabilité civile obligatoire', 180.00, 12, 'Tous véhicules acceptés en Tunisie', '2026-02-25 00:11:56', 'ACTIVE', 6),
(7, 'Assurance Santé Individuelle - Bizerte', 'SANTE', 'Couverture santé individuelle avec options dentaire et optique', 350.00, 12, 'Âge entre 18 et 60 ans, résident à Bizerte', '2026-02-25 00:11:56', 'ACTIVE', 7),
(8, 'Assurance Multirisque Professionnelle', 'AUTRE', 'Assurance pour commerçants et artisans en Tunisie', 890.00, 12, 'Entreprise immatriculée en Tunisie', '2026-02-25 00:11:56', 'ACTIVE', 8);

-- --------------------------------------------------------

--
-- Structure de la table `bank_card`
--

CREATE TABLE `bank_card` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `card_holder` varchar(100) DEFAULT NULL,
  `card_number` varchar(20) DEFAULT NULL,
  `expiry_date` varchar(7) DEFAULT NULL,
  `cvv` varchar(4) DEFAULT NULL,
  `card_type` varchar(20) DEFAULT NULL,
  `rib` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `bank_card`
--

INSERT INTO `bank_card` (`id`, `user_id`, `card_holder`, `card_number`, `expiry_date`, `cvv`, `card_type`, `rib`) VALUES
(1, 1, 'MOHAMED BEN SALEM', '1234567812345678', '12/25', '123', 'VISA', 'TN59 1000 0000 1234 5678 9012 '),
(2, 2, 'FATMA BEN MAHMOUD', '8765432187654321', '06/24', '456', 'MASTERCARD', 'TN59 1000 0000 2345 6789 0123 '),
(3, 3, 'AHMED KHALIFA', '2345678923456789', '09/25', '789', 'VISA', 'TN59 1000 0000 3456 7890 1234 '),
(4, 4, 'SALMA TRABELSI', '3456789034567890', '03/25', '234', 'VISA', 'TN59 1000 0000 4567 8901 2345 '),
(5, 5, 'OMAR JELLOULI', '4567890145678901', '07/25', '567', 'MASTERCARD', 'TN59 1000 0000 5678 9012 3456 '),
(6, 6, 'NOUR BEN ALI', '5678901256789012', '11/24', '890', 'VISA', 'TN59 1000 0000 6789 0123 4567 '),
(7, 7, 'SOUHA BEN SALEM', '6789012367890123', '08/26', '123', 'MASTERCARD', 'TN59 1000 0000 7890 1234 5678 '),
(8, 8, 'YOUSSEF GHRIBI', '7890123478901234', '05/25', '456', 'VISA', 'TN59 1000 0000 8901 2345 6789 '),
(9, 9, 'LEILA MANSOUR', '8901234589012345', '10/26', '789', 'MASTERCARD', 'TN59 1000 0000 9012 3456 7890 '),
(10, 10, 'HATEM BOUAZIZI', '9012345690123456', '02/25', '234', 'VISA', 'TN59 1000 0000 0123 4567 8901 ');

-- --------------------------------------------------------

--
-- Structure de la table `budgets`
--

CREATE TABLE `budgets` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `categorie_id` int(11) DEFAULT NULL,
  `montant_max` decimal(10,2) DEFAULT NULL,
  `mois` int(11) DEFAULT NULL,
  `annee` int(11) DEFAULT NULL,
  `planning_id` int(11) DEFAULT NULL,
  `montant_actuel` double DEFAULT 0,
  `description` varchar(255) DEFAULT NULL,
  `date_creation` date DEFAULT NULL,
  `categorie` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `budgets`
--

INSERT INTO `budgets` (`id`, `user_id`, `categorie_id`, `montant_max`, `mois`, `annee`, `planning_id`, `montant_actuel`, `description`, `date_creation`, `categorie`) VALUES
(1, 0, NULL, 1200.00, 4, 2025, NULL, 0, 'TESTDESC', '2026-02-28', 'Transport');

-- --------------------------------------------------------

--
-- Structure de la table `categories`
--

CREATE TABLE `categories` (
  `id` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL,
  `type` enum('DEPENSE','REVENU') DEFAULT 'DEPENSE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `categories`
--

INSERT INTO `categories` (`id`, `nom`, `type`) VALUES
(1, 'Alimentation', 'DEPENSE'),
(2, 'Transport', 'DEPENSE'),
(3, 'Logement', 'DEPENSE'),
(4, 'Santé', 'DEPENSE'),
(5, 'Éducation', 'DEPENSE'),
(6, 'Loisirs', 'DEPENSE'),
(7, 'Vêtements', 'DEPENSE'),
(8, 'Factures', 'DEPENSE'),
(9, 'Salaire', 'REVENU'),
(10, 'Indépendant', 'REVENU'),
(11, 'Investissements', 'REVENU'),
(12, 'Loyer perçu', 'REVENU');

-- --------------------------------------------------------

--
-- Structure de la table `credit`
--

CREATE TABLE `credit` (
  `id_credit` int(11) NOT NULL,
  `nom_client` varchar(100) NOT NULL,
  `montant` decimal(10,2) NOT NULL,
  `date_credit` date NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `statut` enum('NON_REMBOURSE','REMBOURSE','PARTIELLEMENT_REMBOURSE') DEFAULT 'NON_REMBOURSE',
  `id_user` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `credit`
--

INSERT INTO `credit` (`id_credit`, `nom_client`, `montant`, `date_credit`, `description`, `statut`, `id_user`) VALUES
(1, 'Mohamed Ali Ben Salem', 15000.00, '2025-01-15', 'Achat véhicule d\'occasion à Tunis', 'NON_REMBOURSE', 1),
(2, 'Sarra Ben Mahmoud', 8500.00, '2025-02-20', 'Rénovation salle de bain à Sousse', 'NON_REMBOURSE', 2),
(3, 'Ahmed Khelifi', 5000.00, '2024-11-10', 'Achat matériel informatique pour entreprise', 'PARTIELLEMENT_REMBOURSE', 3),
(4, 'Nadia Gharbi', 25000.00, '2024-09-05', 'Travaux extension maison à Sfax', 'REMBOURSE', 4),
(5, 'Karim Mejri', 12000.00, '2025-01-30', 'Voyage d\'études à Paris', 'NON_REMBOURSE', 5),
(6, 'Henda Bouaziz', 7500.00, '2024-12-12', 'Frais médicaux clinique à Tunis', 'PARTIELLEMENT_REMBOURSE', 6),
(7, 'Sami Trabelsi', 3000.00, '2025-02-01', 'Achat électroménager pour nouvel appartement', 'NON_REMBOURSE', 7),
(8, 'Rim Jelliti', 18000.00, '2024-10-18', 'Achat terrain à Nabeul', 'REMBOURSE', 8),
(9, 'Fathi Dridi', 9500.00, '2025-01-05', 'Organisation mariage à Hammamet', 'PARTIELLEMENT_REMBOURSE', 9),
(10, 'Leila Chabchoub', 4500.00, '2024-12-28', 'Formation professionnelle en marketing digital', 'NON_REMBOURSE', 10);

-- --------------------------------------------------------

--
-- Structure de la table `depenses`
--

CREATE TABLE `depenses` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `planning_id` int(11) DEFAULT NULL,
  `categorie_id` int(11) DEFAULT NULL,
  `montant` decimal(10,2) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `date_depense` date DEFAULT curdate(),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `categorie` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `depenses`
--

INSERT INTO `depenses` (`id`, `user_id`, `planning_id`, `categorie_id`, `montant`, `description`, `date_depense`, `created_at`, `categorie`) VALUES
(1, 1, NULL, NULL, 1500.00, 'slslll', '2026-03-06', '2026-02-28 20:12:02', 'Transport');

-- --------------------------------------------------------

--
-- Structure de la table `finance_profile`
--

CREATE TABLE `finance_profile` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `monthly_income` decimal(12,2) NOT NULL DEFAULT 0.00,
  `current_balance` decimal(12,2) NOT NULL DEFAULT 0.00,
  `currency` varchar(10) NOT NULL DEFAULT 'TND',
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `finance_profile`
--

INSERT INTO `finance_profile` (`id`, `user_id`, `monthly_income`, `current_balance`, `currency`, `updated_at`) VALUES
(1, 1, 1200.00, 35.00, 'TND', '2026-02-23 03:20:52'),
(2, 11, 12000.00, 300.00, 'TND', '2026-02-28 19:07:53'),
(3, 12, 1800.00, 100.00, 'TND', '2026-02-28 19:18:37');

-- --------------------------------------------------------

--
-- Structure de la table `loan`
--

CREATE TABLE `loan` (
  `id` int(11) NOT NULL,
  `lender_id` int(11) NOT NULL,
  `borrower_id` int(11) NOT NULL,
  `principal_amount` double NOT NULL,
  `remaining_amount` double NOT NULL,
  `status` varchar(20) DEFAULT 'ACTIVE',
  `start_date` timestamp NULL DEFAULT current_timestamp(),
  `end_date` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `loan`
--

INSERT INTO `loan` (`id`, `lender_id`, `borrower_id`, `principal_amount`, `remaining_amount`, `status`, `start_date`, `end_date`) VALUES
(1, 1, 3, 300, 300, 'ACTIVE', '2026-02-10 22:00:00', '2026-02-17 22:00:00'),
(2, 2, 5, 500, 350, 'ACTIVE', '2026-02-12 22:00:00', '2026-02-26 22:00:00'),
(3, 9, 6, 150, 100, 'ACTIVE', '2026-02-15 22:00:00', '2026-02-22 22:00:00'),
(4, 4, 8, 200, 200, 'ACTIVE', '2026-02-14 22:00:00', '2026-02-27 22:00:00');

-- --------------------------------------------------------

--
-- Structure de la table `loan_payment`
--

CREATE TABLE `loan_payment` (
  `id` int(11) NOT NULL,
  `loan_id` int(11) NOT NULL,
  `payer_id` int(11) NOT NULL,
  `receiver_id` int(11) NOT NULL,
  `amount_paid` double NOT NULL,
  `payment_date` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `loan_payment`
--

INSERT INTO `loan_payment` (`id`, `loan_id`, `payer_id`, `receiver_id`, `amount_paid`, `payment_date`) VALUES
(1, 2, 5, 2, 150, '2026-02-15 12:30:00'),
(2, 3, 6, 9, 50, '2026-02-17 09:20:00');

-- --------------------------------------------------------

--
-- Structure de la table `loan_request`
--

CREATE TABLE `loan_request` (
  `id` int(11) NOT NULL,
  `borrower_id` int(11) NOT NULL,
  `lender_id` int(11) NOT NULL,
  `amount` double NOT NULL,
  `message` text DEFAULT NULL,
  `status` varchar(20) DEFAULT 'PENDING',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `responded_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `loan_request`
--

INSERT INTO `loan_request` (`id`, `borrower_id`, `lender_id`, `amount`, `message`, `status`, `created_at`, `responded_at`) VALUES
(1, 3, 1, 300, 'Salam Mohamed, je peux emprunter 300 TND pour acheter un cadeau pour l\'Aïd ?', 'ACCEPTED', '2026-02-10 12:30:00', '2026-02-17 08:36:28'),
(2, 5, 2, 500, 'Bonjour Fatma, j\'ai besoin de 500 TND pour réparer ma voiture à Sousse', 'ACCEPTED', '2026-02-12 07:15:00', '2026-02-13 09:20:00'),
(3, 8, 4, 200, 'Salut Salma, tu peux me prêter 200 TND jusqu\'à la fin du mois pour mon loyer ?', 'ACCEPTED', '2026-02-14 09:40:00', '2026-02-15 08:30:00'),
(4, 2, 7, 1000, 'Souha, je voudrais emprunter 1000 TND pour un voyage à Djerba', 'REJECTED', '2026-02-15 14:20:00', '2026-02-16 10:45:00'),
(5, 6, 9, 150, 'Leila, je peux te demander 150 TND pour mes cours d\'anglais ?', 'ACCEPTED', '2026-02-16 08:00:00', '2026-02-17 13:20:00'),
(6, 10, 1, 400, 'Mohamed, j\'ai besoin de 400 TND pour payer les fournitures scolaires des enfants', 'PENDING', '2026-02-17 06:30:00', NULL),
(7, 2, 4, 1555, 'salefni', 'PENDING', '2026-02-26 16:52:46', NULL);

-- --------------------------------------------------------

--
-- Structure de la table `notifications`
--

CREATE TABLE `notifications` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `title` varchar(150) NOT NULL,
  `message` text NOT NULL,
  `type` varchar(20) NOT NULL DEFAULT 'PENDING',
  `status` varchar(20) NOT NULL DEFAULT 'UNREAD',
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `recurring_id` int(11) DEFAULT NULL,
  `reminder_id` int(11) DEFAULT NULL,
  `is_read` tinyint(1) DEFAULT 0,
  `related_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `notifications`
--

INSERT INTO `notifications` (`id`, `user_id`, `title`, `message`, `type`, `status`, `created_at`, `recurring_id`, `reminder_id`, `is_read`, `related_id`) VALUES
(1, 1, 'Demande de prêt acceptée', 'Votre demande de prêt de 300 TND à Ahmed Khalifa a été acceptée', 'INFO', 'UNREAD', '2026-02-17 09:36:28', NULL, NULL, 0, NULL),
(2, 3, 'Remboursement reçu', 'Vous avez reçu un remboursement de 150 TND de Omar Jellouli', 'SUCCESS', 'READ', '2026-02-15 14:30:00', NULL, NULL, 1, NULL);

-- --------------------------------------------------------

--
-- Structure de la table `plannings`
--

CREATE TABLE `plannings` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `type` enum('MENSUEL','TRIMESTRIEL','ANNUEL') DEFAULT 'MENSUEL',
  `mois` int(11) DEFAULT NULL CHECK (`mois` between 1 and 12),
  `annee` year(4) DEFAULT NULL,
  `revenu_prevu` decimal(10,2) DEFAULT 0.00,
  `epargne_prevue` decimal(10,2) DEFAULT 0.00,
  `pourcentage_epargne` tinyint(4) DEFAULT 20,
  `statut` enum('EN_COURS','TERMINE','ANNULE') DEFAULT 'EN_COURS',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `plannings`
--

INSERT INTO `plannings` (`id`, `user_id`, `nom`, `description`, `type`, `mois`, `annee`, `revenu_prevu`, `epargne_prevue`, `pourcentage_epargne`, `statut`, `created_at`, `updated_at`) VALUES
(1, 0, 'Test', 'Test', 'MENSUEL', 1, '2026', 1550.00, 1120.00, 10, NULL, '2026-02-28 20:02:47', '2026-02-28 20:02:47');

-- --------------------------------------------------------

--
-- Structure de la table `reclamation`
--

CREATE TABLE `reclamation` (
  `id` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  `adminId` int(11) NOT NULL,
  `message` text NOT NULL,
  `statut` enum('PENDING','IN_PROGRESS','RESOLVED') NOT NULL DEFAULT 'PENDING',
  `reponse` text DEFAULT NULL,
  `dateEnvoi` datetime NOT NULL,
  `dateReponse` datetime DEFAULT NULL,
  `is_urgent` tinyint(1) DEFAULT 0,
  `category` varchar(100) DEFAULT NULL,
  `sentiment` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `reclamation`
--

INSERT INTO `reclamation` (`id`, `userId`, `adminId`, `message`, `statut`, `reponse`, `dateEnvoi`, `dateReponse`, `is_urgent`, `category`, `sentiment`) VALUES
(1, 3, 1, 'Problème de connexion à mon compte depuis hier', 'RESOLVED', 'Nous avons résolu le problème, veuillez réessayer maintenant', '2026-02-15 10:30:00', '2026-02-15 14:20:00', 1, 'TECHNIQUE', 'neutre'),
(2, 5, 1, 'Transaction non reçue de 150 TND', 'IN_PROGRESS', 'Nous vérifions votre transaction avec la banque', '2026-02-16 09:15:00', '2026-02-16 11:30:00', 1, 'FINANCIER', 'negatif'),
(3, 8, 1, 'Question sur les frais de transfert', 'PENDING', NULL, '2026-02-17 16:45:00', NULL, 0, 'QUESTION', 'positif');

-- --------------------------------------------------------

--
-- Structure de la table `recurring_payments`
--

CREATE TABLE `recurring_payments` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `profile_id` int(11) DEFAULT NULL,
  `name` varchar(120) NOT NULL,
  `amount` decimal(12,2) NOT NULL,
  `frequency` enum('WEEKLY','MONTHLY','YEARLY') NOT NULL,
  `next_payment_date` date NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `recurring_payments`
--

INSERT INTO `recurring_payments` (`id`, `user_id`, `profile_id`, `name`, `amount`, `frequency`, `next_payment_date`, `is_active`, `created_at`) VALUES
(1, 1, NULL, 'Netflix', 45.00, 'MONTHLY', '2031-05-05', 1, '2026-02-22 23:46:07'),
(3, 1, NULL, 'Gym', 80.00, 'MONTHLY', '2027-03-15', 1, '2026-02-22 23:46:07'),
(4, 1, NULL, 'Netflix', 45.00, 'MONTHLY', '2027-06-05', 1, '2026-02-26 16:58:53'),
(5, 1, NULL, 'Running Shoes', 1500.00, 'MONTHLY', '2026-03-27', 1, '2026-02-27 13:35:27'),
(6, 1, 0, 'GYM2', 59.00, 'YEARLY', '2026-02-28', 1, '2026-02-27 15:49:37'),
(7, 1, 0, 'net2', 65.00, 'YEARLY', '2026-03-07', 1, '2026-02-27 15:50:31'),
(8, 1, 0, 'tEST', 15.00, 'YEARLY', '2026-02-28', 1, '2026-02-27 15:50:43'),
(9, 11, 0, 'GYM', 50.00, 'MONTHLY', '2027-11-08', 1, '2026-02-28 19:08:10'),
(10, 12, 0, 'GYM', 45.00, 'YEARLY', '2027-12-07', 1, '2026-02-28 19:18:51'),
(11, 11, 0, 'Netflix', 50.00, 'YEARLY', '2030-10-06', 1, '2026-02-28 19:52:57');

-- --------------------------------------------------------

--
-- Structure de la table `reminders`
--

CREATE TABLE `reminders` (
  `id` int(11) NOT NULL,
  `recurring_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `remind_before_days` int(11) NOT NULL DEFAULT 1,
  `is_enabled` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `services`
--

CREATE TABLE `services` (
  `id` int(11) NOT NULL,
  `prix` float NOT NULL,
  `description` text DEFAULT NULL,
  `type` varchar(100) DEFAULT NULL,
  `statut` enum('DISPONIBLE','NON_DISPONIBLE') NOT NULL,
  `id_user` int(11) DEFAULT NULL,
  `localisation` point DEFAULT NULL,
  `adresse` varchar(255) DEFAULT NULL,
  `TypeService` enum('voiture','maison','','') NOT NULL,
  `image` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `services`
--

INSERT INTO `services` (`id`, `prix`, `description`, `type`, `statut`, `id_user`, `localisation`, `adresse`, `TypeService`, `image`) VALUES
(1, 120, 'Lavage complet intérieur/extérieur avec cirage - Tunis', 'Auto', 'DISPONIBLE', 1, NULL, '15 Rue de Carthage, Tunis', 'voiture', 'lavage_auto.jpg'),
(2, 250, 'Révision moteur complète et vidange - Sfax', 'Auto', 'DISPONIBLE', 2, NULL, '5 Avenue Habib Bourguiba, Sfax', 'voiture', 'revision_moteur.jpg'),
(3, 180, 'Diagnostic électronique et réparation - Sousse', 'Auto', 'DISPONIBLE', 3, NULL, '8 Rue de la Liberté, Sousse', 'voiture', 'diagnostic.jpg'),
(4, 350, 'Nettoyage en profondeur tapis et canapés - Tunis', 'Maison', 'DISPONIBLE', 4, NULL, '12 Rue des Jasmins, La Marsa', 'maison', 'nettoyage_tapis.jpg'),
(5, 450, 'Peinture intérieure complète 3 pièces - Hammamet', 'Maison', 'DISPONIBLE', 5, NULL, '25 Avenue de la Plage, Hammamet', 'maison', 'peinture.jpg'),
(6, 190, 'Réparation plomberie (fuites, robinets) - Nabeul', 'Maison', 'DISPONIBLE', 6, NULL, '7 Rue du Printemps, Nabeul', 'maison', 'plomberie.jpg'),
(7, 80, 'Traitement anti-parasites intérieur - Bizerte', 'Maison', 'DISPONIBLE', 7, NULL, '3 Rue des Oliviers, Bizerte', 'maison', 'anti_parasites.jpg'),
(8, 220, 'Changement de pneus et équilibrage - Monastir', 'Auto', 'DISPONIBLE', 8, NULL, '18 Rue de l\'Industrie, Monastir', 'voiture', 'pneus.jpg'),
(9, 300, 'Rénovation salle de bain (carrelage) - Sousse', 'Maison', 'DISPONIBLE', 9, NULL, '10 Rue des Citronniers, Sousse', 'maison', 'renovation_sdb.jpg'),
(10, 150, 'Climatisation: entretien et recharge - Tunis', 'Auto', 'DISPONIBLE', 10, NULL, '22 Rue du Port, La Goulette', 'voiture', 'climatisation.jpg');

-- --------------------------------------------------------

--
-- Structure de la table `stripe_transactions`
--

CREATE TABLE `stripe_transactions` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `profile_id` int(11) DEFAULT NULL,
  `stripe_payment_intent_id` varchar(255) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `currency` varchar(10) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `stripe_transactions`
--

INSERT INTO `stripe_transactions` (`id`, `user_id`, `profile_id`, `stripe_payment_intent_id`, `amount`, `currency`, `status`, `created_at`) VALUES
(1, 1, 1, 'pi_3T5Qz6HCO8NiAAcd12oPlRE3', 10, 'usd', 'SUCCEEDED', '2026-02-27 12:21:31'),
(2, 1, 1, 'pi_3T5R24HCO8NiAAcd1h3g7ONh', 10, 'usd', 'SUCCEEDED', '2026-02-27 12:24:36'),
(3, 1, 1, 'pi_3T5R2tHCO8NiAAcd1Ln3IdOh', 1000, 'usd', 'SUCCEEDED', '2026-02-27 12:25:27'),
(4, 1, 0, 'pi_3T5RLyHCO8NiAAcd0Nwx7ZqV', 45, 'usd', 'SUCCEEDED', '2026-02-27 12:45:10'),
(5, 1, 0, 'pi_3T5RMxHCO8NiAAcd0mldiYp9', 1500, 'usd', 'SUCCEEDED', '2026-02-27 12:46:11'),
(6, 1, 0, 'pi_3T5RNlHCO8NiAAcd1nsp3TC7', 90, 'usd', 'SUCCEEDED', '2026-02-27 12:47:01'),
(7, 1, 0, 'pi_3T5RQYHCO8NiAAcd1VrCg0ZY', 540, 'usd', 'SUCCEEDED', '2026-02-27 12:49:53'),
(8, 1, 0, 'pi_3T5RQqHCO8NiAAcd1b7LRIx5', 225, 'usd', 'SUCCEEDED', '2026-02-27 12:50:12'),
(9, 1, 0, 'pi_3T5RQzHCO8NiAAcd0hm9ZHvz', 45, 'usd', 'SUCCEEDED', '2026-02-27 12:50:21'),
(10, 1, 0, 'pi_3T5RfCHCO8NiAAcd1rbyAru6', 540, 'usd', 'SUCCEEDED', '2026-02-27 13:05:02'),
(11, 1, 0, 'pi_3T5RibHCO8NiAAcd0mW6MyN9', 225, 'usd', 'SUCCEEDED', '2026-02-27 13:08:33'),
(12, 1, 0, 'pi_3T5RrjHCO8NiAAcd1JKAfWPE', 810, 'usd', 'SUCCEEDED', '2026-02-27 13:17:58'),
(13, 1, 0, 'pi_3T5RsXHCO8NiAAcd0ByGsqKI', 810, 'usd', 'SUCCEEDED', '2026-02-27 13:18:49'),
(14, 1, 0, 'pi_3T5SMfHCO8NiAAcd1v0Aw7Aw', 45, 'usd', 'SUCCEEDED', '2026-02-27 13:49:56'),
(15, 1, 0, 'pi_3T5T31HCO8NiAAcd0ycUslz1', 90, 'usd', 'SUCCEEDED', '2026-02-27 14:33:43'),
(16, 1, 0, 'pi_3T5muGHCO8NiAAcd0k5FKfSh', 960, 'usd', 'SUCCEEDED', '2026-02-28 11:46:05'),
(17, 11, 0, 'pi_3T5rw6HCO8NiAAcd1Su6td0y', 50, 'usd', 'SUCCEEDED', '2026-02-28 17:08:17'),
(18, 11, 0, 'pi_3T5ryXHCO8NiAAcd0x6blr9J', 300, 'usd', 'SUCCEEDED', '2026-02-28 17:10:48'),
(19, 12, 0, 'pi_3T5s6UHCO8NiAAcd0rRMXXN0', 45, 'usd', 'SUCCEEDED', '2026-02-28 17:19:00'),
(20, 12, 0, 'pi_3T5s6pHCO8NiAAcd1PyvFrge', 15, 'usd', 'SUCCEEDED', '2026-02-28 17:19:22'),
(21, 12, 0, 'pi_3T5s7DHCO8NiAAcd1j64lT4x', 18.75, 'usd', 'SUCCEEDED', '2026-02-28 17:19:45'),
(22, 11, 0, 'pi_3T5sdQHCO8NiAAcd0zwup2nk', 50, 'usd', 'SUCCEEDED', '2026-02-28 17:53:03'),
(23, 11, 0, 'pi_3T5sdlHCO8NiAAcd1adfE8wZ', 50, 'usd', 'SUCCEEDED', '2026-02-28 17:53:24'),
(24, 11, 0, 'pi_3T5se1HCO8NiAAcd1dc0TbY9', 25, 'usd', 'SUCCEEDED', '2026-02-28 17:53:40'),
(25, 11, 0, 'pi_3T5seQHCO8NiAAcd12JEAD2O', 4.166666666666667, 'usd', 'SUCCEEDED', '2026-02-28 17:54:05'),
(26, 11, 0, 'pi_3T5sesHCO8NiAAcd0ebsqZGC', 50, 'usd', 'SUCCEEDED', '2026-02-28 17:54:33'),
(27, 11, 0, 'pi_3T5sf5HCO8NiAAcd0nErvNoW', 50, 'usd', 'SUCCEEDED', '2026-02-28 17:54:46'),
(28, 11, 0, 'pi_3T5sfAHCO8NiAAcd1luQNCzj', 50, 'usd', 'SUCCEEDED', '2026-02-28 17:54:51'),
(29, 11, 0, 'pi_3T5sfIHCO8NiAAcd0AJY4V37', 600, 'usd', 'SUCCEEDED', '2026-02-28 17:54:59');

-- --------------------------------------------------------

--
-- Structure de la table `transaction`
--

CREATE TABLE `transaction` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `target` varchar(120) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `transaction`
--

INSERT INTO `transaction` (`id`, `user_id`, `type`, `amount`, `target`, `created_at`) VALUES
(1, 1, 'DEPOT', 2500, 'Salaire mois Février', '2026-02-01 08:30:00'),
(2, 1, 'VIREMENT', 150, 'Transfert à Fatma Ben Mahmoud', '2026-02-05 12:25:00'),
(3, 2, 'DEPOT', 1800, 'Paiement projet freelance', '2026-02-03 09:15:00'),
(4, 3, 'VIREMENT', 500, 'Remboursement Omar Jellouli', '2026-02-07 14:40:00'),
(5, 4, 'PAIEMENT', 220, 'Facture Tunisie Telecom', '2026-02-10 09:20:00'),
(6, 5, 'PAIEMENT', 135.5, 'Courses Monoprix', '2026-02-12 06:30:00'),
(7, 6, 'PAIEMENT', 65.8, 'Restaurant Le Sultan', '2026-02-14 11:45:00'),
(8, 7, 'DEPOT', 3000, 'Vente voiture', '2026-02-15 07:00:00'),
(9, 8, 'VIREMENT', 200, 'Anniversaire cousin', '2026-02-16 13:20:00'),
(10, 9, 'RETRAIT', 400, 'Retrait distributeur', '2026-02-17 08:10:00'),
(11, 10, 'PAIEMENT', 89.9, 'Essence station TOTAL', '2026-02-18 18:30:00'),
(12, 1, 'DEPOT', 600, 'Remboursement Ahmed', '2026-02-19 07:45:00');

-- --------------------------------------------------------

--
-- Structure de la table `transactions`
--

CREATE TABLE `transactions` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `amount` decimal(12,2) NOT NULL,
  `type` enum('INCOME','EXPENSE') NOT NULL,
  `category` varchar(100) DEFAULT NULL,
  `transaction_date` date NOT NULL,
  `description` text DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `fullname` varchar(100) GENERATED ALWAYS AS (concat(`nom`,' ',`prenom`)) STORED,
  `telephone` varchar(20) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','USER') DEFAULT 'USER',
  `status` enum('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
  `is_actif` tinyint(1) DEFAULT 0,
  `date_creation` datetime DEFAULT current_timestamp(),
  `date_update` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`id`, `nom`, `prenom`, `telephone`, `email`, `password`, `role`, `status`, `is_actif`, `date_creation`, `date_update`) VALUES
(1, 'Ben Salem', 'Mohamed', '98 765 432', 'mohamed.bensalem@email.tn', '$2a$10$nvAkDS0aSw5zPbBK57p48O.O6gAYdjZt36P6Y5KvWHj17jxE/RH7m', 'ADMIN', 'APPROVED', 1, '2026-01-15 09:00:00', '2026-02-26 15:46:07'),
(2, 'Ben Mahmoud', 'Fatma', '99250025', 'souha.said@esprit.tn', '$2a$10$4E6Lmq4uw/mtLczSH.y6cePuldWd4GEvTAXC4jLdHf0E6ncDPwOdK', 'USER', 'APPROVED', 1, '2026-01-16 10:30:00', '2026-02-26 17:51:32'),
(3, 'Khalifa', 'Ahmed', '96 543 210', 'ahmed.khalifa@email.tn', '$2a$10$nvAkDS0aSw5zPbBK57p48O.O6gAYdjZt36P6Y5KvWHj17jxE/RH7m', 'USER', 'APPROVED', 1, '2026-01-17 11:45:00', '2026-02-26 15:46:07'),
(4, 'Trabelsi', 'Salma', '95 432 109', 'salma.trabelsi@email.tn', '$2a$10$nvAkDS0aSw5zPbBK57p48O.O6gAYdjZt36P6Y5KvWHj17jxE/RH7m', 'USER', 'APPROVED', 1, '2026-01-18 09:15:00', '2026-02-26 15:46:07'),
(5, 'Jellouli', 'Omar', '94 321 098', 'omar.jellouli@email.tn', '$2a$10$nvAkDS0aSw5zPbBK57p48O.O6gAYdjZt36P6Y5KvWHj17jxE/RH7m', 'USER', 'APPROVED', 1, '2026-01-19 14:20:00', '2026-02-26 15:46:07'),
(6, 'Ben Ali', 'Nour', '93 210 987', 'nour.benali@email.tn', '$2a$10$nvAkDS0aSw5zPbBK57p48O.O6gAYdjZt36P6Y5KvWHj17jxE/RH7m', 'USER', 'APPROVED', 1, '2026-01-20 08:30:00', '2026-02-26 15:46:07'),
(7, 'Ben Salem', 'Souha', '92 109 876', 'souha.bensalem@email.tn', '$2a$10$nvAkDS0aSw5zPbBK57p48O.O6gAYdjZt36P6Y5KvWHj17jxE/RH7m', 'USER', 'APPROVED', 1, '2026-01-21 10:00:00', '2026-02-26 15:46:07'),
(8, 'Ghribi', 'Youssef', '91 098 765', 'youssef.ghribi@email.tn', '$2a$10$nvAkDS0aSw5zPbBK57p48O.O6gAYdjZt36P6Y5KvWHj17jxE/RH7m', 'USER', 'APPROVED', 1, '2026-01-22 13:45:00', '2026-02-26 15:46:07'),
(9, 'Mansour', 'Leila', '90 987 654', 'leila.mansour@email.tn', '$2a$10$nvAkDS0aSw5zPbBK57p48O.O6gAYdjZt36P6Y5KvWHj17jxE/RH7m', 'USER', 'APPROVED', 1, '2026-01-23 15:30:00', '2026-02-26 15:46:07'),
(10, 'Bouazizi', 'Hatem', '89 876 543', 'hatem.bouazizi@email.tn', '$2a$10$nvAkDS0aSw5zPbBK57p48O.O6gAYdjZt36P6Y5KvWHj17jxE/RH7m', 'USER', 'PENDING', 0, '2026-01-24 09:00:00', '2026-02-26 15:46:07'),
(11, 'Melek', 'melek', '95211220', 'melek.guesmi@esprit.tn', '$2a$10$VnGEuirPxU/.rhH7zm51r.dAi1841hL3oj4dWxoAffQxHO12UeLOK', 'USER', 'APPROVED', 1, '2026-02-28 17:06:16', '2026-02-28 18:07:32'),
(12, 'melek', 'TEST', '95211220', 'guesmimelek928@gmail.com', '$2a$10$WbscjsrRgl546SEQRPlgWuREI44J.rtmy5XCVw97RTc83GOQVDSJ.', 'USER', 'APPROVED', 1, '2026-02-28 18:16:17', '2026-02-28 19:16:34');

-- --------------------------------------------------------

--
-- Structure de la table `wallet`
--

CREATE TABLE `wallet` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `balance` double DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `wallet`
--

INSERT INTO `wallet` (`id`, `user_id`, `balance`) VALUES
(1, 1, 4500),
(2, 2, 2800.5),
(3, 3, 1200.75),
(4, 4, 3500),
(5, 5, 1800.25),
(6, 6, 2200),
(7, 7, 5000),
(8, 8, 950.8),
(9, 9, 3100.4),
(10, 10, 500);

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `amitie`
--
ALTER TABLE `amitie`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_ami` (`userId`,`friendId`),
  ADD KEY `friendId` (`friendId`);

--
-- Index pour la table `assurances`
--
ALTER TABLE `assurances`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `bank_card`
--
ALTER TABLE `bank_card`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `rib` (`rib`),
  ADD KEY `user_id` (`user_id`);

--
-- Index pour la table `budgets`
--
ALTER TABLE `budgets`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `credit`
--
ALTER TABLE `credit`
  ADD PRIMARY KEY (`id_credit`);

--
-- Index pour la table `depenses`
--
ALTER TABLE `depenses`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `finance_profile`
--
ALTER TABLE `finance_profile`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_profile_user` (`user_id`);

--
-- Index pour la table `loan`
--
ALTER TABLE `loan`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_loan_lender` (`lender_id`),
  ADD KEY `fk_loan_borrower` (`borrower_id`);

--
-- Index pour la table `loan_payment`
--
ALTER TABLE `loan_payment`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_payment_loan` (`loan_id`),
  ADD KEY `fk_payment_payer` (`payer_id`),
  ADD KEY `fk_payment_receiver` (`receiver_id`);

--
-- Index pour la table `loan_request`
--
ALTER TABLE `loan_request`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_request_borrower` (`borrower_id`),
  ADD KEY `fk_request_lender` (`lender_id`);

--
-- Index pour la table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_notif_user` (`user_id`),
  ADD KEY `idx_notif_created` (`created_at`),
  ADD KEY `idx_notif_status` (`status`),
  ADD KEY `recurring_id` (`recurring_id`),
  ADD KEY `reminder_id` (`reminder_id`);

--
-- Index pour la table `plannings`
--
ALTER TABLE `plannings`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `reclamation`
--
ALTER TABLE `reclamation`
  ADD PRIMARY KEY (`id`),
  ADD KEY `userId` (`userId`),
  ADD KEY `adminId` (`adminId`);

--
-- Index pour la table `recurring_payments`
--
ALTER TABLE `recurring_payments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Index pour la table `reminders`
--
ALTER TABLE `reminders`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_rem_user` (`user_id`),
  ADD KEY `idx_rem_rec` (`recurring_id`);

--
-- Index pour la table `services`
--
ALTER TABLE `services`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `stripe_transactions`
--
ALTER TABLE `stripe_transactions`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `transaction`
--
ALTER TABLE `transaction`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Index pour la table `transactions`
--
ALTER TABLE `transactions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `idx_email` (`email`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_role` (`role`);

--
-- Index pour la table `wallet`
--
ALTER TABLE `wallet`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `amitie`
--
ALTER TABLE `amitie`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT pour la table `assurances`
--
ALTER TABLE `assurances`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT pour la table `bank_card`
--
ALTER TABLE `bank_card`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT pour la table `budgets`
--
ALTER TABLE `budgets`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT pour la table `credit`
--
ALTER TABLE `credit`
  MODIFY `id_credit` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT pour la table `depenses`
--
ALTER TABLE `depenses`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `finance_profile`
--
ALTER TABLE `finance_profile`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `loan`
--
ALTER TABLE `loan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT pour la table `loan_payment`
--
ALTER TABLE `loan_payment`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pour la table `loan_request`
--
ALTER TABLE `loan_request`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT pour la table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pour la table `plannings`
--
ALTER TABLE `plannings`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `reclamation`
--
ALTER TABLE `reclamation`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `recurring_payments`
--
ALTER TABLE `recurring_payments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT pour la table `reminders`
--
ALTER TABLE `reminders`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `services`
--
ALTER TABLE `services`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT pour la table `stripe_transactions`
--
ALTER TABLE `stripe_transactions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT pour la table `transaction`
--
ALTER TABLE `transaction`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT pour la table `transactions`
--
ALTER TABLE `transactions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT pour la table `wallet`
--
ALTER TABLE `wallet`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `amitie`
--
ALTER TABLE `amitie`
  ADD CONSTRAINT `amitie_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `amitie_ibfk_2` FOREIGN KEY (`friendId`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `bank_card`
--
ALTER TABLE `bank_card`
  ADD CONSTRAINT `bank_card_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `finance_profile`
--
ALTER TABLE `finance_profile`
  ADD CONSTRAINT `finance_profile_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `reclamation`
--
ALTER TABLE `reclamation`
  ADD CONSTRAINT `reclamation_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `recurring_payments`
--
ALTER TABLE `recurring_payments`
  ADD CONSTRAINT `recurring_payments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `reminders`
--
ALTER TABLE `reminders`
  ADD CONSTRAINT `reminders_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `transaction`
--
ALTER TABLE `transaction`
  ADD CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `transactions`
--
ALTER TABLE `transactions`
  ADD CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `wallet`
--
ALTER TABLE `wallet`
  ADD CONSTRAINT `wallet_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
