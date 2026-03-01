package services;

import entities.PasswordResetToken;
import entities.User;
import utils.MyDataBase;
import utils.PasswordUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServicePasswordReset {

    private static final Logger logger = Logger.getLogger(ServicePasswordReset.class.getName());
    private Connection cnx;
    private ServiceUser userService;

    public ServicePasswordReset() {
        cnx = MyDataBase.getInstance().getConnection();
        userService = new ServiceUser();
    }

    // Generate and send reset token
    public boolean sendPasswordResetEmail(String email) throws SQLException {
        // Check if user exists
        if (!userService.isEmailTaken(email)) {
            return false; // User not found
        }

        User user = userService.recupererParEmail(email);
        if (user == null) {
            return false;
        }

        // Generate unique token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1); // Token valid for 1 hour

        // Save token to database
        String sql = "INSERT INTO password_reset_tokens(user_id, token, expiry_date) VALUES (?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, user.getId());
            ps.setString(2, token);
            ps.setTimestamp(3, Timestamp.valueOf(expiryDate));
            ps.executeUpdate();
        }

        // Send email
        boolean emailSent = EmailService.sendPasswordResetEmail(email, token);

        if (emailSent) {
            logger.info("Password reset email sent to: " + email);
        } else {
            logger.warning("Failed to send email to: " + email);
        }

        return emailSent;
    }

    // Validate token
    public PasswordResetToken validateToken(String token) throws SQLException {
        String sql = "SELECT * FROM password_reset_tokens WHERE token = ? AND used = FALSE";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                PasswordResetToken resetToken = new PasswordResetToken();
                resetToken.setId(rs.getInt("id"));
                resetToken.setUserId(rs.getInt("user_id"));
                resetToken.setToken(rs.getString("token"));
                resetToken.setExpiryDate(rs.getTimestamp("expiry_date").toLocalDateTime());
                resetToken.setUsed(rs.getBoolean("used"));

                if (resetToken.isExpired()) {
                    return null; // Token expired
                }

                return resetToken;
            }
        }

        return null; // Token not found or already used
    }

    // Reset password using token
    public boolean resetPasswordWithToken(String token, String newPassword) throws SQLException {
        PasswordResetToken resetToken = validateToken(token);

        if (resetToken == null) {
            return false; // Invalid or expired token
        }

        // Update user password
        User user = userService.recupererParId(resetToken.getUserId());
        if (user == null) {
            return false;
        }

        String hashedPassword = PasswordUtils.hashPassword(newPassword);
        String sql = "UPDATE users SET password = ?, date_update = NOW() WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setInt(2, user.getId());
            ps.executeUpdate();
        }

        // Mark token as used
        String updateTokenSql = "UPDATE password_reset_tokens SET used = TRUE WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(updateTokenSql)) {
            ps.setInt(1, resetToken.getId());
            ps.executeUpdate();
        }

        logger.info("Password reset successfully for user ID: " + user.getId());
        return true;
    }

    // Clean up expired tokens (call this periodically)
    public void cleanupExpiredTokens() throws SQLException {
        String sql = "DELETE FROM password_reset_tokens WHERE expiry_date < NOW() OR used = TRUE";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            int deleted = ps.executeUpdate();
            logger.info("Cleaned up " + deleted + " expired/used tokens");
        }
    }
}