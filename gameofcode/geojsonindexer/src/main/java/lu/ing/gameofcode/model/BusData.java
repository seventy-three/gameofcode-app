package lu.ing.gameofcode.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by patrice on 09.04.16.
 */
public class BusData {

    List<BusLine> lines = new ArrayList<>();
    List<BusPath> paths = new ArrayList<>();

    public List<BusLine> getLines() {
        return lines;
    }

    public void addLine(BusLine line) {
        this.lines.add(line);
    }

    public List<BusPath> getPaths() {
        return paths;
    }

    public void addPath(BusPath path) {
        this.paths.add(path);
    }
}
