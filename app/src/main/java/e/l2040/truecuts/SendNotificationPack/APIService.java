package e.l2040.truecuts.SendNotificationPack;

import android.app.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAPlrawMk:APA91bH2XKBuH3LGPv5WBp6yuNg0F6aif-D8hEpxIK21eIT-CA9ZN9Qq3uNiLAoYTpg5Lb5sZxcT3FNsDfdULSTC0_RRvSY8ssvpKYGxTl7B-TMtNY2Jq_t4Icq5xKXDRJiJdIGzL_hb"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationSender body);
}
