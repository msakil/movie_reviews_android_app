package com.android.movieReviews;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.SQLException;
import android.database.Cursor;
import android.content.ContentValues;
import android.database.DatabaseUtils;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MovieInfo {

  public static final String KEY_ROWID = "_id";
  public static final String KEY_MNAME = "movie_name";
  public static final String KEY_MDESC = "movie_desc";
  public static final String KEY_MRATING = "movie_rating";
  public static final String KEY_MPOSTER = "movie_poster";

  private static final String DATABASE_NAME = "MovieInfoDb";
  private static final String DATABASE_TABLE = "movieInfoTable";
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
                  KEY_MNAME + " TEXT NOT NULL, " +
                  KEY_MDESC + " TEXT NOT NULL, " +
                  KEY_MRATING + " TEXT NOT NULL, " +
                  KEY_MPOSTER + " TEXT NOT NULL);"
      );

      try {
        InputStream is = mContext.getResources().getAssets().open("movieinfo.sql");
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

  public MovieInfo(Context c) {
    myContext = c;
  }

  public MovieInfo open() throws SQLException {
    myHelper = new DbHelper(myContext);
    myDatabase = myHelper.getWritableDatabase();
    return this;
  }

  public void close() {
    myHelper.close();
  }

  public String getData() {
    String[] columns = new String[]{KEY_ROWID, KEY_MNAME, KEY_MDESC, KEY_MRATING, KEY_MPOSTER};
    Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
    String result = "";

    int iRow = c.getColumnIndex(KEY_ROWID);
    int iMname = c.getColumnIndex(KEY_MNAME);
    int iMdesc = c.getColumnIndex(KEY_MDESC);
    int iMrating = c.getColumnIndex(KEY_MRATING);
    int iMposter = c.getColumnIndex(KEY_MPOSTER);

    for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
      result = result + c.getString(iRow) + "  " +
               c.getString(iMname) + "  " +
               c.getString(iMdesc) + "  " +
               c.getString(iMrating) + "  " +
               c.getString(iMposter) + "\n";
    }
    return result;
  }

  public long getRowCount() {
    return DatabaseUtils.queryNumEntries(myDatabase, DATABASE_TABLE);
  }

  public String getMname(int pos) {
    String[] columns = new String[]{KEY_ROWID, KEY_MNAME, KEY_MDESC, KEY_MRATING, KEY_MPOSTER};
    Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
    String result = "";
    c.moveToPosition(pos);
    result = c.getString(1);
    return result;
  }

  public String getMdesc(int pos) {
    String[] columns = new String[]{KEY_ROWID, KEY_MNAME, KEY_MDESC, KEY_MRATING, KEY_MPOSTER};
    Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
    String result = "";
    c.moveToPosition(pos);
    result = c.getString(2);
    return result;
  }

  public String getMrating(int pos) {
    String[] columns = new String[]{KEY_ROWID, KEY_MNAME, KEY_MDESC, KEY_MRATING, KEY_MPOSTER};
    Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
    String result = "";
    c.moveToPosition(pos);
    result = c.getString(3);
    return result;
  }

  public String getMposter(int pos) {
    String[] columns = new String[]{KEY_ROWID, KEY_MNAME, KEY_MDESC, KEY_MRATING, KEY_MPOSTER};
    Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
    String result = "";
    c.moveToPosition(pos);
    result = c.getString(4);
    return result;
  }

  public long createEntry(String mName, String mDesc, String mRating, String mPoster) {
    ContentValues cv = new ContentValues();
    cv.put(KEY_MNAME, mName);
    cv.put(KEY_MDESC, mDesc);
    cv.put(KEY_MRATING, mRating);
    cv.put(KEY_MPOSTER, mPoster);
    return myDatabase.insert(DATABASE_TABLE, null, cv);
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
