package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



import Fragments.FavouriteFiles;

import static com.example.spotified.Activities.PlayerActivity.listSongs;


public class FavDB extends SQLiteOpenHelper {



    private static int DB_VERSION = 1;
    private static String DB_NAME = "FavouriteDatabase";
    private static String TABLE_NAME = "FavouriteTable";
    public static String COLUMN_ID = "SongID";
    public static String COLUMN_SONG_TITLE = "SongTitle";
    public static String COLUMN_ARTIST = "SongArtist";
    public static String COLUMN_ALBUM = "SongAlbum";
    public static String COLUMN_PATH = "SongPath";
    public static String COLUMN_DURATION = "SongDuration";

    public static String FAVORITE_STATUS = "fStatus";

    private static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " TEXT," + COLUMN_SONG_TITLE+ " TEXT," +  COLUMN_ARTIST + " TEXT," + COLUMN_ALBUM + " TEXT," +  COLUMN_DURATION+ " TEXT," + COLUMN_PATH+ " TEXT," +
              FAVORITE_STATUS+ " TEXT)";

    public FavDB(Context context) { super(context,DB_NAME,null,DB_VERSION);}


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void insertEmpty()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for(int i=1; i< listSongs.size(); i++)
        {
           cv.put(COLUMN_ID,i);
           cv.put(FAVORITE_STATUS,"0");

           db.insert(TABLE_NAME,null,cv);
        }
    }

    public void insertIntoDatabase(String id, String title, String artist, String album, String duration, String path, String fav_status)
    {
        SQLiteDatabase db;
        db=this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID,id);
        cv.put(COLUMN_SONG_TITLE,title);
        cv.put(COLUMN_ARTIST,artist);
        cv.put(COLUMN_ALBUM,album);
        cv.put(COLUMN_DURATION,duration);
        cv.put(COLUMN_PATH,path);
        cv.put(FAVORITE_STATUS,fav_status);

        db.insert(TABLE_NAME, null, cv);
    }

    public Cursor read_all_data(String id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql= "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID+"="+id+"";
        return db.rawQuery(sql,null,null);
    }
    public void remove_fav(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE " + TABLE_NAME + " SET "+FAVORITE_STATUS+" ='0' WHERE "+COLUMN_ID+"="+id+"";
        db.execSQL(sql);
    }
    public Cursor selectAllList()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM "+TABLE_NAME+" WHERE "+FAVORITE_STATUS+" ='1'";
        return db.rawQuery(sql,null,null);
    }


}

