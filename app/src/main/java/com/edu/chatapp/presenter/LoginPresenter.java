package com.edu.chatapp.presenter;

import android.app.Application;
import android.content.Context;

import com.edu.chatapp.api.ICallBack;
import com.edu.chatapp.model.User;
import com.edu.chatapp.repository.ClientRepositoryImp;
import com.edu.chatapp.repository.IClientRepository;
import com.edu.chatapp.view.ILoginView;

public class LoginPresenter {
    private IClientRepository iClientRepository;
    private ILoginView iLoginView;
    private Context context;
    private Application application;

    public LoginPresenter(ILoginView iLoginView, Context context, Application application) {
        this.iLoginView = iLoginView;
        this.context = context;
        this.application = application;
        iClientRepository = new ClientRepositoryImp();
    }

    public void loginFB(final String username, final String password) {
        iClientRepository.loginApp(context, username, password, new ICallBack<User>() {
            @Override
            public void onSuccess(User user) {
//                loginView.loginSuccess(loginResponse);
                if (user != null) {
                    System.out.println("11 LLLLLLLLLLLLLLLLLLLLL--------------TTTTTTTTTTTTTTTTTTTTTTTTTTT");
                    System.out.println("ll user: " + user);
                    System.out.println("22 LLLLLLLLLLLLLLLLLLLLL--------------TTTTTTTTTTTTTTTTTTTTTTTTTTT");
                    iLoginView.loginViewSuccess(user);
                } else {
                    System.out.println("LLLLLLLLLLLLLLLLLLLLLLLL---------------FFFFFFFFFFFFFFFFFFFFFFFFFF");
                    iLoginView.loginViewFailed("luu thong in nguoi dung that bai");
                }
            }

            @Override
            public void onFail(String msgFail) {
                System.out.println("LLLLLLLLLLLLLLLLLLLLLLLL---------------222222222222222222222222222222222");
                iLoginView.loginViewFailed("luu thong in nguoi dung that bai 222222222222222");
            }
        });
    }
}
