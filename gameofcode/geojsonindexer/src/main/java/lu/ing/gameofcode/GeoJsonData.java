package lu.ing.gameofcode;

import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by patrice on 09.04.16.
 */
public class GeoJsonData implements Serializable {

    private GeoJsonDataType type;
    private String id;
    private String name;
    private List<? extends GeoJsonItem> items = new ArrayList<>();

    public GeoJsonData(String id, GeoJsonDataType type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    @Transient
    public String getId() {
        return id;
    }

    public GeoJsonDataType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<? extends GeoJsonItem> getItems() {
        return items;
    }

    public void setItems(List<? extends GeoJsonItem> items) {
        this.items = items;
    }

    public long getTotalDistance() {
        long totalDistance = 0;
        for (GeoJsonItem item : items) {
            if (item instanceof GeoJsonItemPath) {
                totalDistance += GeoJsonItemPath.class.cast(item).getDistance();
            }
        }
        return totalDistance;
    }
}
