package esprit.tn.souha_pi.entities;

public class User {

    private int id;
    private String fullname;
    private String email;

    public User(){}

    public User(int id,String fullname,String email){
        this.id=id;
        this.fullname=fullname;
        this.email=email;
    }

    public int getId(){return id;}
    public String getFullname(){return fullname;}
    public String getEmail(){return email;}

    public void setId(int id){this.id=id;}
    public void setFullname(String fullname){this.fullname=fullname;}
    public void setEmail(String email){this.email=email;}
}
