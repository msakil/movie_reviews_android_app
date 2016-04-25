package com.android.movieReviews;

public class Movie {

  public String movName, movDesc, movPoster, movRating;

  public Movie(){
  }

  public Movie(String mName, String mDesc, String mPoster, String mRating) {
    movName = mName;
    movDesc = mDesc;
    movPoster = mPoster;
    movRating = mRating;
  }

  public String getMovieName() {
    return movName;
  }

  public String getMovieDesc() {
    return movDesc;
  }

  public String getMoviePoster() {
    return movPoster;
  }

  public String getMovieRating() {
    return movRating;
  }
}
