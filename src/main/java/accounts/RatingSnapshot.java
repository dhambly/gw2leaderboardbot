package accounts;

import java.sql.Timestamp;

public class RatingSnapshot {
    public short rating;
    public short wins;
    public short losses;
    public Timestamp time;

    public boolean isSameScores(RatingSnapshot b) {
        return b != null &&
                this.rating == b.rating &&
                this.wins == b.wins &&
                this.losses == b.losses;
    }

    public boolean hasSameScoresAsAccount(GW2Account acc) {
        return acc != null &&
                this.rating == acc.getRating() &&
                this.wins == acc.getWins() &&
                this.losses == acc.getLosses();
    }

}
