package lu.ing.gameofcode.model;

/**
 * Created by patrice on 09.04.16.
 */
public class BusStop {

    public enum Direction {WAY1, WAY2, BOTH}
    BusLine parentBusLine;
    String name;
    BusPath path;
    Direction direction;

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

    public BusPath getPath() {
        return path;
    }

    public void setPath(BusPath path) {
        this.path = path;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "BusStop{" +
                "name='" + name + '\'' +
                ", path=" + path +
                ", direction=" + direction +
                '}';
    }
}
