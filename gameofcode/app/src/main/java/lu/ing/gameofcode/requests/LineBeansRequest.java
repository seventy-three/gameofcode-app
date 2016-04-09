package lu.ing.gameofcode.requests;

import android.content.Context;

import com.octo.android.robospice.request.SpiceRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lu.ing.gameofcode.line.BusLine;
import lu.ing.gameofcode.line.LineBean;

public class LineBeansRequest extends SpiceRequest<LineBean[]> {

    private final String homeLatitude;
    private final String homeLongitude;
    private final String workLatitude;
    private final String workLongitude;
    private final Context context;

    public LineBeansRequest(Context context, String homeLatitude, String homeLongitude, String workLatitude, String workLongitude) {
        super(LineBean[].class);
        this.homeLatitude = homeLatitude;
        this.homeLongitude = homeLongitude;
        this.workLatitude = workLatitude;
        this.workLongitude = workLongitude;
        this.context = context;
    }

    @Override
    public LineBean[] loadDataFromNetwork() throws Exception {
        try {
            final BusLine line = new BusLine(context);
            final LineBean[] startList = line.getAvailableLines(homeLatitude, homeLongitude);
            final LineBean[] endList = line.getAvailableLines(workLatitude, workLongitude);
            List<LineBean> matchList = new ArrayList<>();
            for (final LineBean lineS : startList) {
                if (null != lineS.getNum()) {
                    for (final LineBean lineE : endList) {
                        if (lineS.getNum().equals(lineE.getNum())) {
                            matchList.add(lineE);
                            break;
                        }
                    }
                }
            }
            return Arrays.copyOf(matchList.toArray(), matchList.size(), LineBean[].class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
