package lu.ing.gameofcode.line;

import java.util.Comparator;

public class LineBeanSorter implements Comparator<LineBean> {
    @Override
    public int compare(LineBean lhs, LineBean rhs) {
        return lhs.getNum().compareTo(rhs.getNum());
    }
}
