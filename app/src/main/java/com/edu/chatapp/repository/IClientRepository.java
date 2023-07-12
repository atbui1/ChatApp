package com.edu.chatapp.repository;

import android.content.Context;

import com.edu.chatapp.api.ICallBack;
import com.edu.chatapp.model.User;

public interface IClientRepository {
    void loginApp(Context context, String username, String password, ICallBack<User> iCallBack);

}
