public class GW2Account {
    private String name;
    private int rank;
    private String date;
    private GW2AccountScores[] scores;
    private int rating;
    private int wins;
    private int losses;

    public String toString() {
        return String.format(
                "Rank %d: %s   %d rating  %dW %dL",
                getRank(),
                getName(),
                getRating(),
                getWins(),
                getLosses()
        );
    }

    public String getScores() {
        return String.format("%d rating %dW %dL", rating, wins, losses);
    }

    public void setScores(GW2AccountScores[] s) {
        this.scores = s;
        this.rating = s[0].getValue();
        this.wins = s[1].getValue();
        this.losses = s[2].getValue();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }


}
