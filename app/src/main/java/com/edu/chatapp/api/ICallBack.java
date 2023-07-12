package com.edu.chatapp.api;

public interface ICallBack<T> {
    void onSuccess(T t);
    void onFail(String msgFail);
}
