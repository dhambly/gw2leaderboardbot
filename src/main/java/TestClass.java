import accounts.AccountContainer;
import accounts.DatabaseHelper;
import accounts.apiobjects.GW2Account;
import accounts.apiobjects.Season;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import handlers.APIHandler;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.String.format;

public class TestClass {

    @org.junit.jupiter.api.Test
    void main() throws IOException {
        ObjectInputStream objectinputstream = null;
        try {
            FileInputStream streamIn = new FileInputStream("accounts.gw2");
            objectinputstream = new ObjectInputStream(streamIn);
            HashMap<String, GW2Account> readCase = (HashMap<String, GW2Account>) objectinputstream.readObject();
            System.out.println(readCase.values().toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (objectinputstream != null) {
                objectinputstream.close();
            }
        }
    }

    @Test
    void outputAccountsFile() {
        ObjectInputStream objectinputstream = null;
        try {
            FileInputStream streamIn = new FileInputStream("accounts.gw2");
            objectinputstream = new ObjectInputStream(streamIn);
            HashMap<String, GW2Account> readCase = (HashMap<String, GW2Account>) objectinputstream.readObject();
            for (GW2Account acc : readCase.values()) {
                System.out.println(acc);
            }
//
//            for (String acc: readCase.keySet()) {
//                System.out.println(acc);
//            }
            System.out.printf("Map size %d%n", readCase.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void loadAccountFileAndStoreIntoDB() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("config.properties"));
        DatabaseHelper db = new DatabaseHelper(prop);
        APIHandler api = new APIHandler();
        AccountContainer accountContainer = new AccountContainer(api, db, true);
        db.writeAllAccountsToDB(accountContainer.getAllAccounts(), true);
    }

    @Test
    void loadAndPrintAccountsFromDB() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("config.properties"));
        DatabaseHelper db = new DatabaseHelper(prop);
        HashMap<String, GW2Account> map = db.loadRawAccountMapFromDB(true);
        for (GW2Account account : map.values()) {
            System.out.println(account.reformattedToString());
        }
        System.out.println("count " + map.values().size());
    }

    @Test
    void testDateTimeZoneConversion() {
        long time;
        String date = "2021-08-03T19:00:00.000Z";
        System.out.println(Calendar.getInstance().getTimeZone().getOffset(System.currentTimeMillis()));
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
        try {
            Date t = ft.parse(date);
            t.setTime(t.getTime() + TimeZone.getTimeZone("America/New_York").getOffset(t.getTime()));
            time = t.getTime();
            System.out.println(new Date(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Test
    void initializeSeasons() throws IOException {
        final String GW2_APIURL = "https://api.guildwars2.com/v2";
        final String GW2API_SEASONURL = "https://api.guildwars2.com/v2/pvp/seasons";
        final String LEADERBOARDEXTENSION_NA = "/leaderboards/ladder/na";
        final String LEADERBOARDEXTENSION_EU = "/leaderboards/ladder/eu";
        LinkedList seasonIDs;
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        seasonIDs = mapper.readValue(
                new URL(GW2API_SEASONURL),
                LinkedList.class);
        ArrayList<Season> seasons = new ArrayList<>();
        for (var seasonID : seasonIDs) {
            String IDstr = seasonID.toString();
            Season newSeason = mapper.readValue(
                    new URL(GW2API_SEASONURL
                            + "/"
                            + IDstr), Season.class);
            seasons.add(newSeason);
        }

        for (Season s : seasons) {
            System.out.println(String.format("%s from %s to %s with key %s", s.getName(), new Date(s.getStart()), new Date(s.getEnd()), s.getKey()));
        }
//        latestSeason = seasonIDs.getLast();

       System.out.println("Season ID:" + seasons.get(seasons.size()-1));
    }

    @Test
    void createSeasons() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("config.properties"));
        DatabaseHelper db = new DatabaseHelper(prop);
        APIHandler api = new APIHandler();
        db.setSeasonIDs(api.getSeasons());
    }
}
