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

}
