package com.edu.chatapp.view;

import com.edu.chatapp.model.User;

public interface ILoginView {
    void loginViewSuccess(User user);
    void loginViewFailed(String msg);
}
