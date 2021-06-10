package android.jun.birdwatch;

import androidx.fragment.app.Fragment;
import android.os.Bundle;

public class BirdActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new BirdFragment();
    }





}