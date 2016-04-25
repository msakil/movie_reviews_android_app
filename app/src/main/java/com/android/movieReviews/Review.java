package com.android.movieReviews;

public class Review {

  public String name, photo, rating, review;

  public Review() {

  }

  public Review(String rName, String rPhoto, String rRating, String rReview) {
    name = rName;
    photo = rPhoto;
    rating = rRating;
    review = rReview;
  }

  public String getRname() {
    return name;
  }

  public String getRphoto() {
    return photo;
  }

  public String getRrating() {
    return rating;
  }

  public String getRreview() {
    return review;
  }

}
