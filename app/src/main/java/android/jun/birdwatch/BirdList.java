package android.jun.birdwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.jun.birdwatch.database.BirdBaseHelper;
import android.jun.birdwatch.database.BirdCursorWrapper;
import android.jun.birdwatch.database.BirdDbSchema;
import android.jun.birdwatch.database.BirdDbSchema.BirdTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BirdList {
    private static BirdList sBirdList;
    private List<Bird> mBirds;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static BirdList get(Context context) {
        if (sBirdList == null) {
            sBirdList = new BirdList(context);
        }
        return sBirdList;
    }

    //Constructor
    public BirdList(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new BirdBaseHelper(context).getWritableDatabase();
        mBirds = new ArrayList<>();
    }

    //Return birds
    public List<Bird> getBirds(){
        List<Bird> birds = new ArrayList<>();

        BirdCursorWrapper cursor = queryBirds(null, null);

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                birds.add(cursor.getBird());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return birds;
    }

    //Find a bird
    public Bird getBird (UUID id){
        String uuidString = id.toString();
        BirdCursorWrapper cursor = queryBirds(BirdTable.Cols.UUID + " = ?", new String[]{ uuidString});

        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getBird();
        } finally {
            cursor.close();
        }
    }

    //Updating the bird's info in teh db
    public void updateBird(Bird bird){
        String uuidString = bird.getID().toString();
        ContentValues values = getContentValues(bird);

        mDatabase.update(BirdTable.NAME, values, BirdTable.Cols.UUID + " = ?", new String[] { uuidString});
    }

    //Add a bird
    public UUID addBird(){
        Bird bird = new Bird();
        ContentValues values = getContentValues(bird);
        mDatabase.insert(BirdTable.NAME, null, values);
        return bird.getID();
    }

    //Remove a bird... aw :(
    public void removeBird(UUID id){
        String uuidString = id.toString();
        mDatabase.delete(BirdTable.NAME, BirdTable.Cols.UUID + " = ?", new String[] { uuidString});
    }

    //Converting bird data into ContentValues to we written to DB
    private static ContentValues getContentValues(Bird bird){
        ContentValues values = new ContentValues();
        values.put(BirdTable.Cols.UUID, bird.getID().toString());
        values.put(BirdTable.Cols.NAME, bird.getName());
        values.put(BirdTable.Cols.DATE, bird.getDate().getTime());
        values.put(BirdTable.Cols.DESCRIPTION, bird.getDescription());

        return values;
    }

    //Querying a bird from the DB
    private BirdCursorWrapper queryBirds(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                BirdTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new BirdCursorWrapper(cursor);
    }
}