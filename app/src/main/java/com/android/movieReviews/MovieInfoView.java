package com.android.movieReviews;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;
import android.app.Dialog;

public class MovieInfoView extends Activity {

  TextView dbInfo;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.movieinfoview);
    setupVars();
    String data = "";
    try{
      MovieInfo movInfo = new MovieInfo(this);
      movInfo.open();
      data = movInfo.getData();
      movInfo.close();
    }catch(Exception e) {
      String error = e.toString();
      Dialog d = new Dialog(this);
      TextView tv = new TextView(this);
      //tv.setText(error);
      tv.setText("Unable to save info. Please try again.");
      d.setContentView(tv);
      d.show();
    }finally {
      dbInfo.setText(data);
    }
  }

  public void setupVars() {
    dbInfo = (TextView) findViewById(R.id.tvDbInfo);
  }
}