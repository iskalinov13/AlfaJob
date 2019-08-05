package com.example.alfajob.Interface;

import com.example.alfajob.Notifications.MyResponse;
import com.example.alfajob.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAH8I8HAw:APA91bFxWRzC8IprmQqdo04FugVKT67VSUyT8FnEr3bnYuIYk3cIJiFdlNqTzrE7b0lf5bLyvmvq6pP4SK0V9WGhcL0nNV0i8GvkWBnqeXclczPW1s7j7CYeXNpiIRv0Pkn4riOOkbwG",
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
