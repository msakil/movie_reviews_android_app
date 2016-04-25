package com.android.movieReviews;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Button;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.WindowManager.LayoutParams;
import android.content.Intent;
import android.widget.Toast;
import android.widget.TextView;
import android.app.Dialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.app.ActionBar;

import java.lang.ClassNotFoundException;
import java.lang.Exception;

public class SignUp extends Activity implements OnClickListener {

  EditText fName, lName, email, passwd, rePasswd, userPhoto;
  Button submit;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //requestWindowFeature(Window.FEATURE_NO_TITLE);
    //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.signup);
    setUpVars();

    final ActionBar actionBar = getActionBar();
    actionBar.setCustomView(R.layout.actionbar_custom);
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayShowCustomEnabled(true);
  }

  public void onClick(View v) {
    switch(v.getId()) {
      case R.id.bSubmit:
        boolean didItWork = true;
        boolean toasted = false;
        try {
          String firstName = fName.getText().toString();
          String lastName = lName.getText().toString();
          String emailId = email.getText().toString();
          String password = passwd.getText().toString();
          String rePassword = rePasswd.getText().toString();
          String userPic = userPhoto.getText().toString();

          if(password.equals(rePassword) && !firstName.equals("") && !lastName.equals("") && !emailId.equals("")
              && !password.equals("")) {
            UserInfo newUser = new UserInfo(SignUp.this);
            newUser.open();
            newUser.createEntry(firstName, lastName, emailId, password, userPic);
            newUser.close();
          }else {
            if(!password.equals(rePassword)) {
              Toast t = Toast.makeText(SignUp.this, "The passwords entered do not match. Please try again", Toast.LENGTH_LONG);
              t.show();
              toasted = true;
            }else {
              Toast t = Toast.makeText(SignUp.this, "Except 'User Photo' all fields are mandatory, please fill them and try again",
                                        Toast.LENGTH_LONG);
              t.show();
              toasted = true;
            }
          }
        }catch(Exception e) {
          didItWork = false;
          String error = e.toString();
          Dialog d = new Dialog(this);
          TextView tv = new TextView(this);
          //tv.setText(error);
          tv.setText("Unable to save info. Please try again.");
          d.setContentView(tv);
          d.show();
        }finally {
          if(didItWork && !toasted) {
            Dialog d = new Dialog(this);
            d.setTitle("User info saved successfully!");
            TextView tv = new TextView(this);
            tv.setText("Success!");
            d.setContentView(tv);
            d.show();
          }
        }
        break;
    }
  }

  public void setUpVars() {
    fName = (EditText) findViewById(R.id.etFname);
    lName = (EditText) findViewById(R.id.etLname);
    email = (EditText) findViewById(R.id.etEmailId);
    passwd = (EditText) findViewById(R.id.etPassword);
    rePasswd = (EditText) findViewById(R.id.etRePassword);
    submit = (Button) findViewById(R.id.bSubmit);
    submit.setOnClickListener(this);
    userPhoto = (EditText) findViewById(R.id.etUserPhoto);
  }
}
