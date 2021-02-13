package e.l2040.truecuts;

import android.net.Uri;

public class UpcomingAppointment {


    private String name;
    private String time;
    private String date;
    private Uri uri;

    public UpcomingAppointment(String name, String time, String date, Uri uri) {
        this.name = name;
        this.time = time;
        this.date = date;
        this.uri = uri;
    }


    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public Uri getUri() {
        return uri;
    }
}
