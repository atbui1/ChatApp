package com.edu.chatapp.iclick;

import com.edu.chatapp.model.Post;
import com.edu.chatapp.model.User;

public interface IClickPost {
    void clickUser(Post post);
    void clickOption();
    void clickLike();
    void clickComment();
    void clickShare();

}
