package esprit.tn.souha_pi.services;

import entities.User;
import entities.Role;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private final Connection cnx = MyDataBase.getInstance().getConnection();

    /* =========================================================
                        GET USER BY ID
       ========================================================= */

    public User getById(int id) {

        String sql = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                User user = new User();

                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setTelephone(rs.getString("telephone"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));

                // IMPORTANT : role enum
                try {
                    user.setRole(Role.valueOf(rs.getString("role")));
                } catch (Exception e) {
                    user.setRole(Role.USER);
                }

                user.setIs_actif(rs.getBoolean("is_actif"));

                return user;
            }

        } catch (Exception e) {
            System.out.println("UserService getById error: " + e.getMessage());
        }

        return null;
    }

    /* =========================================================
                        GET BY EMAIL
       ========================================================= */

    public User getByEmail(String email) {

        String sql = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                User user = new User();

                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setTelephone(rs.getString("telephone"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));

                try {
                    user.setRole(Role.valueOf(rs.getString("role")));
                } catch (Exception e) {
                    user.setRole(Role.USER);
                }

                user.setIs_actif(rs.getBoolean("is_actif"));

                return user;
            }

        } catch (Exception e) {
            System.out.println("UserService getByEmail error: " + e.getMessage());
        }

        return null;
    }
    public List<User> getAll(){
        List<User> list = new ArrayList<>();

        try{
            String req = "SELECT * FROM users ORDER BY id DESC";
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);

            while(rs.next()){
                list.add(mapUser(rs));
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }
    private User mapUser(ResultSet rs) throws SQLException {

        User user = new User();

        user.setId(rs.getInt("id"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setTelephone(rs.getString("telephone"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));

        String roleStr = rs.getString("role");
        if(roleStr != null)
            user.setRole(Role.valueOf(roleStr));
        else
            user.setRole(Role.USER);

        Timestamp dc = rs.getTimestamp("date_creation");
        if(dc != null) user.setDate_creation(dc.toLocalDateTime());

        Timestamp du = rs.getTimestamp("date_update");
        if(du != null) user.setDate_update(du.toLocalDateTime());

        user.setIs_actif(rs.getBoolean("is_actif"));

        return user;
    }
}