package android.jun.birdwatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BirdListFragment extends Fragment {
    private RecyclerView mBirdRecyclerView;
    private BirdAdapter mBirdAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment, container, false);

        mBirdRecyclerView = (RecyclerView) view.findViewById(R.id.bird_recycler_view);
        mBirdRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
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
            mBirdAdapter.notifyDataSetChanged();
        }
    }


    private class BirdHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mNameTextView;
        private TextView mDescriptionTextView;
        private TextView mDateTextView;
        private Bird mBird;

        public BirdHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_bird, parent, false));
            itemView.setOnClickListener(this);
            mNameTextView = (TextView) itemView.findViewById(R.id.bird_name);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.bird_description);
            mDateTextView = (TextView) itemView.findViewById(R.id.bird_date);
        }

        public void bind (Bird bird){
            mBird = bird;
            mNameTextView.setText(bird.getName());
            mDescriptionTextView.setText(bird.getDescription());
            mDateTextView.setText(bird.getDate().toString());
        }

        @Override
        public void onClick(View view) {
            Intent intent = BirdPagerActivity.newIntent(getActivity(), mBird.getID());
            startActivity(intent);
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
    }
}
