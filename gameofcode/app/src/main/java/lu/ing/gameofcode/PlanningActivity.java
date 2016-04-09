package lu.ing.gameofcode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lu.ing.gameofcode.line.BusLine;
import lu.ing.gameofcode.line.BusStop;
import lu.ing.gameofcode.line.LineBean;

public class PlanningActivity extends AppCompatActivity {

    @Bind(R.id.timeline_rv)
    RecyclerView mRecyclerView;

    @Bind(R.id.sunny)
    RelativeLayout sunnyLayout;

    @Bind(R.id.rainy)
    RelativeLayout rainyLayout;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SpiceManager spiceManager = new SpiceManager(MySpiceService.class);;

    private boolean raining = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);
        ButterKnife.bind(this);

        if (raining) {
            sunnyLayout.setVisibility(View.GONE);
            rainyLayout.setVisibility(View.VISIBLE);
        } else {
            sunnyLayout.setVisibility(View.VISIBLE);
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

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);

        // lignes disponibles du point de depart à l'arrivée
        final BusLine line = new BusLine(this);
        spiceManager.execute(new SpiceRequest<LineBean[]>(LineBean[].class) {
            @Override
            public LineBean[] loadDataFromNetwork() throws Exception {
                try {
                    final LineBean[] startList = line.getAvailableLines("49599457","6132893");
                    final LineBean[] endList = line.getAvailableLines("49579455","6112891");
                    List<LineBean> matchList = new ArrayList<>();
                    for (final LineBean lineS : startList){
                        if(null!=lineS.getNum()) {
                            for (final LineBean lineE : endList) {
                                if (lineS.getNum().equals(lineE.getNum())){
                                    matchList.add(lineE);
                                    break;
                                }
                            }
                        }
                    }
                    return Arrays.copyOf(matchList.toArray(), matchList.size(), LineBean[].class);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }, new RequestListener<LineBean[]>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.d("MAIN","getAvailableLines Failure");
            }
            @Override
            public void onRequestSuccess(LineBean[] lines) {
                Log.d("MAIN","getAvailableLines Success nb matched="+lines.length);
                for (final LineBean line : lines) {
                    Log.d("MAIN", "getAvailableLines LINE N°"+line.getNum());
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        spiceManager.shouldStop();
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
