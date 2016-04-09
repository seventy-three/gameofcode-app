package lu.ing.gameofcode;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lu.ing.gameofcode.line.BusLine;
import lu.ing.gameofcode.line.BusStop;
import lu.ing.gameofcode.line.LineBean;

public class BusFragment extends Fragment {

    @Bind(R.id.timeline_rv)
    RecyclerView mRecyclerView;

    @Bind(R.id.buslines_sp)
    Spinner busLinesSpinner;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SpiceManager spiceManager = new SpiceManager(MySpiceService.class);;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bus, container, false);
        ButterKnife.bind(this, rootView);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
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

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        spiceManager.start(getContext());

        // lignes disponibles du point de depart à l'arrivée
        SharedPreferences preferences = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);

        final String homeLongitude = Double.toString(SharedPreferencesUtils.getDouble(preferences, "homeLongitude", 0));
        final String homeLatitude = Double.toString(SharedPreferencesUtils.getDouble(preferences, "homeLatitude", 0));
        final String workLongitude = Double.toString(SharedPreferencesUtils.getDouble(preferences, "workLongitude", 0));
        final String workLatitude = Double.toString(SharedPreferencesUtils.getDouble(preferences, "workLatitude", 0));

        spiceManager.execute(new SpiceRequest<LineBean[]>(LineBean[].class) {
            @Override
            public LineBean[] loadDataFromNetwork() throws Exception {
                try {
                    final BusLine line = new BusLine(getContext());
                    final LineBean[] startList = line.getAvailableLines(homeLatitude, homeLongitude);
                    final LineBean[] endList = line.getAvailableLines(workLatitude, workLongitude);
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
                setupBusLinesSpinner(Arrays.asList(lines));
            }
        });
    }

    private void setupBusLinesSpinner(List<LineBean> lineBeen) {
        Collections.sort(lineBeen, new LineBeanSorter());
        final ArrayAdapter<LineBean> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lineBeen);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        busLinesSpinner.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        spiceManager.shouldStop();
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

    private class LineBeanSorter implements Comparator<LineBean> {
        @Override
        public int compare(LineBean lhs, LineBean rhs) {
            return lhs.getNum().compareTo(rhs.getNum());
        }
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
}
