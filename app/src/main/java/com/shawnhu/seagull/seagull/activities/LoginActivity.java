package com.shawnhu.seagull.seagull.activities;

import android.content.Intent;
import android.net.Uri;

import com.shawnhu.seagull.activities.AbstractLoginActivity;
import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;

/**
 * Created by shawn on 14-8-14.
 */
public class LoginActivity extends AbstractLoginActivity {
    @Override
    protected int isEmailValid(String email) {
        return super.isEmailValid(email);
    }
    @Override
    protected int isPasswordValid(String password) {
        return super.isPasswordValid(password);
    }
    protected int tryLoginUser(String acc, String pwd) {

        return SUCCESS_CODE;
    }
    protected int trySignUpUser(String acc, String pwd) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(SeagullTwitterConstants.TWITTER_SIGNUP_URL));
        startActivity(intent);
        return SUCCESS_CODE;
    }
}
