package e.l2040.truecuts;

public class User {

    private String email;
    private boolean barberOrNot;
    private String username;


    public User(String email, boolean barberOrNot, String username) {
        this.email = email;
        this.barberOrNot = barberOrNot;
        this.username = username;
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isBarberOrNot() {
        return barberOrNot;
    }

    public void setBarberOrNot(boolean barberOrNot) {
        this.barberOrNot = barberOrNot;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
