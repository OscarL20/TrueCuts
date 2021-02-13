package e.l2040.truecuts;

public class BarberShop {

    private String barberShopName;
    private String address;

    public BarberShop(String barberShopName, String address) {
        this.barberShopName = barberShopName;
        this.address = address;
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
