package android.jun.birdwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BirdListActivityMain extends AppCompatActivity {
    //Finals
    public static final String CURRENT_NIGHT_MODE = "mCurrentNightMode";

    //Variables
    private boolean mCurrentNightMode;

    protected Fragment createFragment() {
        return new BirdListFragment();
    }

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
        }
        else {
            //Initialize a new instance with default values
            mCurrentNightMode = true;
        }

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

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    // We need to setup the menu bar in the app bar
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();  // pop up menu on the app bar
        menuInflater.inflate(R.menu.menu, menu); // get menu from the xml file, main_menu.xml

        return super.onCreateOptionsMenu(menu);  // return menu object
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_new_bird:
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

}