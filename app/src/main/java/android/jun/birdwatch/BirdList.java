package android.jun.birdwatch;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BirdList {
    private static BirdList sBirdList;
    private List<Bird> mBirds;

    public static BirdList get(Context context) {
        if (sBirdList == null) {
            sBirdList = new BirdList(context);
        }
        return sBirdList;
    }

    //Constructor
    public BirdList(Context context){
        mBirds = new ArrayList<>();
        //Generating a few sample birds
        for(int i = 0; i < 5 ; i++){
            Bird bird = new Bird();
            bird.setName("Sample Bird #" + (i+1));
            bird.setDescription("Just a normal bird.");
            mBirds.add(bird);
        }
    }

    //Return birds
    public List<Bird> getBirds(){
        return mBirds;
    }

    //Find a bird
    public Bird getBird (UUID id){
        for (Bird bird: mBirds){
            if(bird.getID().equals(id)){
                return bird;
            }
        }
        return null;
    }
}