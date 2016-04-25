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

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class MovieAdapter extends BaseAdapter {
  private Activity activity;
  private LayoutInflater inflater;
  private List<Movie> movieList;
  private String imageUrl = "";

  public MovieAdapter(Activity activity, List<Movie> movieList) {
    this.activity = activity;
    this.movieList = movieList;
  }

  @Override
  public int getCount() {
    return movieList.size();
  }

  @Override
  public Object getItem(int location) {
    return movieList.get(location);
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
      convertView = inflater.inflate(R.layout.listrow, null);
    }

    TextView name = (TextView) convertView.findViewById(R.id.tvMovieName);
    RatingBar rating = (RatingBar) convertView.findViewById(R.id.ratingBar);
    ImageView rateIt = (ImageView) convertView.findViewById(R.id.bImgRateIt);
    TextView desc = (TextView) convertView.findViewById(R.id.tvMovieDesc);

    name.setText(movieList.get(position).movName);
    desc.setText(movieList.get(position).movDesc);
    rating.setRating(Float.parseFloat(movieList.get(position).movRating));

    if(movieList.get(position).movPoster != null) {
      imageUrl = movieList.get(position).movPoster;
      ImageLoader mImageLoader = VolleyApplication.getInstance().getImageLoader();
      NetworkImageView mNetworkImageView = (NetworkImageView) convertView.findViewById(R.id.nwImgMoviePoster);
      mNetworkImageView.setImageUrl(imageUrl, mImageLoader);
    }

    return convertView;
  }
}
