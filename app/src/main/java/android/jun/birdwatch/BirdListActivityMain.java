package android.jun.birdwatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

public class BirdListActivityMain extends AppCompatActivity {

    protected Fragment createFragment() {
        return new BirdListFragment();
    }

    //Finals
    public static final String CURRENT_NIGHT_MODE = "mCurrentNightMode";
    public static final String SUBTITLE_VISIBILITY = "mSubtitleVisibility";
    private static final String EXTRA_BIRD_ID  = "bird_id";

    //Variables
    private boolean mCurrentNightMode;
    private boolean mSubtitleVisibility;
    List<Bird> mBirds;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird_list);
        //Linking up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            //Restore value of members from saved state
            mCurrentNightMode = savedInstanceState.getBoolean(CURRENT_NIGHT_MODE);
            mSubtitleVisibility = savedInstanceState.getBoolean(SUBTITLE_VISIBILITY);
        }
        else {
            //Initialize a new instance with default values
            mCurrentNightMode = false;
            mSubtitleVisibility = false;
        }

        //Checking Night mode and setting appropriately
        if(mCurrentNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            mCurrentNightMode = true;
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            mCurrentNightMode = false;
        }
        getDelegate().applyDayNight();

        mBirds = BirdList.get(this).getBirds();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container,fragment)
                    .commit();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        //Saving current states
        savedInstanceState.putBoolean(CURRENT_NIGHT_MODE, mCurrentNightMode);
        savedInstanceState.putBoolean(SUBTITLE_VISIBILITY, mSubtitleVisibility);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    // We need to setup the menu bar in the app bar
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();  // pop up menu on the app bar
        menuInflater.inflate(R.menu.main_menu, menu); // get menu from the xml file, main_menu.xml

        MenuItem showBird = menu.findItem(R.id.action_show_bird_count);
        if(mSubtitleVisibility){
            showBird.setTitle(R.string.action_bar_hide_bird_count);
        }
        else{
            showBird.setTitle(R.string.action_bar_show_bird_count);
        }

        return super.onCreateOptionsMenu(menu);  // return menu object
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_show_bird_count:
                //Toggling show bird count
                mSubtitleVisibility = !mSubtitleVisibility;
                invalidateOptionsMenu();    //To recreate the options menu
                updateSubtitle();
                return true;

            case R.id.action_new_bird:
                //Starting activity with new Bird
                Intent intent = BirdPagerActivity.newIntent(this, BirdList.get(this).addBird());
                startActivity(intent);
                return true;

            case R.id.action_switch_theme:
                if(mCurrentNightMode) {
                    // Dark mode is active, switching to light mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    getDelegate().applyDayNight();
                    mCurrentNightMode = false;
                }
                else{
                    // Light mode is active, bravo six going dark
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    getDelegate().applyDayNight();
                    mCurrentNightMode = true;
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Showing the subtitle under the app name on the toolbar
    private void updateSubtitle() {
        //Getting number of birds
        BirdList birdList = BirdList.get(this);
        int birdCount = birdList.getBirds().size();
        String subtitle = getString(R.string.bird_count_format, birdCount);

        if(!mSubtitleVisibility){
            subtitle = null;
        }

        this.getSupportActionBar().setSubtitle(subtitle);
    }

}