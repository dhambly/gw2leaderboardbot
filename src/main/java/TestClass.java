import accounts.AccountContainer;
import accounts.DatabaseHelper;
import accounts.GameHistory;
import accounts.apiobjects.GW2Account;
import accounts.apiobjects.Season;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import handlers.APIHandler;
import leaderboardbot.Leaderboard;
import net.dv8tion.jda.api.JDABuilder;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

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
    void loadAndPrintAccountsFromDB() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("config.properties"));
        DatabaseHelper db = new DatabaseHelper(prop);
        APIHandler apiHandler = new APIHandler();
        HashMap<String, GW2Account> map = db.loadRawAccountMapFromDB(true, apiHandler.getLatestSeason());
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

        System.out.println("Season ID:" + seasons.get(seasons.size() - 1));
    }

    @Test
    void createSeasons() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("config.properties"));
        DatabaseHelper db = new DatabaseHelper(prop);
        APIHandler api = new APIHandler();
        db.setSeasonIDs(api.getSeasons());
    }

    @Test
    void insertLeaderboard() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("config.properties"));
        DatabaseHelper db = new DatabaseHelper(prop);
        APIHandler api = new APIHandler();
        db.setSeasonIDs(api.getSeasons());
        AccountContainer accountContainerNA = new AccountContainer(api, db, true);
        db.writeAllAccountsToDB(accountContainerNA.getCurrentLeaderboardObject(), true);
    }

    @Test
    void InsertRandomThing() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("config.properties"));
        DatabaseHelper db = new DatabaseHelper(prop);
        db.insertMultiRandomThing("deeznuts", "sdjfioasdjf");
        db.insertMultiRandomThing("deeznuts", "sdjfisdfasdfoasdjf");
    }

    @Test
    void getRandomMultiThing() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("config.properties"));
        DatabaseHelper db = new DatabaseHelper(prop);
        System.out.println(db.getRandomMultiThing("zoosenuts"));
    }

    @Test
    void testGameHistory() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("config.properties"));
        DatabaseHelper db = new DatabaseHelper(prop);
        GW2Account acc = new GW2Account();
        acc.setName("NotoriousNaru.6241");
        Season season = new Season();
        season.setDatabaseId((byte) 37);
        acc.setSeason(season);
        GameHistory gh = db.loadGameHistory(acc, true);
        gh.getRatingHistory().forEach(e-> System.out.println(e.rating));
    }

    @Test
    void loadGameHistoryFromDB() throws IOException, ClassNotFoundException, SQLException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("config.properties"));
        DatabaseHelper db = new DatabaseHelper(properties);
        String db_connection = properties.getProperty("db_connection");
        String db_user = properties.getProperty("db_user");
        String db_password = properties.getProperty("db_password");
        Class.forName(properties.getProperty("db_driver"));
        GW2Account acc = new GW2Account();
        acc.setName("NotoriousNaru.6241");
        Season season = new Season();
        season.setDatabaseId((byte) 37);
        acc.setSeason(season);
        Connection connection =  DriverManager.getConnection(db_connection, db_user, db_password);
        GameHistory gameHistory = new GameHistory(acc);
        try {
//            String query = "SELECT * FROM new_rating_snapshots "
//                    + "WHERE account_name = '" + acc.getName()
//                    + "' and eu = " + (0)
//                    + " and season = " + acc.getSeason().getDatabaseId()
//                    + " ORDER BY time asc";
            String query = "SELECT * FROM new_rating_snapshots \n" +
                    "                    WHERE account_name = 'NotoriousNaru.6241'\n" +
                    "                    and eu = 0\n" +
                    "                    and season = 37\n" +
                    "                    ORDER BY time asc";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            System.out.println(resultSet);
            while (resultSet.next()) {
                Timestamp time = resultSet.getTimestamp("time");
                short rating = resultSet.getShort("rating");
                short wins = resultSet.getShort("wins");
                short losses = resultSet.getShort("losses");
                gameHistory.addGameHistory(rating, wins, losses, time);
                System.out.println(time);
            }
            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        gameHistory.getRatingHistory().forEach(e -> System.out.println(e.time));
    }
}
