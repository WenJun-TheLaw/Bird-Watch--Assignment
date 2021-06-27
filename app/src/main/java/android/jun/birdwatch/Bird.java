package android.jun.birdwatch;

import java.util.Date;
import java.util.UUID;

public class Bird {
    private UUID mID;
    private String mName;
    private Date mDate;
    private String description;

    public UUID getID() {
        return mID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bird(){
        this(UUID.randomUUID());
    }

    public Bird(UUID id){
        mID = id;
        mDate = new Date();
    }

    public String getPhotoFilename(){
        return "IMG_" + getID().toString() + ".jpg";
    }
}
