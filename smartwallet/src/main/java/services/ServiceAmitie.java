package services;

import entities.Amitie;
import entities.StatutAmitie;
import entities.User;
import utils.MyDataBase;
import utils.NotificationHelper;
import utils.Session;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceAmitie {

    private Connection cnx;
    private ServiceUser userService;

    public ServiceAmitie() {
        cnx = MyDataBase.getInstance().getConnection();
        userService = new ServiceUser();
    }

    // ================= ADD FRIEND (SEND REQUEST) =================
    public void addFriend(int friendId) throws SQLException {
        int currentUserId = Session.getCurrentUser().getId();

        if (currentUserId == friendId) {
            throw new SQLException("You cannot send a friend request to yourself.");
        }

        if (requestExists(currentUserId, friendId)) {
            throw new SQLException("A request already exists between these users.");
        }

        String sql = "INSERT INTO amitie(user_id, friend_id, statut, created_at) VALUES (?, ?, ?, NOW())";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setInt(2, friendId);
            ps.setString(3, StatutAmitie.PENDING.name());
            ps.executeUpdate();

            // 🔔 CREATE NOTIFICATION FOR RECEIVER
            User sender = Session.getCurrentUser();
            String message = sender.getPrenom() + " " + sender.getNom() + " sent you a friend request.";
            NotificationHelper.createAndShowNotification(
                    friendId,
                    NotificationHelper.FRIEND_REQUEST,
                    message,
                    null
            );
        }
    }

    // ================= ACCEPT FRIEND =================
    public void acceptFriend(int requesterId) throws SQLException {
        int currentUserId = Session.getCurrentUser().getId();
        String sql = "UPDATE amitie SET statut = ? WHERE user_id = ? AND friend_id = ? AND statut = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, StatutAmitie.ACCEPTED.name());
            ps.setInt(2, requesterId);
            ps.setInt(3, currentUserId);
            ps.setString(4, StatutAmitie.PENDING.name());
            ps.executeUpdate();

            // 🔔 CREATE NOTIFICATION FOR REQUESTER
            User accepter = Session.getCurrentUser();
            String message = accepter.getPrenom() + " " + accepter.getNom() + " accepted your friend request!";
            NotificationHelper.createAndShowNotification(
                    requesterId,
                    NotificationHelper.FRIEND_ACCEPTED,
                    message,
                    null
            );
        }
    }

    // ================= DELETE FRIEND (REFUSE REQUEST) =================
    public void deleteFriend(int requesterId) throws SQLException {
        int currentUserId = Session.getCurrentUser().getId();
        String sql = "DELETE FROM amitie WHERE user_id = ? AND friend_id = ? AND statut = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, requesterId);
            ps.setInt(2, currentUserId);
            ps.setString(3, StatutAmitie.PENDING.name());
            ps.executeUpdate();
        }
    }

    // ================= REMOVE FRIEND =================
    public void removeFriend(int friendId) throws SQLException {
        int currentUserId = Session.getCurrentUser().getId();
        String sql = "DELETE FROM amitie WHERE " +
                "((user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)) AND statut = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setInt(2, friendId);
            ps.setInt(3, friendId);
            ps.setInt(4, currentUserId);
            ps.setString(5, StatutAmitie.ACCEPTED.name());
            ps.executeUpdate();
        }
    }

    // ================= BLOCK FRIEND =================
    public void blockFriend(int friendId) throws SQLException {
        int currentUserId = Session.getCurrentUser().getId();

        removeFriend(friendId);

        String sql = "INSERT INTO amitie(user_id, friend_id, statut, created_at) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setInt(2, friendId);
            ps.setString(3, StatutAmitie.BLOCKED.name());
            ps.executeUpdate();
        }
    }

    // ================= UNBLOCK USER =================
    public void unblockUser(int userId) throws SQLException {
        int currentUserId = Session.getCurrentUser().getId();
        String sql = "DELETE FROM amitie WHERE user_id = ? AND friend_id = ? AND statut = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setInt(2, userId);
            ps.setString(3, StatutAmitie.BLOCKED.name());
            ps.executeUpdate();
        }
    }

    // ================= CANCEL SENT REQUEST =================
    public void cancelSentRequest(int friendId) throws SQLException {
        int currentUserId = Session.getCurrentUser().getId();
        String sql = "DELETE FROM amitie WHERE user_id = ? AND friend_id = ? AND statut = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setInt(2, friendId);
            ps.setString(3, StatutAmitie.PENDING.name());
            ps.executeUpdate();
        }
    }

    // ================= GET PENDING REQUESTS (INCOMING) =================
    public List<Amitie> getPendingRequests() throws SQLException {
        List<Amitie> list = new ArrayList<>();
        int currentUserId = Session.getCurrentUser().getId();
        String sql = "SELECT * FROM amitie WHERE friend_id = ? AND statut = ? ORDER BY created_at DESC";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setString(2, StatutAmitie.PENDING.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapAmitie(rs));
            }
        }
        return list;
    }

    // ================= GET ACCEPTED FRIENDS =================
    public List<Amitie> getAcceptedFriends() throws SQLException {
        List<Amitie> list = new ArrayList<>();
        int currentUserId = Session.getCurrentUser().getId();
        String sql = "SELECT * FROM amitie WHERE (user_id = ? OR friend_id = ?) AND statut = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setInt(2, currentUserId);
            ps.setString(3, StatutAmitie.ACCEPTED.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapAmitie(rs));
            }
        }
        return list;
    }

    // ================= GET BLOCKED USERS =================
    public List<Amitie> getBlockedUsers() throws SQLException {
        List<Amitie> list = new ArrayList<>();
        int currentUserId = Session.getCurrentUser().getId();
        String sql = "SELECT * FROM amitie WHERE user_id = ? AND statut = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setString(2, StatutAmitie.BLOCKED.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapAmitie(rs));
            }
        }
        return list;
    }

    // ================= GET SENT REQUESTS =================
    public List<Amitie> getSentRequests() throws SQLException {
        List<Amitie> list = new ArrayList<>();
        int currentUserId = Session.getCurrentUser().getId();
        String sql = "SELECT * FROM amitie WHERE user_id = ? AND statut = ? ORDER BY created_at DESC";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setString(2, StatutAmitie.PENDING.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapAmitie(rs));
            }
        }
        return list;
    }

    // ================= SEARCH USERS =================
    public List<User> searchUsers(String keyword) throws SQLException {
        List<User> list = new ArrayList<>();
        int currentUserId = Session.getCurrentUser().getId();
        String sql = "SELECT * FROM users WHERE (nom LIKE ? OR prenom LIKE ?) AND id != ? AND is_actif = 1";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setInt(3, currentUserId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(userService.recupererParId(rs.getInt("id")));
            }
        }
        return list;
    }

    // ================= CHECK IF REQUEST EXISTS =================
    private boolean requestExists(int userId1, int userId2) throws SQLException {
        String sql = "SELECT id FROM amitie WHERE " +
                "(user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId1);
            ps.setInt(2, userId2);
            ps.setInt(3, userId2);
            ps.setInt(4, userId1);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    // ================= MAPPER =================
    private Amitie mapAmitie(ResultSet rs) throws SQLException {
        Amitie a = new Amitie();
        a.setId(rs.getInt("id"));
        a.setUser_id(rs.getInt("user_id"));
        a.setFriend_id(rs.getInt("friend_id"));
        a.setStatut(StatutAmitie.valueOf(rs.getString("statut")));
        a.setDateCreation(rs.getTimestamp("created_at").toLocalDateTime());
        return a;
    }
}