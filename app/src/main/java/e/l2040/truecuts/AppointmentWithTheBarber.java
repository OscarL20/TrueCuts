package e.l2040.truecuts;

public class AppointmentWithTheBarber {

    private String barberId;
    private String barberName;
    private String date;
    private String time;
    private String phone;
    private String barberShopName;
    private String address;

    public AppointmentWithTheBarber(String barberId, String barberName, String date, String time, String phone, String barberShopName, String address) {
        this.barberId = barberId;
        this.barberName = barberName;
        this.date = date;
        this.time = time;
        this.phone = phone;
        this.barberShopName = barberShopName;
        this.address = address;
    }

    public AppointmentWithTheBarber() {
    }

    public String getBarberId() {
        return barberId;
    }

    public void setBarberId(String barberId) {
        this.barberId = barberId;
    }

    public String getBarberName() {
        return barberName;
    }

    public void setBarberName(String barberName) {
        this.barberName = barberName;
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

    public String getBarberShopName() {
        return barberShopName;
    }

    public void setBarberShopName(String barberShopName) {
        this.barberShopName = barberShopName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
