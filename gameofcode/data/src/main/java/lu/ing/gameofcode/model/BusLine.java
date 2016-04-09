package lu.ing.gameofcode.model;

import java.util.List;

/**
 * Created by patrice on 09.04.16.
 */
public class BusLine {

    String code;
    String name;
    List<BusStop> way;
    List<BusStop> way2;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BusStop> getWay() {
        return way;
    }

    public void setWay(List<BusStop> way) {
        this.way = way;
    }
}
