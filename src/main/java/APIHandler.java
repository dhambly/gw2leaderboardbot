import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

@SuppressWarnings("rawtypes")
public class APIHandler {
    private static final String GW2_APIURL = "https://api.guildwars2.com/v2";
    private static final String GW2API_SEASONURL = "https://api.guildwars2.com/v2/pvp/seasons";
    private static final String LEADERBOARDEXTENSION_NA = "/leaderboards/ladder/na";
    private static final String LEADERBOARDEXTENSION_EU = "/leaderboards/ladder/eu";
    private LinkedList seasonIDs;
    private Object latestSeason;

    public APIHandler() throws IOException {        initializeSeasons();
    }

    private void initializeSeasons() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        seasonIDs = mapper.readValue(
                new URL(GW2API_SEASONURL),
                LinkedList.class);
        latestSeason = seasonIDs.getLast();
    }

    public LinkedList<GW2Account> getLeaderboard()  {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(
                    new URL(GW2API_SEASONURL
                            + "/"
                            + latestSeason
                            + LEADERBOARDEXTENSION_NA),
                    new TypeReference<LinkedList<GW2Account>>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new LinkedList<>();
    }

}
