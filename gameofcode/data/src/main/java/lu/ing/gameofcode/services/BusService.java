package lu.ing.gameofcode.services;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import lu.ing.gameofcode.model.BusStop;

public interface BusService {

    List<BusStop> getBusStops(final int busLineNumber, LatLng start, LatLng stop);

}
