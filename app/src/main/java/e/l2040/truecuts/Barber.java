package e.l2040.truecuts;

import java.util.ArrayList;

public class Barber {

    private String email;
    private boolean barberOrNot;
    private String username;
    private String zipcode;
    private String barberShopName;
    private ArrayList<String> barberShopHrs;
    private String phoneNumber;
    private String address;
    private String uid;



    public Barber(String email, boolean barberOrNot, String username, String uid) {
        this.email = email;
        this.barberOrNot = barberOrNot;
        this.username = username;
        this.uid = uid;
    }

    public Barber(){

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

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getBarberShopName() {
        return barberShopName;
    }

    public void setBarberShopName(String barberShopName) {
        this.barberShopName = barberShopName;
    }

    public ArrayList<String> getBarberShopHrs() {
        return barberShopHrs;
    }

    public void setBarberShopHrs(ArrayList<String> barberShopHrs) {
        this.barberShopHrs = barberShopHrs;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
