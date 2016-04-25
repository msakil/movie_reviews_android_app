package com.android.movieReviews;

import android.app.Activity;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.Context;
import android.content.res.Resources;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RatingBar;

import java.util.List;


public class ReviewAdapter extends BaseAdapter {
  private Activity activity;
  private LayoutInflater inflater;
  private List<Review> reviewList;

  public ReviewAdapter(Activity activity, List<Review> reviewList) {
    this.activity = activity;
    this.reviewList = reviewList;
  }

  @Override
  public int getCount() {
    return reviewList.size();
  }

  @Override
  public Object getItem(int location) {
    return reviewList.get(location);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if(inflater == null) {
      inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    if(convertView == null) {
      convertView = inflater.inflate(R.layout.movierow, null);
    }

    ImageView userPhoto = (ImageView) convertView.findViewById(R.id.bImgUserPhoto);
    TextView userName = (TextView) convertView.findViewById(R.id.tvUserName);
    RatingBar userRating = (RatingBar) convertView.findViewById(R.id.userRatingBar);
    TextView userReview = (TextView) convertView.findViewById(R.id.tvUserReview);

    userName.setText(reviewList.get(position).name);
    userRating.setRating(Float.parseFloat(reviewList.get(position).rating));
    userReview.setText(reviewList.get(position).review);

    String uPhoto = (reviewList.get(position).photo);
    Resources res = activity.getResources();
    int resId = res.getIdentifier(uPhoto, "drawable", activity.getPackageName());
    userPhoto.setImageResource(resId);

    return convertView;
  }
}
