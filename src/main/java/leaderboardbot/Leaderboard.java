package leaderboardbot;

import accounts.apiobjects.GW2Account;
import accounts.apiobjects.Season;

import java.util.ArrayList;

public class Leaderboard {
    Season season;
    ArrayList<GW2Account> accountList;

    public Leaderboard(Season season, ArrayList<GW2Account> accountList) {
        this.season = season;
        this.accountList = accountList;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public ArrayList<GW2Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(ArrayList<GW2Account> accountList) {
        this.accountList = accountList;
    }
}
