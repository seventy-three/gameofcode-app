package lu.ing.gameofcode;

/**
 * Created by patrice on 09.04.16.
 */
public abstract class GeoJsonItem {

    private String name;

    public GeoJsonItem() {
    }

    public GeoJsonItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
