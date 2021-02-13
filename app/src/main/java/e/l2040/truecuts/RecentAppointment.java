package e.l2040.truecuts;

import android.net.Uri;

public class RecentAppointment {


    private String barberShop;
    private String barber;
    private String address;
    private Uri uri;

    public RecentAppointment(String barberShop, String barber, String address, Uri uri) {
        this.barberShop = barberShop;
        this.barber = barber;
        this.address = address;
        this.uri = uri;
    }


    public String getBarberShop() {
        return barberShop;
    }

    public String getBarber() {
        return barber;
    }

    public String getAddress() {
        return address;
    }

    public Uri getUri() {
        return uri;
    }
}
