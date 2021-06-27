package android.jun.birdwatch.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.jun.birdwatch.database.BirdDbSchema.BirdTable;

public class BirdBaseHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "birdBase.db";

    public BirdBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + BirdTable.NAME + " (" +
            " _id integer primary key autoincrement," +
            BirdTable.Cols.UUID + ", " +
            BirdTable.Cols.NAME + ", " +
            BirdTable.Cols.DATE + ", " +
            BirdTable.Cols.DESCRIPTION +
            ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
