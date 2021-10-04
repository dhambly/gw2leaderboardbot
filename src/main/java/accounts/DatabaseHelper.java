package accounts;

import accounts.apiobjects.GW2Account;
import accounts.apiobjects.Season;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class DatabaseHelper {
    private final Properties properties;

    public DatabaseHelper(Properties properties) {
        this.properties = properties;
    }

    public void runScheduledRatingSnapshotUpdate(GW2Account acc, Timestamp insertionTime, boolean isNA) {
        try {
            String insertOrUpdate = "INSERT INTO rating_snapshots" +
                    (isNA ? " " : "_eu ") +
                    "(id, time, rating, wins, losses) " +
                    "VALUES (?,?,?,?,?)";
            int id = acc.getAccount_id();
            if (id == 0) {
                id = getIdForAccount(acc, isNA);
                if (id == -1) {
                    System.out.println("Skipping " + acc.getName() + " due to no matching ID");
                    return;
                }
            }
            Connection connection = generateConnection();
            RatingSnapshot lastSnapshot = getLatestSnapshot(acc, connection, isNA);
//            if (!lastSnapshot.hasSameScoresAsAccount(acc)) {
            PreparedStatement preparedStatement = connection.prepareStatement(insertOrUpdate);
//                System.out.printf("Inserting %s data into Rating_Snapshot table%n", acc.getName());
            preparedStatement.setInt(1, id);
            preparedStatement.setTimestamp(2, insertionTime);
            preparedStatement.setShort(3, acc.getRating());
            preparedStatement.setShort(4, acc.getWins());
            preparedStatement.setShort(5, acc.getLosses());
            int rs = preparedStatement.executeUpdate();
            preparedStatement.close();
//            }
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setSeasonIDs(ArrayList<Season> seasons) {
        try {
            Connection connection = generateConnection();
            for (Season season : seasons) {
                String key = season.getKey();
                String query = "SELECT id from season_lookup_table where" +
                        " season_api_key = '" + key + "'";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                    season.setDatabaseId(resultSet.getInt("id"));
//                    System.out.println(String.format("Mapped %s to %d", season.getKey(), season.getDatabaseId()));
                } else {
                    String insertOrUpdate = "INSERT INTO season_lookup_table" +
                            "(season_api_key,name) " +
                            "VALUES (?,?);";
                    PreparedStatement stmt = connection.prepareStatement(insertOrUpdate, Statement.RETURN_GENERATED_KEYS);
                    System.out.println("Inserting new season into database with name " + season.getName());
                    stmt.setString(1, season.getKey());
                    stmt.setString(2, season.getName());
                    stmt.executeUpdate();
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        season.setDatabaseId(rs.getInt(1));
                    }
                    rs.close();
                    stmt.close();
                }
                resultSet.close();
                statement.close();
            }
            connection.close();

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e);
        }
    }

    private RatingSnapshot getLatestSnapshot(GW2Account acc, Connection connection, boolean isNA) {
        RatingSnapshot ratingSnapshot = new RatingSnapshot();
        int id = acc.getAccount_id();
        if (acc.getAccount_id() == 0) {
            id = getIdForAccount(acc, isNA);
        }
        if (id < 1) {
            System.out.println("Failed to find id....");
            return null;
        }
        try {
            String query = "SELECT * FROM rating_snapshots" +
                    (isNA ? " " : "_eu ") +
                    "WHERE id = " + acc.getAccount_id() + " ORDER BY time desc limit 1";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                ratingSnapshot.time = resultSet.getTimestamp("time");
                ratingSnapshot.rating = resultSet.getShort("rating");
                ratingSnapshot.wins = resultSet.getShort("wins");
                ratingSnapshot.losses = resultSet.getShort("losses");

            }
            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratingSnapshot;
    }

    public int getIdForAccount(GW2Account account, boolean isNA) {
        int id = -1;
        try {
            Connection connection = generateConnection();
            String query = "SELECT * FROM accounts" +
                    "WHERE name = '" + account.getName() + "' and eu = " +(isNA?0:1);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                id = resultSet.getInt("id");
            }
            resultSet.close();
            statement.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        account.setAccount_id(id);
        return id;
    }

    public GameHistory loadGameHistory(GW2Account acc, boolean isNA) {
        GameHistory gameHistory = new GameHistory(acc);
        int id = acc.getAccount_id();
        if (acc.getAccount_id() == 0) {
            id = getIdForAccount(acc, isNA);
        }
        if (id < 1) {
            System.out.println("Failed to find id....");
            return null;
        }
        try {
            Connection connection = generateConnection();
            String query = "SELECT * FROM rating_snapshots" +
                    (isNA ? " " : "_eu ") +
                    "WHERE id = " + acc.getAccount_id() + " ORDER BY time asc";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                Timestamp time = resultSet.getTimestamp("time");
                short rating = resultSet.getShort("rating");
                short wins = resultSet.getShort("wins");
                short losses = resultSet.getShort("losses");
                gameHistory.addGameHistory(rating, wins, losses, time);
            }
            resultSet.close();
            statement.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return gameHistory;
    }

    public HashMap<String, GW2Account> loadRawAccountMapFromDB(boolean isNA) {
        HashMap<String, GW2Account> map = new HashMap<>(1000);

        try {
            Connection connection = generateConnection();
            String query = "SELECT * FROM accounts" +
                    (isNA ? "" : "_eu");
            GW2Account account;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                account = new GW2Account();
                account.setName(resultSet.getString("name"));
                account.setRating(resultSet.getShort("rating"));
                account.setWins(resultSet.getShort("wins"));
                account.setLosses(resultSet.getShort("losses"));
                //account.setOnLeaderboard(resultSet.getBoolean("onleaderboard"));
                Date date = resultSet.getDate("date");
                account.setTime(date.getTime());
                account.setAccount_id(resultSet.getInt("id"));
//                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-")
//                SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");

                map.put(account.getNameToLower(), account);
            }
            resultSet.close();
            statement.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    public void writeAllAccountsToDB(ArrayList<GW2Account> gw2Accounts, boolean isNA) {
        try {
            String insertOrUpdate = "INSERT INTO accounts" +
                    (isNA ? " " : "_eu ") +
                    "(name,rating,wins,losses,onleaderboard,date) " +
                    "VALUES (?,?,?,?,?,?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "rating=?,wins=?,losses=?,onleaderboard=?,date=?;";
            Connection connection = generateConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertOrUpdate);
            for (GW2Account acc : gw2Accounts) {
                //System.out.printf("Inserting %s to DB%n", acc.getName());
                preparedStatement.setString(1, acc.getName());
                preparedStatement.setShort(2, acc.getRating());
                preparedStatement.setShort(3, acc.getWins());
                preparedStatement.setShort(4, acc.getLosses());
                preparedStatement.setBoolean(5, acc.isOnLeaderboard());
                Timestamp t = new Timestamp(acc.getTime());
                preparedStatement.setTimestamp(6, t);
                preparedStatement.setShort(7, acc.getRating());
                preparedStatement.setShort(8, acc.getWins());
                preparedStatement.setShort(9, acc.getLosses());
                preparedStatement.setBoolean(10, acc.isOnLeaderboard());
                preparedStatement.setTimestamp(11, t);
                int rs = preparedStatement.executeUpdate();
            }
            preparedStatement.close();
            connection.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection generateConnection() throws ClassNotFoundException, SQLException {
        String db_connection = properties.getProperty("db_connection");
        String db_user = properties.getProperty("db_user");
        String db_password = properties.getProperty("db_password");
        Class.forName(properties.getProperty("db_driver"));

        return DriverManager.getConnection(db_connection, db_user, db_password);
    }

}
