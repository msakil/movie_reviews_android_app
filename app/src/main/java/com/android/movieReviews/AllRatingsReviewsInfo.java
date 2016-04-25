package com.android.movieReviews;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.SQLException;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;


public class AllRatingsReviewsInfo {

  public static final String KEY_ROWID = "_id";
  public static final String KEY_UEMAIL = "user_email";
  public static final String KEY_MNAME = "movie_name";
  public static final String KEY_URATING = "user_rating";
  public static final String KEY_UREVIEW = "user_review";

  private static final String DATABASE_NAME = "AllRatingsReviewsInfo";
  private static final String DATABASE_TABLE = "allRatingsReviewsInfoDb";
  private static final int DATABASE_VERSION = 1;

  private DbHelper myHelper;
  private final Context myContext;
  private SQLiteDatabase myDatabase;

  private static class DbHelper extends SQLiteOpenHelper {

    private final Context mContext;

    public DbHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
      mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                  KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                  KEY_UEMAIL + " TEXT NOT NULL, " +
                  KEY_MNAME + " TEXT NOT NULL, " +
                  KEY_URATING + " TEXT, " +
                  KEY_UREVIEW + " TEXT);"
      );
      try {
        InputStream is = mContext.getResources().getAssets().open("ratings_reviews_info.sql");
        String[] statements = parseSqlFile(is);
        for(String statement : statements) {
          db.execSQL(statement);
        }
      }catch(Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
      onCreate(db);
    }
  }

  public AllRatingsReviewsInfo(Context c) {
    myContext = c;
  }

  public AllRatingsReviewsInfo open() throws SQLException {
    myHelper = new DbHelper(myContext);
    myDatabase = myHelper.getWritableDatabase();
    return this;
  }

  public void close() {
    myHelper.close();
  }

  public long createEntry(String uEmail, String mName, String uRating, String uReview) {
    String[] columns = new String[]{KEY_ROWID, KEY_UEMAIL, KEY_MNAME, KEY_URATING, KEY_UREVIEW};
    Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
    boolean userPriorEntry = false;

    for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
      if(c.getString(1).equals(uEmail) && c.getString(2).equals(mName)) {
        userPriorEntry = true;
      }
    }

    ContentValues cv = new ContentValues();
    if(userPriorEntry) {
      if(null == uRating) {
        cv.put(KEY_UREVIEW, uReview);
      }else if(null == uReview) {
        cv.put(KEY_URATING, uRating);
      }else if(null != uRating && null != uReview) {
        cv.put(KEY_URATING, uRating);
        cv.put(KEY_UREVIEW, uReview);
      }
      return myDatabase.update(DATABASE_TABLE, cv, KEY_UEMAIL + " = " + "'" + uEmail + "'", null);
    }else {
      cv.put(KEY_UEMAIL, uEmail);
      cv.put(KEY_MNAME, mName);
      cv.put(KEY_URATING, uRating);
      cv.put(KEY_UREVIEW, uReview);
      return myDatabase.insert(DATABASE_TABLE, null, cv);
    }
  }

  public String getData() {
    String[] columns = new String[]{KEY_ROWID, KEY_UEMAIL, KEY_MNAME, KEY_URATING, KEY_UREVIEW};
    Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
    String result = "";

    int iRow = c.getColumnIndex(KEY_ROWID);
    int iUemail = c.getColumnIndex(KEY_UEMAIL);
    int iMname = c.getColumnIndex(KEY_MNAME);
    int iUrating = c.getColumnIndex(KEY_URATING);
    int iUReview = c.getColumnIndex(KEY_UREVIEW);

    for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
      result = result + c.getString(iRow) + "  " +
               c.getString(iUemail) + "  " +
               c.getString(iMname) + "  " +
               c.getString(iUrating) + "  " +
               c.getString(iUReview) + "\n";
    }
    return result;
  }

  public String getRating(String emailId, String movName) {
    String[] columns = new String[]{KEY_ROWID, KEY_UEMAIL, KEY_MNAME, KEY_URATING, KEY_UREVIEW};
    Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
    String result = "";
    String userEmail = "";
    String movieName = "";
    boolean isMatch = false;

    for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
      userEmail = c.getString(1);
      movieName = c.getString(2);
      if(userEmail.equals(emailId) && movieName.equals(movName)) {
        result = c.getString(3);
        isMatch = true;
      }
    }

    if(isMatch) {
      return result;
    }else {
      return null;
    }
  }

  public String getRating(int pos) {
    String[] columns = new String[]{KEY_ROWID, KEY_UEMAIL, KEY_MNAME, KEY_URATING, KEY_UREVIEW};
    Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
    String result = "";

    c.moveToPosition(pos);
    result = c.getString(3);

    return result;
  }

  public String getReview(String emailId, String movName) {
    String[] columns = new String[]{KEY_ROWID, KEY_UEMAIL, KEY_MNAME, KEY_URATING, KEY_UREVIEW};
    Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
    String result = "";
    String userEmail = "";
    String movieName = "";
    boolean isMatch = false;

    for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
      userEmail = c.getString(1);
      movieName = c.getString(2);
      if(userEmail.equals(emailId) && movieName.equals(movName)) {
        result = c.getString(4);
        isMatch = true;
      }
    }

    if(isMatch) {
      return result;
    }else {
      return null;
    }
  }

  public String getReview(int pos) {
    String[] columns = new String[]{KEY_ROWID, KEY_UEMAIL, KEY_MNAME, KEY_URATING, KEY_UREVIEW};
    Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
    String result = "";

    c.moveToPosition(pos);
    result = c.getString(4);

    return result;
  }

  public long getRowCount() {
    return DatabaseUtils.queryNumEntries(myDatabase, DATABASE_TABLE);
  }

  public String getEmail(int pos, String mName) {
    String[] columns = new String[]{KEY_ROWID, KEY_UEMAIL, KEY_MNAME, KEY_URATING, KEY_UREVIEW};
    Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
    String movieName = "";
    String result = "";

    c.moveToPosition(pos);
    movieName = c.getString(2);
    if(movieName.equals(mName)) {
      result = c.getString(1);
    }

    return result;
  }

  public String getEmail(int pos) {
    String[] columns = new String[]{KEY_ROWID, KEY_UEMAIL, KEY_MNAME, KEY_URATING, KEY_UREVIEW};
    Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
    String result = "";

    c.moveToPosition(pos);
    result = c.getString(1);

    return result;
  }

  /**
  * I got this method from SO, a couple really good java guys had put it up.
  * 'parseSqlFile' here is an overloaded metod based on type of paramenter passed.
  * @param sqlFile
  *            - InputStream for the file that contains sql statements.
  *
  * @return String array containing the sql statements.
  */
  public static String[] parseSqlFile(InputStream sqlFile) throws IOException {
    return parseSqlFile(new BufferedReader(new InputStreamReader(sqlFile)));
  }

  /**
  * I got this method from SO, a couple really good java guys had put it up.
  * 'parseSqlFile' here is an overloaded metod based on type of paramenter passed.
  *
  * Parses a file containing sql statements into a String array that contains
  * only the sql statements. Comments and white spaces in the file are not
  * parsed into the String array. Note the file must not contained malformed
  * comments and all sql statements must end with a semi-colon ";" in order
  * for the file to be parsed correctly. The sql statements in the String
  * array will not end with a semi-colon ";".
  *
  * @param sqlFile
  *            - BufferedReader for the file that contains sql statements.
  *
  * @return String array containing the sql statements.
  */
  public static String[] parseSqlFile(BufferedReader sqlFile) throws IOException {
    String line;
    StringBuilder sql = new StringBuilder();
    String multiLineComment = null;

    while ((line = sqlFile.readLine()) != null) {
      line = line.trim();

      // Check for start of multi-line comment
      if (multiLineComment == null) {
        // Check for first multi-line comment type
        if (line.startsWith("/*")) {
          if (!line.endsWith("}")) {
            multiLineComment = "/*";
          }
          // Check for second multi-line comment type
        } else if (line.startsWith("{")) {
          if (!line.endsWith("}")) {
            multiLineComment = "{";
          }
          // Append line if line is not empty or a single line comment
        } else if (!line.startsWith("--") && !line.equals("")) {
          sql.append(line);
        } // Check for matching end comment
      } else if (multiLineComment.equals("/*")) {
        if (line.endsWith("*/")) {
          multiLineComment = null;
        }
        // Check for matching end comment
      } else if (multiLineComment.equals("{")) {
        if (line.endsWith("}")) {
          multiLineComment = null;
        }
      }

    }

    sqlFile.close();

    return sql.toString().split(";");
  }
}
