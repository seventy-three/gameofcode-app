package lu.ing.gameofcode.model;

import java.util.List;

/**
 * Created by patrice on 09.04.16.
 */
public class BusLine {

    String code;
    String name;
    List<BusStop> way1;
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

    public List<BusStop> getWay1() {
        return way1;
    }

    public void setWay1(List<BusStop> way1) {
        this.way1 = way1;
    }

    public List<BusStop> getWay2() {
        return way2;
    }

    public void setWay2(List<BusStop> way2) {
        this.way2 = way2;
    }
}
