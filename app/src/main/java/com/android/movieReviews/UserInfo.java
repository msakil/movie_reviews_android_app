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

public class UserInfo {

  public static final String KEY_ROWID = "_id";
  public static final String KEY_FNAME = "user_first_name";
  public static final String KEY_LNAME = "user_last_name";
  public static final String KEY_EMAIL = "user_email_id";
  public static final String KEY_PASSWORD = "user_password";
  public static final String KEY_PHOTO = "user_photo";

  private static final String DATABASE_NAME = "UserInfoDb";
  private static final String DATABASE_TABLE = "userInfoTable";
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
                  KEY_FNAME + " TEXT NOT NULL, " +
                  KEY_LNAME + " TEXT NOT NULL, " +
                  KEY_EMAIL + " TEXT NOT NULL, " +
                  KEY_PASSWORD + " TEXT NOT NULL, " +
                  KEY_PHOTO + " TEXT NOT NULL);"
      );
      try{
        InputStream is = mContext.getResources().getAssets().open("userinfo.sql");
        String[] statements = parseSqlFile(is);
        for(String statement : statements) {
          db.execSQL(statement);
        }
      }catch(Exception e) {
        //e.printStackTrace();
      }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
      onCreate(db);
    }
  }

  public UserInfo(Context c) {
    myContext = c;
  }

  public UserInfo open() throws SQLException {
    myHelper = new DbHelper(myContext);
    myDatabase = myHelper.getWritableDatabase();
    return this;
  }

  public void close() {
    myHelper.close();
  }

  public long createEntry(String fName, String lName, String email, String password, String photo) {
    ContentValues cv = new ContentValues();
    cv.put(KEY_FNAME, fName);
    cv.put(KEY_LNAME, lName);
    cv.put(KEY_EMAIL, email);
    cv.put(KEY_PASSWORD, password);
    cv.put(KEY_PHOTO, photo);
    return myDatabase.insert(DATABASE_TABLE, null, cv);
  }

  public String getData() {
    String[] columns = new String[]{KEY_ROWID, KEY_FNAME, KEY_LNAME, KEY_EMAIL, KEY_PASSWORD, KEY_PHOTO};
    Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
    String result = "";

    int iRow = c.getColumnIndex(KEY_ROWID);
    int iFname = c.getColumnIndex(KEY_FNAME);
    int iLname = c.getColumnIndex(KEY_LNAME);
    int iEmail = c.getColumnIndex(KEY_EMAIL);
    int iPassword = c.getColumnIndex(KEY_PASSWORD);
    int iPhoto = c.getColumnIndex(KEY_PHOTO);

    for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
      result = result + c.getString(iRow) + "  " +
               c.getString(iFname) + "  " +
               c.getString(iLname) + "  " +
               c.getString(iEmail) + "  " +
               c.getString(iPassword) + "  " +
               c.getString(iPhoto) + "\n" ;
    }
    return result;
  }

  public String getPassword(String emailId) {
    String[] columns = new String[]{KEY_ROWID, KEY_FNAME, KEY_LNAME, KEY_EMAIL, KEY_PASSWORD, KEY_PHOTO};
    Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
    Boolean userFound = false;
    String result = "";
    for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
      String userId = c.getString(3);
      if(userId.equals(emailId)) {
        result = c.getString(4);
        userFound = true;
      }
    }
    if(userFound) {
      return result;
    }else {
      return null;
    }
  }

  public String getFirstName(String emailId) {
    String[] columns = new String[]{KEY_ROWID, KEY_FNAME, KEY_LNAME, KEY_EMAIL, KEY_PASSWORD, KEY_PHOTO};
    Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
    Boolean userFound = false;
    String result = "";
    for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
      String userId = c.getString(3);
      if(userId.equals(emailId)) {
        result = c.getString(1);
        userFound = true;
      }
    }
    if(userFound) {
      return result;
    }else {
      return null;
    }
  }

  public long getRowCount() {
    return DatabaseUtils.queryNumEntries(myDatabase, DATABASE_TABLE);
  }

  public String getPhoto(String emailId) {
    String[] columns = new String[]{KEY_ROWID, KEY_FNAME, KEY_LNAME, KEY_EMAIL, KEY_PASSWORD, KEY_PHOTO};
    Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
    Boolean userFound = false;
    String result = "";
    for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
      String userId = c.getString(3);
      if(userId.equals(emailId)) {
        result = c.getString(5);
        userFound = true;
      }
    }
    if(userFound) {
      return result;
    }else {
      return null;
    }
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
