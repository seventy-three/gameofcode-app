package lu.ing.gameofcode.services;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import lu.ing.gameofcode.geojson.GeoJsonData;
import lu.ing.gameofcode.geojson.GeoJsonParser;
import lu.ing.gameofcode.model.BusData;
import lu.ing.gameofcode.model.BusLine;
import lu.ing.gameofcode.model.BusStop;

/**
 * Created by patrice on 09.04.16.
 */
public class BusServiceImpl implements BusService {

    private BusData busData = null;

    public BusServiceImpl(BusData busData) {
        this.busData = busData;
    }

    public void initBusData() {
        if (busData != null) {
            return;
        }
        GeoJsonParser parser = new GeoJsonParser();
        List<GeoJsonData> jsonData;
        try {
            jsonData = parser.readData();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        BusDataGenerator generator = new BusDataGenerator(jsonData);
        generator.interpretBusLines();
        busData = generator.getBusData();
    }

    @Override
    public List<BusStop> getBusStops(final String busLineNumber, LatLng start, LatLng stop) {
        initBusData();
        for (BusLine line : busData.getLines()) {
            if (busLineNumber.equals(line.getCode())) {
                return line.getWay();
            }
        }
        return null;
    }
}
