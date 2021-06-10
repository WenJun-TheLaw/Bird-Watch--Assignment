package android.jun.birdwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.List;
import java.util.UUID;

public class BirdPagerActivity extends AppCompatActivity {
    private static final String EXTRA_BIRD_ID = "bird_id";
    private ViewPager mViewPager;
    private List<Bird> mBirds;

    public static Intent newIntent(Context packageContext, UUID birdId){
        Intent intent = new Intent(packageContext, BirdPagerActivity.class);
        intent.putExtra(EXTRA_BIRD_ID, birdId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird_pager);

        UUID birdId = (UUID) getIntent().getSerializableExtra(EXTRA_BIRD_ID);
        mViewPager = (ViewPager) findViewById(R.id.bird_view_pager);
        mBirds = BirdList.get(this).getBirds();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Bird bird = mBirds.get(position);
                return BirdFragment.newInstance(bird.getID());
            }

            @Override
            public int getCount() {
                return mBirds.size();
            }
        });

        for(int i = 0; i < mBirds.size(); i++){
            if(mBirds.get(i).getID().equals(birdId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}