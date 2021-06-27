package android.jun.birdwatch.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.jun.birdwatch.Bird;
import android.jun.birdwatch.database.BirdDbSchema.BirdTable;

import java.util.Date;
import java.util.UUID;

public class BirdCursorWrapper extends CursorWrapper {
    public BirdCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Bird getBird(){
        String uuidString = getString(getColumnIndex(BirdTable.Cols.UUID));
        String name = getString(getColumnIndex(BirdTable.Cols.NAME));
        long date = getLong(getColumnIndex(BirdTable.Cols.DATE));
        String description = getString(getColumnIndex(BirdTable.Cols.DESCRIPTION));

        Bird bird = new Bird(UUID.fromString(uuidString));
        bird.setName(name);
        bird.setDate(new Date(date));
        bird.setDescription(description);

        return bird;
    }
}
