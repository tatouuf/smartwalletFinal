package services;

import entities.Reclamation;
import entities.ReclamationStatuts;
import entities.Role;
import entities.User;
import utils.MyDataBase;
import utils.NotificationHelper;
import utils.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReclamation {

    private Connection cnx;
    private ServiceUser userService;
    private AIService aiService;

    public ServiceReclamation() {
        cnx = MyDataBase.getInstance().getConnection();
        userService = new ServiceUser();
        aiService = AIService.getInstance();
    }

    // ================= SEND RECLAMATION (WITH AI) =================
    public void sendReclamation(int userId, String message) throws SQLException {
        String category = aiService.categorizeReclamation(message);
        AIService.SentimentResult sentiment = aiService.analyzeSentiment(message);

        // CORRIGÉ: 'dateEnvoi' au lieu de 'date_envoi'
        String sql = "INSERT INTO reclamation(userId, message, statut, dateEnvoi, category, sentiment, is_urgent) " +
                "VALUES (?, ?, ?, NOW(), ?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, message);
            ps.setString(3, ReclamationStatuts.PENDING.name());
            ps.setString(4, category);
            ps.setString(5, sentiment.getSentiment());
            ps.setBoolean(6, sentiment.isUrgent());
            ps.executeUpdate();

            List<User> admins = userService.recuperer().stream()
                    .filter(u -> "ADMIN".equals(u.getRole()))
                    .toList();

            User sender = userService.recupererParId(userId);
            String notifMessage = (sentiment.isUrgent() ? "🚨 URGENT: " : "") +
                    "New " + category.replace("_", " ").toLowerCase() +
                    " from " + sender.getPrenom() + " " + sender.getNom();

            for (User admin : admins) {
                NotificationHelper.createAndShowNotification(
                        admin.getId(),
                        NotificationHelper.NEW_RECLAMATION,
                        notifMessage,
                        null
                );
            }
        }
    }

    // ================= GET AI REPLY SUGGESTION =================
    public String getAIReplySuggestion(int reclamationId) throws SQLException {
        Reclamation r = getReclamationById(reclamationId);
        if (r == null) return null;

        String category = r.getCategory() != null ? r.getCategory() : "GENERAL";
        return aiService.generateReplySuggestion(category, r.getMessage());
    }

    // ================= REPLY TO RECLAMATION =================
    public void replyReclamation(int reclamationId, int adminId, String reponse) throws SQLException {
        // CORRIGÉ: 'adminId' au lieu de 'admin_id'
        String sql = "UPDATE reclamation SET reponse = ?, adminId = ?, statut = ?, dateReponse = NOW() WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, reponse);
            ps.setInt(2, adminId);
            ps.setString(3, ReclamationStatuts.IN_PROGRESS.name());
            ps.setInt(4, reclamationId);
            ps.executeUpdate();

            Reclamation reclamation = getReclamationById(reclamationId);
            if (reclamation != null) {
                NotificationHelper.createAndShowNotification(
                        reclamation.getUser_id(),
                        NotificationHelper.RECLAMATION_REPLY,
                        "Admin replied to your reclamation!",
                        reclamationId
                );
            }
        }
    }

    // ================= RESOLVE RECLAMATION =================
    public void resolveReclamation(int reclamationId) throws SQLException {
        String sql = "UPDATE reclamation SET statut = ? WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, ReclamationStatuts.RESOLVED.name());
            ps.setInt(2, reclamationId);
            ps.executeUpdate();
        }
    }

    // ================= DELETE RECLAMATION =================
    public void deleteReclamation(int reclamationId) throws SQLException {
        String sql = "DELETE FROM reclamation WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, reclamationId);
            ps.executeUpdate();
        }
    }

    // ================= GET ALL RECLAMATIONS (ADMIN) =================
    public List<Reclamation> getAllReclamations() throws SQLException {
        List<Reclamation> list = new ArrayList<>();
        // CORRIGÉ: 'dateEnvoi' au lieu de 'date_envoi'
        String sql = "SELECT * FROM reclamation ORDER BY is_urgent DESC, dateEnvoi DESC";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapReclamation(rs));
            }
        }
        return list;
    }

    // ================= GET RECLAMATIONS BY USER =================
    public List<Reclamation> getReclamationsByUser(int userId) throws SQLException {
        List<Reclamation> list = new ArrayList<>();
        // CORRIGÉ: 'userId' et 'dateEnvoi'
        String sql = "SELECT * FROM reclamation WHERE userId = ? ORDER BY dateEnvoi DESC";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapReclamation(rs));
            }
        }
        return list;
    }

    // ================= GET RECLAMATION BY ID =================
    public Reclamation getReclamationById(int id) throws SQLException {
        String sql = "SELECT * FROM reclamation WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapReclamation(rs);
            }
        }
        return null;
    }

    // ================= MAPPER RESULTSET TO RECLAMATION =================
    private Reclamation mapReclamation(ResultSet rs) throws SQLException {
        Reclamation r = new Reclamation();
        r.setId(rs.getInt("id"));
        r.setUser_id(rs.getInt("userId"));  // CORRIGÉ: 'userId' au lieu de 'user_id'
        r.setMessage(rs.getString("message"));
        r.setStatut(ReclamationStatuts.valueOf(rs.getString("statut")));
        r.setReponse(rs.getString("reponse"));

        // AI fields
        r.setCategory(rs.getString("category"));
        r.setSentiment(rs.getString("sentiment"));
        r.setUrgent(rs.getBoolean("is_urgent"));

        int adminId = rs.getInt("adminId");  // CORRIGÉ: 'adminId' au lieu de 'admin_id'
        if (!rs.wasNull()) {
            r.setAdmin_id(adminId);
        }

        // CORRIGÉ: 'dateEnvoi' au lieu de 'date_envoi'
        r.setDate_envoi(rs.getTimestamp("dateEnvoi").toLocalDateTime());

        Timestamp dateReponse = rs.getTimestamp("dateReponse");  // CORRIGÉ: 'dateReponse'
        if (dateReponse != null) {
            r.setDate_reponse(dateReponse.toLocalDateTime());
        }

        return r;
    }
}