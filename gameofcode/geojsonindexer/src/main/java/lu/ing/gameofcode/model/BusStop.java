package lu.ing.gameofcode.model;

/**
 * Created by patrice on 09.04.16.
 */
public class BusStop {

    BusLine parentBusLine;
    String name;
    BusPath way1Path;
    BusPath way2Path;

    public BusStop() {
    }

    public BusStop(BusLine parentBusLine) {
        this.parentBusLine = parentBusLine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BusPath getWay1Path() {
        return way1Path;
    }

    public void setWay1Path(BusPath way1Path) {
        this.way1Path = way1Path;
    }

    public BusPath getWay2Path() {
        return way2Path;
    }

    public void setWay2Path(BusPath way2Path) {
        this.way2Path = way2Path;
    }
}
