package handlers;

import accounts.apiobjects.GW2Account;
import accounts.apiobjects.GW2APIAccount;
import accounts.apiobjects.Season;
import accounts.apiobjects.SingleSeasonGW2Rating;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import leaderboardbot.Leaderboard;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

@SuppressWarnings("rawtypes")
public class APIHandler {
    private static final String GW2_APIURL = "https://api.guildwars2.com/v2";
    private static final String GW2API_SEASONURL = "https://api.guildwars2.com/v2/pvp/seasons";
    private static final String LEADERBOARDEXTENSION_NA = "/leaderboards/ladder/na";
    private static final String LEADERBOARDEXTENSION_EU = "/leaderboards/ladder/eu";
    private LinkedList seasonIDs;
    private Season latestSeason;

    private ArrayList<Season> seasons;

    public APIHandler() throws IOException {
        initializeSeasons();
    }

    private void initializeSeasons() throws IOException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        seasonIDs = mapper.readValue(
                new URL(GW2API_SEASONURL),
                LinkedList.class);
        seasons = new ArrayList<>();
        for (var seasonID : seasonIDs) {
            String IDstr = seasonID.toString();
            Season newSeason = mapper.readValue(
                    new URL(GW2API_SEASONURL
                            + "/"
                            + IDstr), Season.class);
            seasons.add(newSeason);
        }

        latestSeason = seasons.get(seasons.size() - 1);
        System.out.println("Season ID:" + latestSeason.getKey());
    }

    public void reinitializeSeasons() {
        try {
            initializeSeasons();
        } catch (IOException e) {
            System.out.println("Failed to re-load season keys");
            e.printStackTrace();
        }
    }

    public Leaderboard getLeaderboard(boolean isNA) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayList<GW2Account> list = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                list.addAll(mapper.readValue(
                        new URL(GW2API_SEASONURL
                                + "/"
                                + latestSeason.getKey()
                                + (isNA ? LEADERBOARDEXTENSION_NA : LEADERBOARDEXTENSION_EU)
                                + "?page="
                                + i),
                        new TypeReference<ArrayList<GW2Account>>() {
                        }));
            }
            list.forEach(a -> a.setSeason(latestSeason));
            return new Leaderboard(latestSeason, list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Leaderboard(latestSeason, new ArrayList<>());
    }

    public int getCurrentRatingFromAPIKey(String key) {
        int rating = -1;
        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayList<SingleSeasonGW2Rating> list = new ArrayList<>(mapper.readValue(
                    new URL(GW2_APIURL
                            + "/pvp/standings"
                            + "?access_token="
                            + key),
                    new TypeReference<ArrayList<SingleSeasonGW2Rating>>() {
                    }));
            for (SingleSeasonGW2Rating cur : list) {
                if (cur.getSeason_id().equals(latestSeason.getKey())) {
                    rating = cur.getCurrent().getRating();
                    System.out.println(cur.getSeason_id());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rating;
    }

    public String getAccountNameFromAPIKey(String key) {
        //https://api.guildwars2.com/v2/account?access_token=029A77EE-BEF0-9643-9BFA-BDD55733A2CCE15778C0-618E-4F78-8B5B-FC36D979D147
        String name = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            GW2APIAccount apiAccount = mapper.readValue(
                    new URL(GW2_APIURL
                            + "/account"
                            + "?access_token="
                            + key),
                    new TypeReference<GW2APIAccount>() {
                    });
            name = apiAccount.getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    public Season getLatestSeason() {
        return latestSeason;
    }
}
