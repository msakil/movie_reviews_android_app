package com.android.movieReviews;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;
import android.app.Dialog;

public class AllRatingsReviewsInfoView extends Activity {

  TextView dbInfo;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.allratingsreviewsinfoview);
    setupVars();

    String data = "";
    try{
      AllRatingsReviewsInfo info = new AllRatingsReviewsInfo(this);
      info.open();
      data = info.getData();
      info.close();
    }catch(Exception e) {
      String error = e.toString();
      Dialog d = new Dialog(this);
      TextView tv = new TextView(this);
      tv.setText(error);
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
