package e.l2040.truecuts;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class AppointmentViewModel extends ViewModel {
    private String barberShopName;
    private String barberName;
    private String address;
    private String date;
    private String time;
    private String phone;
    private String uid;
    private List<Uri> listOfUris;
    private Uri barberProfileImage;
    private Uri barberShopImage;


    public AppointmentViewModel(String barberShopName, String barberName, String address, String date, String time, String phone, String uid) {
        this.barberShopName = barberShopName;
        this.barberName = barberName;
        this.address = address;
        this.date = date;
        this.time = time;
        this.phone = phone;
        this.uid = uid;
    }


    public AppointmentViewModel() {
    }


    public String getBarberShopName() {
        return barberShopName;
    }

    public void setBarberShopName(String barberShopName) {
        this.barberShopName = barberShopName;
    }

    public String getBarberName() {
        return barberName;
    }

    public void setBarberName(String barberName) {
        this.barberName = barberName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public List<Uri> getListOfUris() {
        return listOfUris;
    }

    public void setListOfUris(List<Uri> listOfUris) {
        this.listOfUris = listOfUris;
    }
}
