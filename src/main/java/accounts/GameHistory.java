package accounts;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

public class GameHistory {


    public ArrayList<RatingSnapshot> getRatingHistory() {
        return ratingHistory;
    }

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
        ratingHistory.add(ratingSnapshot);
    }
}
