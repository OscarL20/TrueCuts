package e.l2040.truecuts;

import java.util.ArrayList;

class CustomerAppointment {

    private String uid;
    private String name;
    private String date;
    private ArrayList<String> time;

    public CustomerAppointment(String uid, String name, String date, ArrayList<String> time) {
        this.uid = uid;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public CustomerAppointment() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<String> getTime() {
        return time;
    }

    public void setTime(ArrayList<String> time) {
        this.time = time;
    }
}
