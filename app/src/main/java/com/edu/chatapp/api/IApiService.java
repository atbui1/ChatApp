package com.edu.chatapp.api;

import com.edu.chatapp.model.Sender;
import com.edu.chatapp.model.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IApiService {

    String KEY_FIREBASE = "AAAAhIrUtk8:APA91bEgNsiTUUyxbY9Xf018ti4qWDeF5_L7ZtUAv2OUe4K66mlidDq0BsC109iu7J7yNF-UWe6QXzrBCBPE7wq3yiHi0VgqSKr2PDdzkJ69Isx6u6M_DvUyVnFCO7q_3I6uEGI73T_3";

    @Headers(
            {
                "Content-Type:application/json",
                "Authorization:key=AAAAhIrUtk8:APA91bEgNsiTUUyxbY9Xf018ti4qWDeF5_L7ZtUAv2OUe4K66mlidDq0BsC109iu7J7yNF-UWe6QXzrBCBPE7wq3yiHi0VgqSKr2PDdzkJ69Isx6u6M_DvUyVnFCO7q_3I6uEGI73T_3"
            }
    )
    @POST(ApiUrl.UlrDetail.SEND_NOTIFICATION)
    Call<ResponseBody> sendNotification(@Body Sender sender);

    @GET(ApiUrl.UlrDetail.URL_TBL_USERS + "/.json")
    Call<ResponseBody> getUsersFB();

}
