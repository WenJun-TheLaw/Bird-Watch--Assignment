package android.jun.birdwatch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BirdListFragment extends Fragment {
    //Finals
    public static final String CURRENT_NIGHT_MODE = "mCurrentNightMode";
    public static final String SUBTITLE_VISIBILITY = "mSubtitleVisibility";
    private static final String FRAGMENT_TAG  = "bird_list_fragment";

    //Variables
    private boolean mCurrentNightMode;
    private boolean mSubtitleVisibility;
    List<Bird> mBirds;
    private RecyclerView mBirdRecyclerView;
    private BirdAdapter mBirdAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mBirds = BirdList.get(requireActivity()).getBirds();

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_bird, container, false);

        mBirdRecyclerView = (RecyclerView) view.findViewById(R.id.bird_recycler_view);
        mBirdRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Checking Night mode and setting appropriately
        if(mCurrentNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            mCurrentNightMode = true;
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            mCurrentNightMode = false;
        }
        ((AppCompatActivity) requireActivity()).getDelegate().applyDayNight();

        updateUI();

        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);

        //Saving current states
        outState.putBoolean(CURRENT_NIGHT_MODE, mCurrentNightMode);
        outState.putBoolean(SUBTITLE_VISIBILITY, mSubtitleVisibility);
    }

    // We need to setup the menu bar in the app bar
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu); // get menu from the xml file, main_menu.xml

        MenuItem showBird = menu.findItem(R.id.action_show_bird_count);
        if(mSubtitleVisibility){
            showBird.setTitle(R.string.action_bar_hide_bird_count);
        }
        else{
            showBird.setTitle(R.string.action_bar_show_bird_count);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_show_bird_count:
                //Toggling show bird count
                mSubtitleVisibility = !mSubtitleVisibility;
                requireActivity().invalidateOptionsMenu();    //To recreate the options menu
                updateSubtitle();
                return true;

            case R.id.action_new_bird:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack(FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fm.beginTransaction()
                        .addToBackStack(FRAGMENT_TAG)
                        .commit();
                //Starting activity with new Bird
                Intent intent = BirdPagerActivity.newIntent(requireActivity(), BirdList.get(requireActivity()).addBird());
                startActivity(intent);
                return true;

            case R.id.action_switch_theme:
                if(mCurrentNightMode) {
                    // Dark mode is active, switching to light mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    ((AppCompatActivity) requireActivity()).getDelegate().applyDayNight();
                    mCurrentNightMode = false;
                }
                else{
                    // Light mode is active, bravo six going dark
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    ((AppCompatActivity) requireActivity()).getDelegate().applyDayNight();
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
        BirdList birdList = BirdList.get(requireActivity());
        int birdCount = birdList.getBirds().size();
        String subtitle = getString(R.string.bird_count_format, birdCount);

        if(!mSubtitleVisibility){
            subtitle = null;
        }

        ((AppCompatActivity) requireActivity()).getSupportActionBar().setSubtitle(subtitle);
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        BirdList birdList = BirdList.get(getActivity());
        List<Bird> birds = birdList.getBirds();

        if(mBirdAdapter == null){
            mBirdAdapter = new BirdAdapter(birds);
            mBirdRecyclerView.setAdapter(mBirdAdapter);
        }
        else{
            mBirdAdapter.setBirds(birds);
            mBirdAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private class BirdHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mNameTextView;
        private TextView mDescriptionTextView;
        private TextView mDateTextView;
        private ImageView mBirdPhoto;
        private File mPhotoFile;
        private Bird mBird;

        public BirdHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_bird, parent, false));
            itemView.setOnClickListener(this);
            mNameTextView = itemView.findViewById(R.id.bird_name);
            mDescriptionTextView = itemView.findViewById(R.id.bird_description);
            mDateTextView = itemView.findViewById(R.id.bird_date);
            mBirdPhoto = itemView.findViewById(R.id.bird_list_photo);
        }

        public void bind (Bird bird){
            mBird = bird;
            mNameTextView.setText(bird.getName());
            mDescriptionTextView.setText(bird.getDescription());
            mDateTextView.setText(bird.getDate().toString());
            mPhotoFile = BirdList.get(requireContext()).getPhotoFile(mBird);
            updatePhotoView();
        }

        @Override
        public void onClick(View view) {
            Intent intent = BirdPagerActivity.newIntent(getActivity(), mBird.getID());
            startActivity(intent);
        }

        private void updatePhotoView() {
            //If no photo, display nothing
            if (mPhotoFile == null || !mPhotoFile.exists()) {
                mBirdPhoto.setImageDrawable(null);
            }
            //IF got, scale the bitmap and dispay it
            else {
                Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), requireActivity());
                mBirdPhoto.setImageBitmap(bitmap);
            }
        }
    }

    private class BirdAdapter extends RecyclerView.Adapter<BirdHolder>{
        private List<Bird> mBirds;

        public BirdAdapter(List<Bird> birds){
            mBirds = birds;
        }

        @Override
        public BirdHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new BirdHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(BirdHolder holder, int position) {
            Bird bird = mBirds.get(position);
            holder.bind(bird);
        }

        @Override
        public int getItemCount() {
            return mBirds.size();
        }

        public void setBirds(List<Bird> birds){
            mBirds = birds;
        }
    }


}
