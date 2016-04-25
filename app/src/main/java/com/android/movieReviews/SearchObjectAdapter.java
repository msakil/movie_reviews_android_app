package com.android.movieReviews;

import android.app.Activity;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;


public class SearchObjectAdapter extends BaseAdapter {
  private Activity activity;
  private LayoutInflater inflater;
  private List<Movie> searchList;
  private String imageUrl = "";

  public SearchObjectAdapter(Activity activity, List<Movie> searchList) {
    this.activity = activity;
    this.searchList = searchList;
  }

  @Override
  public int getCount() {
    return searchList.size();
  }

  @Override
  public Object getItem(int location) {
    return searchList.get(location);
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
      convertView = inflater.inflate(R.layout.searchrow, null);
    }

    TextView movieName = (TextView) convertView.findViewById(R.id.tvMovName);
    movieName.setText(searchList.get(position).movName);

    if(searchList.get(position).movPoster != null && searchList.get(position).movPoster != "") {
      imageUrl = searchList.get(position).movPoster;
      ImageLoader imgLoader = (ImageLoader) VolleyApplication.getInstance().getImageLoader();
      NetworkImageView nwImg = (NetworkImageView) convertView.findViewById(R.id.nwImgMoviePos);
      nwImg.setImageUrl(imageUrl, imgLoader);
    }else {
      String errorDrawable = "error";
      int resId = activity.getResources().getIdentifier(errorDrawable, "drawable", activity.getPackageName());
      imageUrl = "";
      ImageLoader imgLoader = (ImageLoader) VolleyApplication.getInstance().getImageLoader();
      NetworkImageView nwImg = (NetworkImageView) convertView.findViewById(R.id.nwImgMoviePos);
      nwImg.setDefaultImageResId(resId);
      nwImg.setImageUrl(imageUrl, imgLoader);
    }

    return convertView;
  }
}
