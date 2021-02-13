package e.l2040.truecuts;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyViewModel extends ViewModel {

    private String barberShop;
    private String barber;
    private String address;
    private ArrayList<String> hours;
    private String phone;
    private String uid;
    private List<Uri> listOfUris;
    private Uri barberProfileImage;
    private Uri barberShopImage;

    public List<Uri> getListOfUris() {
        return listOfUris;
    }

    public void setListOfUris(List<Uri> listOfUris) {
        this.listOfUris = listOfUris;
    }

    public Uri getBarberProfileImage() {
        return barberProfileImage;
    }

    public void setBarberProfileImage(Uri barberProfileImage) {
        this.barberProfileImage = barberProfileImage;
    }

    public Uri getBarberShopImage() {
        return barberShopImage;
    }

    public void setBarberShopImage(Uri barberShopImage) {
        this.barberShopImage = barberShopImage;
    }

    public String getBarberShop() {
        return barberShop;
    }

    public void setBarberShop(String barberShop) {
        this.barberShop = barberShop;
    }

    public String getBarber() {
        return barber;
    }

    public void setBarber(String barber) {
        this.barber = barber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<String> getHours() {
        return hours;
    }

    public void setHours(ArrayList<String> hours) {
        this.hours = hours;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}
