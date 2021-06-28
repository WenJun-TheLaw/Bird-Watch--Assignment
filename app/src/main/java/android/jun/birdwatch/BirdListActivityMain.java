package android.jun.birdwatch;

import androidx.annotation.Nullable;
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
    public static final String FRAGMENT_NAME = "bird_list_fragment";
    private Fragment mFragment;

    protected Fragment createFragment() {
        return new BirdListFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_bird_list);

        FragmentManager fm = getSupportFragmentManager();
        mFragment = fm.findFragmentById(R.id.fragment_container);

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            mFragment = getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_NAME);
        }
        else{
            mFragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, mFragment)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, FRAGMENT_NAME, mFragment);
    }
}