package lu.ing.gameofcode;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lu.ing.gameofcode.line.BusStop;
import lu.ing.gameofcode.line.LineBean;
import lu.ing.gameofcode.line.LineBeanSorter;

public class BusFragment extends Fragment {

    @Bind(R.id.timeline_rv)
    RecyclerView mRecyclerView;

    @Bind(R.id.buslines_sp)
    Spinner busLinesSpinner;

    @Bind(R.id.loading_pb)
    ProgressBar loadingView;

    private SpiceManager spiceManager = new SpiceManager(MySpiceService.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bus, container, false);
        ButterKnife.bind(this, rootView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getContext());
        loadBusLines();
    }

    @Override
    public void onStop() {
        super.onStop();
        spiceManager.shouldStop();
    }

    private void loadBusLines() {
        showLoading();
        SharedPreferences preferences = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        final String homeLongitude = Double.toString(SharedPreferencesUtils.getDouble(preferences, "homeLongitude", 0));
        final String homeLatitude = Double.toString(SharedPreferencesUtils.getDouble(preferences, "homeLatitude", 0));
        final String workLongitude = Double.toString(SharedPreferencesUtils.getDouble(preferences, "workLongitude", 0));
        final String workLatitude = Double.toString(SharedPreferencesUtils.getDouble(preferences, "workLatitude", 0));

        final LineBeansRequest request = new LineBeansRequest(getContext(),
                homeLatitude, homeLongitude,
                workLatitude, workLongitude);
        spiceManager.execute(request, new BusLinesRequestListener());
    }

    private void loadBusStops() {
        showLoading();
        
        RecyclerView.Adapter mAdapter = new MyAdapter(new BusStop[]{
                createBusStop("Arret 1", "35 min de marche", false),
                createBusStop("Arret 2", "30 min de marche", false),
                createBusStop("Arret 3", "25 min de marche", false),
                createBusStop("Arret 4", "20 min de marche", false),
                createBusStop("Arret 5", "15 min de marche", true),
                createBusStop("Arret 6", "10 min de marche", false)
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        loadingView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void setupBusLinesSpinner(List<LineBean> lineBeen) {
        Collections.sort(lineBeen, new LineBeanSorter());
        final ArrayAdapter<LineBean> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lineBeen);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        busLinesSpinner.setAdapter(adapter);
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
            return new ViewHolder(v);
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

    private class BusLinesRequestListener implements RequestListener<LineBean[]> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            hideLoading();
        }

        @Override
        public void onRequestSuccess(LineBean[] lines) {
            hideLoading();
            setupBusLinesSpinner(Arrays.asList(lines));
        }
    }
}
