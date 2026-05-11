package esprit.tn.chayma.services;

import esprit.tn.chayma.entities.Notification;
import esprit.tn.chayma.entities.Budget;
import esprit.tn.chayma.entities.Planning;
import esprit.tn.chayma.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    private final Connection conn;

    public NotificationService() {
        this.conn = MyDataBase.getInstance().getConnection();
    }

    public boolean add(Notification n) {
        String sql = "INSERT INTO notifications (user_id, type, message, is_read, created_at, related_id) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP(), ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, n.getUserId());
            ps.setString(2, n.getType());
            ps.setString(3, n.getMessage());
            ps.setBoolean(4, n.isRead());
            if (n.getRelatedId() != null) ps.setInt(5, n.getRelatedId()); else ps.setNull(5, java.sql.Types.INTEGER);
            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) n.setId(keys.getInt(1));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Notification> listByUser(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT id, user_id, type, message, is_read, created_at, related_id FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getInt("id"));
                n.setUserId(rs.getInt("user_id"));
                n.setType(rs.getString("type"));
                n.setMessage(rs.getString("message"));
                n.setRead(rs.getBoolean("is_read"));
                java.sql.Timestamp c = rs.getTimestamp("created_at");
                if (c != null) n.setCreatedAt(c.toLocalDateTime());
                n.setRelatedId(rs.getObject("related_id") != null ? rs.getInt("related_id") : null);
                list.add(n);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Notification> listUnreadByUser(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT id, user_id, type, message, is_read, created_at, related_id FROM notifications WHERE user_id = ? AND is_read = 0 ORDER BY created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getInt("id"));
                n.setUserId(rs.getInt("user_id"));
                n.setType(rs.getString("type"));
                n.setMessage(rs.getString("message"));
                n.setRead(rs.getBoolean("is_read"));
                java.sql.Timestamp c = rs.getTimestamp("created_at");
                if (c != null) n.setCreatedAt(c.toLocalDateTime());
                n.setRelatedId(rs.getObject("related_id") != null ? rs.getInt("related_id") : null);
                list.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) as count FROM notifications WHERE user_id = ? AND is_read = 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean markAsRead(int id) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markAllAsRead(int userId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM notifications WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========== INTELLIGENT NOTIFICATIONS METHODS ==========

    /**
     * Vérifie le dépassement du budget pour une dépense ajoutée
     * Calcule le total réel des dépenses pour la catégorie/mois/année
     * et crée une notification si dépassement détecté
     */
    public void checkBudgetExceeded(int userId, String categorie, double montantAjoute, int mois, int annee) {
        try {
            BudgetService budgetService = new BudgetService();
            Budget budget = budgetService.getByUserCategoryMonthYear(userId, categorie, mois, annee);

            if (budget != null && budget.getMontantMax() > 0) {
                // Calculer le total réel des dépenses pour cette catégorie/mois/année
                double totalDepenses = calculateTotalExpenses(userId, categorie, mois, annee);
                double montantMax = budget.getMontantMax();
                double pourcentageUtilise = (totalDepenses / montantMax) * 100;

                System.out.println("[NOTIFICATION] Catégorie: " + categorie + ", Total: " + totalDepenses +
                                 ", Max: " + montantMax + ", % : " + pourcentageUtilise);

                // Notification de dépassement total
                if (totalDepenses > montantMax) {
                    double depassement = totalDepenses - montantMax;
                    String msg = String.format("⚠️ DÉPASSEMENT BUDGET: %s\n" +
                            "Montant utilisé: %.2f DT\n" +
                            "Budget limite: %.2f DT\n" +
                            "Dépassement: %.2f DT",
                            categorie, totalDepenses, montantMax, depassement);

                    System.out.println("[NOTIFICATION] Création notification DÉPASSEMENT: " + msg);
                    Notification n = new Notification(userId, "depassement_budget", msg, budget.getId());
                    boolean added = add(n);
                    System.out.println("[NOTIFICATION] Enregistrement en BD: " + (added ? "✓ OK" : "✗ ERREUR"));
                }
                // Alerte si 80% du budget atteint (mais pas dépassé)
                else if (pourcentageUtilise >= 80 && pourcentageUtilise < 100) {
                    String msg = String.format("⚠️ BUDGET PRESQUE ATTEINT: %s\n" +
                            "Utilisation: %.0f%%\n" +
                            "Montant utilisé: %.2f DT sur %.2f DT",
                            categorie, pourcentageUtilise, totalDepenses, montantMax);

                    System.out.println("[NOTIFICATION] Création notification BUDGET PRESQUE ATTEINT: " + msg);
                    Notification n = new Notification(userId, "budget_warning", msg, budget.getId());
                    boolean added = add(n);
                    System.out.println("[NOTIFICATION] Enregistrement en BD: " + (added ? "✓ OK" : "✗ ERREUR"));
                }
            }
        } catch (Exception ex) {
            System.err.println("[NOTIFICATION] ERREUR dans checkBudgetExceeded: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Calcule le total réel des dépenses pour une catégorie donnée
     * pendant le mois et année spécifiés
     */
    private double calculateTotalExpenses(int userId, String categorie, int mois, int annee) {
        double total = 0;
        String sql = "SELECT SUM(montant) as total FROM depenses " +
                     "WHERE user_id = ? AND categorie = ? AND MONTH(date_depense) = ? AND YEAR(date_depense) = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, categorie);
            ps.setInt(3, mois);
            ps.setInt(4, annee);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Object result = rs.getObject("total");
                if (result != null) {
                    total = rs.getDouble("total");
                }
            }
            System.out.println("[DB] Total dépenses calculé: " + total + " pour " + categorie + " " + mois + "/" + annee);
        } catch (SQLException e) {
            System.err.println("[DB] ERREUR calcul total dépenses: " + e.getMessage());
            e.printStackTrace();
        }

        return total;
    }

    /**
     * Vérifie un objectif de planning non respecté
     */
    public void checkPlanningGoal(int userId, Planning planning) {
        if (planning != null && planning.getEpargnePrevue() > 0) {
            // Logique de vérification si l'épargne prévue n'est pas atteinte
            String msg = String.format("📊 OBJECTIF NON RESPECTÉ: %s\n" +
                    "Épargne prévue: %.2f DT (mois: %d/%d)",
                    planning.getNom(), planning.getEpargnePrevue(),
                    planning.getMois(), planning.getAnnee());
            Notification n = new Notification(userId, "planning_goal_missed", msg, planning.getId());
            add(n);
        }
    }

    /**
     * Crée une notification pour une facture récurrente
     */
    public void notifyRecurringBill(int userId, String nomFacture, double montant, String categorie, int joursRestants) {
        String msg = String.format("🔄 FACTURE RÉCURRENTE À VENIR: %s\n" +
                "Montant: %.2f DT | Catégorie: %s | Dans %d jours",
                nomFacture, montant, categorie, joursRestants);
        Notification n = new Notification(userId, "recurring_bill", msg, null);
        add(n);
    }

    /**
     * Crée une notification d'épargne atteinte
     */
    public void notifySavingsGoal(int userId, String categorie, double montantEpargne, double objectif) {
        String msg = String.format("💰 OBJECTIF D'ÉPARGNE ATTEINT: %s\n" +
                "Montant épargné: %.2f DT (objectif: %.2f DT)",
                categorie, montantEpargne, objectif);
        Notification n = new Notification(userId, "savings_goal", msg, null);
        add(n);
    }

    /**
     * Notification générale d'info
     */
    public void notifyInfo(int userId, String titre, String message) {
        String msg = String.format("ℹ️ %s\n%s", titre, message);
        Notification n = new Notification(userId, "info", msg, null);
        add(n);
    }

    /**
     * Notification d'alerte
     */
    public void notifyAlert(int userId, String titre, String message) {
        String msg = String.format("🚨 %s\n%s", titre, message);
        Notification n = new Notification(userId, "alert", msg, null);
        add(n);
    }
}
