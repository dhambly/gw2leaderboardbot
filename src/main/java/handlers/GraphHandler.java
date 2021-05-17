package handlers;

import accounts.RatingSnapshot;
import com.mitchtalmadge.asciidata.graph.ASCIIGraph;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;

public class GraphHandler {
    public static String graphRatingHistory(LinkedList<RatingSnapshot> ratingSnapshots, int maxSize) {
        double[] arr;
        if (ratingSnapshots.size() > maxSize) {
            arr = new double[maxSize];
            for (int i = ratingSnapshots.size() - maxSize, j = 0; i < ratingSnapshots.size(); i++, j++) {
                arr[j] = ratingSnapshots.get(i).rating;
            }
        } else {
            arr = new double[ratingSnapshots.size()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = ratingSnapshots.get(i).rating;
            }
        }

        return ASCIIGraph
                .fromSeries(arr)
                .withNumRows(8)
                .withTickFormat(new DecimalFormat("0"))
                .withTickWidth(4)
                .plot();
    }

    //Currently hardcoded for hourly
    public static String getFormattedHourlyGraph(LinkedList<RatingSnapshot> ratingSnapshots) {
        String graph = graphRatingHistory(ratingSnapshots, 24);
        graph = graph.substring(0, graph.length() - 1);
        graph += "\n";
        graph += "     |     |     |     |    |\n";
        Instant curTime = Instant.now();
        Instant[] xAxis = new Instant[5];
        for (int i = 4, j = 0; i >= 0; i--,j++) {
            xAxis[i] = curTime.minus(j * 6L, ChronoUnit.HOURS);
        }
        xAxis[4] = curTime.minus(1, ChronoUnit.HOURS);
        StringBuilder timeLine = new StringBuilder(" ");
        var eastern = ZoneId.of("America/New_York");
        boolean prev2Digit = false;
        for (int i = 0; i < 5; i++) {
            int hour = xAxis[i].atZone(eastern).getHour();
            boolean am = hour < 12;
            if (hour > 12) hour -= 12;
            if (hour == 0) hour = 12;
            timeLine.append(" ");
            if (i != 4) timeLine.append(" ");
            if (!prev2Digit) timeLine.append(" ");
            if (hour > 9) prev2Digit = true;
            timeLine.append(hour).append(am ? "am" : "pm");
        }
        graph += timeLine.toString();
        return graph;
    }
}
