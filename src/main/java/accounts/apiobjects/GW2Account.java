package accounts.apiobjects;

import accounts.AccountListHandler;
import handlers.TextWidthHelper;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GW2Account implements Serializable {
    private String name;
    private int rank;
    private String date;
    private GW2AccountScores[] scores;
    private short rating;
    private short wins;
    private short losses;
    private long time;
    private boolean onLeaderboard;
    private Season season;


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
                "Rank %s%d: **%s**%s *%4d*  (%d-%d)",
                getRank() >= 10 ? "" : " ",
                getRank(),
                getName(),
                generateWhiteSpace(240 - getBoldNameWidth()),
                getRating(),
                getWins(),
                getLosses()
        );
    }

    public GW2Account() {
        onLeaderboard = true;
    }

    public String getFormattedDate() {
        SimpleDateFormat output = new SimpleDateFormat("E yyyy/MM/dd 'at' hh:mm:ss a 'ET'");
        Date t = new Date(time);
        return output.format(t);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    private String generateWhiteSpace(int w) {
        StringBuilder s = new StringBuilder();
        double spaces = ((double) w - 0.5)/4.5;
        for (int i = 0; i < spaces; i++){
            s.append(" ");
        }
        return s.toString();
    }

    private int getBoldNameWidth() {
        return TextWidthHelper.getBoldWidth(getName());
//        Font font = new Font("Uni Sans", Font.TYPE1_FONT, 12);
//        return (int) font.getStringBounds(getName(),
//                new FontRenderContext(font.getTransform(), true, true))
//                .getBounds().getWidth();
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

    public String getNameToLower() {
        return name.toLowerCase();
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
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        try {
            Date t = ft.parse(date);
            t.setTime(t.getTime() + Calendar.getInstance().getTimeZone().getOffset(t.getTime()));
            this.time = t.getTime();
        } catch (ParseException e) { e.printStackTrace();}
    }

    public short getRating() {
        return rating;
    }

    public void setRating(short rating) {
        this.rating = rating;
    }

    public short getWins() {
        return wins;
    }

    public void setWins(short wins) {
        this.wins = wins;
    }

    public short getLosses() {
        return losses;
    }

    public void setLosses(short losses) {
        this.losses = losses;
    }

    public boolean isOnLeaderboard() {
        return onLeaderboard;
    }

    public void setOnLeaderboard(boolean onLeaderboard) {
        this.onLeaderboard = onLeaderboard;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }
}
