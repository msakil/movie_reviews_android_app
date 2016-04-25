package com.android.movieReviews;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.Toast;
import android.app.Dialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.app.ActionBar;

import java.lang.ClassNotFoundException;


public class Login extends Activity implements OnClickListener {

  EditText userId, password;
  Button login, signUp;
  UserInfo userInfo;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login);

    setupVars();

    final ActionBar actionBar = getActionBar();
    actionBar.setCustomView(R.layout.actionbar_custom);
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayShowCustomEnabled(true);
  }

  @Override
  public void onClick(View v) {
    switch(v.getId()) {
      case R.id.bLogin:
        String givenPasswd = "";
        String correctPasswd = "";
        try {
          String loginId = "";
          loginId = userId.getText().toString();
          givenPasswd = password.getText().toString();

          UserInfo details = new UserInfo(Login.this);
          details.open();
          correctPasswd  = details.getPassword(loginId);
          details.close();

          if(null != correctPasswd && givenPasswd.equals(correctPasswd)) {
            Bundle loginBundleS = new Bundle();
            loginBundleS.putString("login_email", loginId);
            Class mainActivityClass = Class.forName("com.android.movieReviews.MainActivity");
            Intent iMain = new Intent(Login.this, mainActivityClass);
            iMain.putExtras(loginBundleS);
            startActivity(iMain);
          }else {
            Toast incorrectPasswd = Toast.makeText(Login.this, "Incorrect UserId/Password. Try again", Toast.LENGTH_LONG);
            incorrectPasswd.show();
          }
        }catch(Exception e) {
          //e.printStackTrace();
          String error = e.toString();
          Dialog d = new Dialog(this);
          TextView tv = new TextView(this);
          tv.setText("There was a problem verifying the login info. Please try again.");
          d.setContentView(tv);
          d.show();
        }
        break;
      case R.id.bSignUp:
        try {
          Class signUpClass = Class.forName("com.android.movieReviews.SignUp");
          Intent openSignUp = new Intent(Login.this, signUpClass);
          startActivity(openSignUp);
        }catch(ClassNotFoundException e) {
          //e.printStackTrace();
        }
        break;
    }
  }

  public void setupVars() {
    userId = (EditText) findViewById(R.id.etUserId);
    password = (EditText) findViewById(R.id.etPassword);
    login = (Button) findViewById(R.id.bLogin);
    signUp = (Button) findViewById(R.id.bSignUp);
    login.setOnClickListener(this);
    signUp.setOnClickListener(this);
    userInfo = new UserInfo(this);
  }
}
