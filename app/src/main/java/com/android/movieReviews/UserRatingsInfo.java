package com.android.movieReviews;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.SQLException;
import android.content.ContentValues;

public class UserRatingsInfo {

  public static final String KEY_ROWID = "_id";
  public static final String KEY_EMAIL = "user_email";
  public static final String KEY_MOVIE = "movie_name";
  public static final String KEY_RATING = "movie_ratings";
  public static final String KEY_REVIEW = "movie_review";

  private static final String DATABASE_NAME = "UserRatingsInfoTable";
  private static final String DATABASE_TABLE = "userRatingsInfoTable";
  private static final int DATABASE_VERSION = 1;

  private DbHelper myHelper;
  private Context myContext;
  private SQLiteDatabase myDatabase;

  private static class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                  KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                  KEY_EMAIL + " TEXT NOT NULL, " +
                  KEY_MOVIE + " TEXT NOT NULL, " +
                  KEY_RATING + " TEXT NOT NULL, " +
                  KEY_REVIEW + " TEXT NULL);"
      );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
      onCreate(db);
    }
  }

  public UserRatingsInfo(Context c) {
    myContext = c;
  }

  public UserRatingsInfo open() throws SQLException {
    myHelper = new DbHelper(myContext);
    myDatabase = myHelper.getWritableDatabase();
    return this;
  }

  public void close() {
    myHelper.close();
  }

  public long createEntry(String userEmail, String movieName, String rating, String review) {
    ContentValues cv = new ContentValues();
    cv.put(KEY_EMAIL, userEmail);
    cv.put(KEY_MOVIE, movieName);
    cv.put(KEY_RATING, rating);
    if(null != review) {
      cv.put(KEY_REVIEW, review);
    }
    return myDatabase.insert(DATABASE_TABLE, null, cv);
  }
}
