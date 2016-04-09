package lu.ing.gameofcode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import lu.ing.gameofcode.line.BusStop;

public class PlanningActivity extends AppCompatActivity {

    @Bind(R.id.timeline_rv)
    RecyclerView mRecyclerView;

    @Bind(R.id.rainy)
    RelativeLayout rainyLayout;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private boolean raining = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);
        ButterKnife.bind(this);

        if (raining) {
            mRecyclerView.setVisibility(View.GONE);
            rainyLayout.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            rainyLayout.setVisibility(View.GONE);
        }

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(new BusStop[] {
                createBusStop("Arret 1", "35 min de marche", false),
                createBusStop("Arret 2", "30 min de marche", false),
                createBusStop("Arret 3", "25 min de marche", false),
                createBusStop("Arret 4", "20 min de marche", false),
                createBusStop("Arret 5", "15 min de marche", true),
                createBusStop("Arret 6", "10 min de marche", false)
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private BusStop createBusStop(final String name, final String time, final boolean stop) {
        return new BusStop() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getWalkTimeToWork() {
                return time;
            }

            @Override
            public boolean shouldStopHere() {
                return stop;
            }
        };
    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private BusStop[] mDataset;

        public static class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.title_tv)
            public TextView mTextView;

            public ViewHolder(View v) {
                super(v);
                ButterKnife.bind(this, v);
            }
        }

        public MyAdapter(BusStop[] myDataset) {
            mDataset = myDataset;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_timeline, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final BusStop busStop = mDataset[position];
            if (busStop.shouldStopHere()) {
                holder.mTextView.setText(String.format("%s\n%s", busStop.getName(), busStop.getWalkTimeToWork()));
            } else {
                holder.mTextView.setText(busStop.getName());
            }
        }

        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }


}
