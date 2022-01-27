package accounts;

import accounts.apiobjects.GW2Account;
import accounts.apiobjects.Season;
import leaderboardbot.Leaderboard;

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
            String insertOrUpdate = "INSERT INTO new_rating_snapshots " +
                    "(account_name, time, rating, wins, losses, season, eu) " +
                    "VALUES (?,?,?,?,?,?,?)";
            Connection connection = generateConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertOrUpdate);
            preparedStatement.setString(1, acc.getName());
            preparedStatement.setTimestamp(2, insertionTime);
            preparedStatement.setShort(3, acc.getRating());
            preparedStatement.setShort(4, acc.getWins());
            preparedStatement.setShort(5, acc.getLosses());
            if (acc.getSeason() == null) {
                System.out.println("Season is null");
            }
            preparedStatement.setByte(6, acc.getSeason().getDatabaseId());
            preparedStatement.setBoolean(7, !isNA);
            preparedStatement.executeUpdate();
            preparedStatement.close();
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
                    season.setDatabaseId(resultSet.getByte("id"));
                } else {
                    String insertOrUpdate = "INSERT INTO season_lookup_table " +
                            "(season_api_key,name) " +
                            "VALUES (?,?);";
                    PreparedStatement stmt = connection.prepareStatement(insertOrUpdate, Statement.RETURN_GENERATED_KEYS);
                    System.out.println("Inserting new season into database with name " + season.getName());
                    stmt.setString(1, season.getKey());
                    stmt.setString(2, season.getName());
                    stmt.executeUpdate();
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        season.setDatabaseId(rs.getByte(1));
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
        try {
            String query = "SELECT * FROM new_rating_snapshots "
                    + "WHERE account_name = " + acc.getName()
                    + " and eu = " + (isNA ? 0 : 1)
                    + " and season = " + acc.getSeason().getDatabaseId()
                    + "ORDER BY time desc limit 1";
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

    @Deprecated
    public int getIdForAccount(GW2Account account, boolean isNA) {
        int id = -1;
        try {
            Connection connection = generateConnection();
            String query = "SELECT * FROM new_accounts " +
                    "WHERE name = '" + account.getName() + "' and eu = " + (isNA ? 0 : 1);
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
//        account.setAccount_id(id);
        return id;
    }

    public GameHistory loadGameHistory(GW2Account acc, boolean isNA) {
        GameHistory gameHistory = new GameHistory(acc);
        try {
            Connection connection = generateConnection();
            String query = "SELECT * FROM new_rating_snapshots "
                    + "WHERE account_name = '" + acc.getName()
                    + "' and eu = " + (isNA ? 0 : 1)
                    + " and season = " + acc.getSeason().getDatabaseId()
                    + " ORDER BY time asc";
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

    public HashMap<String, GW2Account> loadRawAccountMapFromDB(boolean isNA, Season curSeason) {
        HashMap<String, GW2Account> map = new HashMap<>(1000);

        try {
            Connection connection = generateConnection();
            String query = "SELECT * FROM new_accounts " +
                    "where season = " + curSeason.getDatabaseId()
                    + " and eu = " + (isNA ? 0 : 1);

            GW2Account account;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                account = new GW2Account();
                account.setName(resultSet.getString("name"));
                account.setRating(resultSet.getShort("rating"));
                account.setWins(resultSet.getShort("wins"));
                account.setLosses(resultSet.getShort("losses"));
                Date date = resultSet.getDate("date");
                account.setTime(date.getTime());
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

    public void writeAllAccountsToDB(Leaderboard leaderboard, boolean isNA) {
        ArrayList<GW2Account> gw2Accounts = leaderboard.getAccountList();
        try {
            String insertOrUpdate = "INSERT INTO new_accounts " +
                    "(name,eu,season,rating,wins,losses,date) " +
                    "VALUES (?,?,?,?,?,?,?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "rating=?,wins=?,losses=?,date=?;";
            Connection connection = generateConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertOrUpdate);
            for (GW2Account acc : gw2Accounts) {
                //System.out.printf("Inserting %s to DB%n", acc.getName());
                preparedStatement.setString(1, acc.getName());
                preparedStatement.setBoolean(2, !isNA);
                preparedStatement.setByte(3, leaderboard.getSeason().getDatabaseId());
                preparedStatement.setShort(4, acc.getRating());
                preparedStatement.setShort(5, acc.getWins());
                preparedStatement.setShort(6, acc.getLosses());
                Timestamp t = new Timestamp(acc.getTime());
                preparedStatement.setTimestamp(7, t);
                preparedStatement.setShort(8, acc.getRating());
                preparedStatement.setShort(9, acc.getWins());
                preparedStatement.setShort(10, acc.getLosses());
                preparedStatement.setTimestamp(11, t);
                int rs = preparedStatement.executeUpdate();
            }
            preparedStatement.close();
            connection.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void updateRandomThing(String key, String value) {
        try {
            String insertOrUpdate = "INSERT INTO random_stuff " +
                    "(name, thething) " +
                    "VALUES (?,?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "thething=?;";
            Connection connection = generateConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertOrUpdate);
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, value);
            preparedStatement.setString(3, value);
            int rs = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public String getRandomThing(String key) {
        String result = null;
        try {
            Connection connection = generateConnection();
            String query = "select thething from random_stuff where name = '" + key+"'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                result = resultSet.getString("thething");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void insertMultiRandomThing(String key, String value) {
        try {
            String insertOrUpdate = "INSERT INTO random_multi_things " +
                    "(id_name, thething) " +
                    "VALUES (?,?) ";
            Connection connection = generateConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertOrUpdate);
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, value);
            int rs = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getRandomMultiThing(String key) {
        ArrayList<String> things = new ArrayList<>();
        try {
            Connection connection = generateConnection();
            String query = "select thething from random_multi_things where id_name = '" + key+"' order by id desc";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                things.add(resultSet.getString("thething"));
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return things;
    }

    private Connection generateConnection() throws ClassNotFoundException, SQLException {
        String db_connection = properties.getProperty("db_connection");
        String db_user = properties.getProperty("db_user");
        String db_password = properties.getProperty("db_password");
        Class.forName(properties.getProperty("db_driver"));

        return DriverManager.getConnection(db_connection, db_user, db_password);
    }

}
