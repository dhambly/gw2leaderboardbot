package accounts;

import accounts.apiobjects.GW2Account;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;

public class GameHistory {


    ArrayList<RatingSnapshot> ratingHistory;

    GW2Account gw2Account;

    public GameHistory(GW2Account gw2Account) {
        this.gw2Account = gw2Account;
        this.ratingHistory = new ArrayList<>();
    }


    public void addGameHistory(short rating, short wins, short losses, Timestamp time) {
        RatingSnapshot ratingSnapshot = new RatingSnapshot();
        ratingSnapshot.rating = rating;
        ratingSnapshot.wins = wins;
        ratingSnapshot.losses = losses;
        ratingSnapshot.time = time;
        if (!ratingSnapshot.isSameScores(getLastRatingSnapshot()))
            ratingHistory.add(ratingSnapshot);
    }

    public RatingSnapshot getLastRatingSnapshot() {
        if (ratingHistory.size() > 0)
            return ratingHistory.get(ratingHistory.size() - 1);
        else return null;
    }

    public ArrayList<RatingSnapshot> getRatingHistory() {
        return ratingHistory;
    }

    public ArrayList<RatingSnapshot> getCleanedRatingHistory() {
        ArrayList<RatingSnapshot> cleaned = new ArrayList<>(ratingHistory);
        int i = 1;
        while (i < cleaned.size()) {
            RatingSnapshot prev = cleaned.get(i - 1);
            RatingSnapshot cur = cleaned.get(i);
            if (prev.isSameScores(cur)) {
                cleaned.remove(i);
            } else {
                i++;
            }
        }
        return cleaned;
    }

    public LinkedList<RatingSnapshot> generateFullRatingHistory() {
        LinkedList<RatingSnapshot> full = new LinkedList<>(ratingHistory);
        int n = full.size();
        for (int i = 0; i < full.size() - 1; i++) {
            RatingSnapshot cur = full.get(i);
            RatingSnapshot next = full.get(i + 1);
            if (next.time.getTime() - cur.time.getTime() > (1000 * 60 * 65)) {

            }
        }

        return full;
    }

    public LinkedList<RatingSnapshot> generateDailyRatingHistory() {
        LinkedList<RatingSnapshot> daily = new LinkedList<>();
        ArrayList<RatingSnapshot> ratingSnapshots = new ArrayList<>(this.ratingHistory);
        RatingSnapshot currentFakeSnapshot = ratingSnapshots.get(ratingSnapshots.size()-1).clone();
        Instant instant = Instant.now().truncatedTo(ChronoUnit.DAYS);
        currentFakeSnapshot.time = Timestamp.from(instant);
        ratingSnapshots.add(currentFakeSnapshot);
        try {
            daily.add(ratingSnapshots.get(0));
            Instant firstDay = daily.getFirst().time.toInstant().truncatedTo(ChronoUnit.DAYS);
            for (int j = 1, ratingHistorySize = ratingSnapshots.size(); j < ratingHistorySize; j++) {
                RatingSnapshot ratingSnapshot = ratingSnapshots.get(j);
                Instant prevTime = daily.getLast().time.toInstant()
                        .truncatedTo(ChronoUnit.DAYS);
                Instant nextTime = ratingSnapshot.time.toInstant()
                        .truncatedTo(ChronoUnit.DAYS);
                if (prevTime.equals(nextTime) && !nextTime.equals(firstDay)) {
                    daily.removeLast();
                    daily.add(ratingSnapshot);
                } else {
                    int days = amountOfDaysBetweenInstants(prevTime, nextTime);
                    if (days > 1) {
                        for (int i = 1; i < days; i++) {
                            RatingSnapshot temp = new RatingSnapshot();
                            temp.rating = daily.getLast().rating;
                            temp.time = Timestamp.from(daily.getLast().time.toInstant().plus(1, ChronoUnit.DAYS));
                            daily.add(temp);
                        }
                    }
                    daily.add(ratingSnapshot);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return daily;
    }

    public LinkedList<RatingSnapshot> generateHourlyRatingHistory() {
        LinkedList<RatingSnapshot> hourly = new LinkedList<>();
        ArrayList<RatingSnapshot> ratingSnapshots = new ArrayList<>(this.ratingHistory);
        RatingSnapshot currentFakeSnapshot = ratingSnapshots.get(ratingSnapshots.size()-1).clone();
        Instant instant = Instant.now().truncatedTo(ChronoUnit.HOURS);
        currentFakeSnapshot.time = Timestamp.from(instant);
        ratingSnapshots.add(currentFakeSnapshot);
        try {
            hourly.add(ratingSnapshots.get(0));
            Instant firstHour = hourly.getFirst().time.toInstant();
            for (int j = 1, ratingHistorySize = ratingSnapshots.size(); j < ratingHistorySize; j++) {
                RatingSnapshot ratingSnapshot = ratingSnapshots.get(j);
                Instant prevTime = hourly.getLast().time.toInstant();
                Instant nextTime = ratingSnapshot.time.toInstant();
                if (prevTime.equals(nextTime) && !nextTime.equals(firstHour)) {
                    hourly.removeLast();
                    hourly.add(ratingSnapshot);
                } else {
                    int hours = amountOfHoursBetweenInstants(prevTime, nextTime);
                    if (hours > 1) {
                        for (int i = 1; i < hours; i++) {
                            RatingSnapshot temp = new RatingSnapshot();
                            temp.rating = hourly.getLast().rating;
                            temp.time = Timestamp.from(hourly.getLast().time.toInstant().plus(1, ChronoUnit.HOURS));
                            hourly.add(temp);
                        }
                    }
                    hourly.add(ratingSnapshot);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hourly;
    }

    public int amountOfDaysBetweenInstants(Instant before, Instant after) {
        return (int) ChronoUnit.DAYS.between(before, after);
    }

    public int amountOfHoursBetweenInstants(Instant before, Instant after) {
        return (int) ChronoUnit.HOURS.between(before, after);
    }
}
