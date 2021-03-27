import java.awt.*;
import java.awt.font.FontRenderContext;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GW2Account implements Serializable {
    private String name;
    private int rank;
    private String date;
    private GW2AccountScores[] scores;
    private int rating;
    private int wins;
    private int losses;
    private boolean onLeaderboard;

    public String toString() {
        return String.format(
                "Rank %d: %s   %d rating  %d - %d",
                getRank(),
                getName(),
                getRating(),
                getWins(),
                getLosses()
        );
    }

    public String reformattedToString() {
        return String.format(
                "Rank %s%d: **%s**%s*%4d*  (%d-%d)",
                getRank() >= 10 ? "" : " ",
                getRank(),
                getName(),
                generateWhiteSpace(150 - getBoldNameWidth()),
                getRating(),
                getWins(),
                getLosses()
        );
    }

    public GW2Account() {
        onLeaderboard = true;
    }

    public String getFormattedDate() {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        SimpleDateFormat output = new SimpleDateFormat("E yyyy/MM/dd 'at' hh:mm:ss a 'ET'");
        try {
            Date t = ft.parse(date);
            t.setTime(t.getTime() - 1000 * 60 * 60 * 4);
            return output.format(t);
        } catch (ParseException e) {
            return "????";
        }
    }

    private String generateWhiteSpace(int w) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < w + 2; i += 3) {
            s.append(" ");
        }
        return s.toString();
    }

    private int getBoldNameWidth() {
        Font font = new Font("Uni Sans", Font.TYPE1_FONT, 12);
        return (int) font.getStringBounds(getName(),
                new FontRenderContext(font.getTransform(), true, true))
                .getBounds().getWidth();
    }

    //Parameter is the filename in src/main/accountsets directory containing list of accounts
    public boolean isA(String listName) {
        return AccountListHandler.accountIsA(this.getName(), listName);
    }

    public boolean isAChimp() {
        return AccountListHandler.isAChimp(name);
    }

    public boolean isAnEgirl() {
        return AccountListHandler.isAnEgirl(name);
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

    public boolean isOnLeaderboard() {
        return onLeaderboard;
    }

    public void setOnLeaderboard(boolean onLeaderboard) {
        this.onLeaderboard = onLeaderboard;
    }


}
