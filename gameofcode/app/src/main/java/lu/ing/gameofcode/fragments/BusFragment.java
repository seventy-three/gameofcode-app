package lu.ing.gameofcode.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import lu.ing.gameofcode.R;
import lu.ing.gameofcode.line.LineBean;
import lu.ing.gameofcode.line.LineBeanSorter;
import lu.ing.gameofcode.model.BusStop;
import lu.ing.gameofcode.requests.LineBeansRequest;
import lu.ing.gameofcode.services.BusService;
import lu.ing.gameofcode.services.BusServiceImpl;
import lu.ing.gameofcode.utils.MySpiceService;
import lu.ing.gameofcode.utils.SharedPreferencesUtils;
import lu.ing.gameofcode.veloh.VelohAlternative;
import lu.ing.gameofcode.veloh.VelohStationBean;

public class BusFragment extends Fragment {

    @Bind(R.id.timeline_rv)
    RecyclerView mRecyclerView;

    @Bind(R.id.buslines_sp)
    Spinner busLinesSpinner;

    @Bind(R.id.loading_pb)
    ProgressBar loadingView;

    private SpiceManager spiceManager = new SpiceManager(MySpiceService.class);
    private BusService busService;
    private List<BusStop> busStops;
    private List<VelohStationBean> velohStationBeen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bus, container, false);
        ButterKnife.bind(this, rootView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        busService = new BusServiceImpl(getContext());

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

    private LatLng getCoordinates(final SharedPreferences preferences, final String latProp, final String longProp) {

        return new LatLng(
                SharedPreferencesUtils.getDouble(preferences, latProp, 0),
                SharedPreferencesUtils.getDouble(preferences, longProp, 0));
    }

    private void loadBusStops(final String num) {
        showLoading();

        SharedPreferences preferences = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        final LatLng home = getCoordinates(preferences, "homeLatitude", "homeLongitude");
        final LatLng work = getCoordinates(preferences, "workLatitude", "workLongitude");
        busStops = busService.getBusStops(num, home, work);

        if (busStops == null) {
            hideLoading();
            return;
        }

        velohStationBeen = new ArrayList<>();
        for (BusStop busStop : busStops) {
            
        }

        RecyclerView.Adapter mAdapter = new MyAdapter(busStops);
        mRecyclerView.setAdapter(mAdapter);

        hideLoading();
    }

    private void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        loadingView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void setupBusLinesSpinner(final List<LineBean> lineBeen) {
        Collections.sort(lineBeen, new LineBeanSorter());
        final ArrayAdapter<LineBean> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lineBeen);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        busLinesSpinner.setAdapter(adapter);
        busLinesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadBusStops(lineBeen.get(position).getNum());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<lu.ing.gameofcode.model.BusStop> mDataset;

        public static class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.title_tv)
            public TextView mTextView;

            public ViewHolder(View v) {
                super(v);
                ButterKnife.bind(this, v);
            }
        }

        public MyAdapter(List<lu.ing.gameofcode.model.BusStop> myDataset) {
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
            final lu.ing.gameofcode.model.BusStop busStop = mDataset.get(position);
            holder.mTextView.setText(String.format(Locale.FRANCE, "%s\n%d minutes de marche",
                    busStop.getName(),
                    busStop.getPath().getTimeFoot() / 60));
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
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
