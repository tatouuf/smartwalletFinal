package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.User;
import esprit.tn.souha_pi.utils.Mydatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    Connection cnx = Mydatabase.getInstance().getConnection();

    /* ================= GET BY ID ================= */

    public User getById(int id){

        String sql = "SELECT * FROM users WHERE id=?";

        try{
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return new User(
                        rs.getInt("id"),
                        rs.getString("fullname"),
                        rs.getString("email")
                );
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /* ================= GET ALL USERS ================= */

    public List<User> getAll(){

        List<User> list = new ArrayList<>();

        String sql = "SELECT * FROM users";

        try{
            PreparedStatement ps = cnx.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                list.add(new User(
                        rs.getInt("id"),
                        rs.getString("fullname"),
                        rs.getString("email")
                ));
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

    /* ================= ADD USER ================= */

    public void add(User u){

        String sql = "INSERT INTO users(fullname,email) VALUES(?,?)";

        try{
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1,u.getFullname());
            ps.setString(2,u.getEmail());
            ps.executeUpdate();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /* ================= UPDATE ================= */

    public void update(User u){

        String sql = "UPDATE user SET fullname=?, email=? WHERE id=?";

        try{
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1,u.getFullname());
            ps.setString(2,u.getEmail());
            ps.setInt(3,u.getId());
            ps.executeUpdate();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /* ================= DELETE ================= */

    public void delete(int id){

        String sql = "DELETE FROM user WHERE id=?";

        try{
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1,id);
            ps.executeUpdate();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /* ================= SEARCH BY EMAIL ================= */

    public User getByEmail(String email){

        String sql = "SELECT * FROM user WHERE email=?";

        try{
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1,email);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return new User(
                        rs.getInt("id"),
                        rs.getString("fullname"),
                        rs.getString("email")
                );
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
