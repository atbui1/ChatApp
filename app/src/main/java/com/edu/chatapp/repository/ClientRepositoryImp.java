package com.edu.chatapp.repository;

import android.content.Context;

import com.edu.chatapp.api.ApiClient;
import com.edu.chatapp.api.ICallBack;
import com.edu.chatapp.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientRepositoryImp implements IClientRepository{
    @Override
    public void loginApp(Context context, String username, String password, ICallBack<User> iCallBack) {
        ApiClient apiClient = new ApiClient();
        JSONObject jsonObject = new JSONObject();
        List<User> userList = new ArrayList<>();
        try {
            jsonObject.put("email", username);
            jsonObject.put("password", password);
        } catch (Exception ex) {
            System.out.println("ERR Login");
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Call<ResponseBody> serviceCall = apiClient.iClientService().loginFB();
        System.out.println("bodyJson: " + body);
        System.out.println("serviceCall: " + serviceCall);
        serviceCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200 && response.body() != null) {
                        System.out.println("47 --------------- iml");
                        try {
                            System.out.println("49 --------------- iml");
                            String result = response.body().string();
                            System.out.println("49 --------------- iml result: " + result);
                            JSONArray jsonArray111 = new JSONArray(result.toString());
                            System.out.println("49 --------------- iml json jsonArray111: " + jsonArray111);
                            JSONObject jsonObject = new JSONObject(result);//.getJSONObject("data");
                            System.out.println("49 --------------- iml json object: " + jsonObject.toString());

//                            User userResponse = new Gson().fromJson(jsonObject.toString(), User.class);
//                            System.out.println("49 --------------- iml userResponse: " + userResponse);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString(""));
                            System.out.println("49 --------------- iml jsonArray: " + jsonArray);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                User user = new Gson().fromJson(jsonArray.getJSONObject(i).toString(), User.class);
                                userList.add(user);
                                System.out.println("het all list user: " + userList.size());
                            }
                            if (userList.size() > 0) {
                                for (User u:userList) {
                                    if (u.getUsername().equals(username.toString().trim()) && u.getPassword().equals(password.toString().trim())) {
                                        System.out.println("user login: " + u);
                                        System.out.println("llllllllllllllllllllll success");
                                        System.out.println("54 --------------- iml");
                                        iCallBack.onSuccess(u);
                                    }
                                }
                            } else {
                                System.out.println("57 --------------- iml");
                                iCallBack.onFail("failed 111111111");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        System.out.println("64 --------------- iml");
                    } else {
                        System.out.println("66 --------------- iml");
                        iCallBack.onFail("");
                    }
                } catch (Exception ex) {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
