package lu.ing.gameofcode.services;

import java.util.List;

import lu.ing.gameofcode.model.BusStop;

public interface BusService {

    List<BusStop> getBusStops(final int busLineNumber);


}
