package com.edu.chatapp.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiBase {
    private Retrofit retrofitAdapter;

    public Retrofit getRetrofitAdapter() {
        return retrofitAdapter;
    }

    public<T> T getService(Class<T> tClass, String url) {
        if (retrofitAdapter == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(600, TimeUnit.MILLISECONDS)
                    .connectTimeout(800, TimeUnit.MILLISECONDS)
                    .build();

            retrofitAdapter = new Retrofit.Builder().baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return getRetrofitAdapter().create(tClass);
    }
}
