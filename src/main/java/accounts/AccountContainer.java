package accounts;

import accounts.apiobjects.GW2Account;
import accounts.apiobjects.Season;
import handlers.APIHandler;
import leaderboardbot.Leaderboard;

import java.util.*;

public class AccountContainer {
    private Leaderboard currentLeaderboard;
    private HashMap<String, GW2Account> allAccounts;
    private HashSet<String> offLeaderboardAccountNames;
    private final APIHandler api;
    private final DatabaseHelper db;
    private final boolean isNA;



    public AccountContainer(APIHandler api, DatabaseHelper databaseHelper, boolean isNA) {
        this.api = api;
        this.offLeaderboardAccountNames = new HashSet<>();
        this.isNA = isNA;
        this.currentLeaderboard = api.getLeaderboard(isNA);
        this.db = databaseHelper;
        this.allAccounts = readAccountMapFromDB();
        HashSet<String> tempHash = new HashSet<>();
        for (GW2Account acc : currentLeaderboard.getAccountList()) {
            if (!tempHash.contains(acc.getNameToLower())) {
                allAccounts.put(acc.getNameToLower(), acc);
                tempHash.add(acc.getNameToLower());
            }
        }
    }

    public DatabaseHelper getDb() {
        return db;
    }

    public PriorityQueue<GW2Account> getDroppedAccounts() {
        PriorityQueue<GW2Account> accountPriorityQueue =
                new PriorityQueue<>(50, (b, a) -> Integer.compare(a.getRating(), b.getRating()));
        for (String accountString : offLeaderboardAccountNames) {
            try {
                accountPriorityQueue.add(allAccounts.get(accountString));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return accountPriorityQueue;
    }

    public ArrayList<GW2Account> getCurrentLeaderboard() {
        return currentLeaderboard.getAccountList();
    }

    public ArrayList<GW2Account> updateLeaderboard() {
        Leaderboard newLeaderboards = api.getLeaderboard(isNA);
        ArrayList<GW2Account> newLeaderboardAccountList = api.getLeaderboard(isNA).getAccountList();
        if (!newLeaderboards.getSeason().getKey().equals(currentLeaderboard.getSeason().getKey())) {
            System.out.println("New season has been found");
            System.out.printf("Swapping to season %s from season %s%n", newLeaderboards.getSeason().getName(), currentLeaderboard.getSeason().getName());
            allAccounts = new HashMap<>();
            offLeaderboardAccountNames = new HashSet<>();
            currentLeaderboard.setAccountList(new ArrayList<>());
        }
        HashSet<String> newLeaderboardSet = new HashSet<>();
        for (GW2Account acc : newLeaderboardAccountList) {
            if (!newLeaderboardSet.contains(acc.getNameToLower())) {
                newLeaderboardSet.add(acc.getNameToLower());

                allAccounts.put(acc.getNameToLower(), acc);
                offLeaderboardAccountNames.remove(acc.getNameToLower());
            }
        }
        for (GW2Account acc : currentLeaderboard.getAccountList()) {
            if (!newLeaderboardSet.contains(acc.getNameToLower())) {
                offLeaderboardAccountNames.add(acc.getNameToLower());
                acc.setOnLeaderboard(false);
                acc.setRank(251);
            }
        }
        this.currentLeaderboard = newLeaderboards;
        this.currentLeaderboard.setAccountList(newLeaderboardAccountList);
        db.writeAllAccountsToDB(this.currentLeaderboard, isNA);
        return this.currentLeaderboard.getAccountList();
    }

    private HashMap<String, GW2Account> readAccountMapFromDB() {
        HashMap<String, GW2Account> map = db.loadRawAccountMapFromDB(isNA, currentLeaderboard.getSeason());
        //printHashMapOrderedByRank(map);
        return checkForDrops(map);
    }

    private void printHashMapOrderedByRank(HashMap<String, GW2Account> readCase) {
        ArrayList<GW2Account> sortedList = new ArrayList<>(readCase.values());
        sortedList.sort(Comparator.comparingInt(GW2Account::getRank));
        StringBuilder sb = new StringBuilder("Loaded account list from database with values: \n");
        sortedList.forEach(acc -> sb.append(acc.toString()).append("\n"));
        System.out.println(sb.toString());
    }

    public HashMap<String, GW2Account> checkForDrops(HashMap<String, GW2Account> map) {
        var leaderboardSet = new HashSet<String>();
        currentLeaderboard.getAccountList().forEach(acc -> leaderboardSet.add(acc.getNameToLower()));

        System.out.println(currentLeaderboard.getAccountList().size());
        for (GW2Account acc : map.values()) {
            if (!leaderboardSet.contains(acc.getNameToLower())) {
                //System.out.println("Found someone off leaderboard while loading " + acc.getName());
                offLeaderboardAccountNames.add(acc.getNameToLower());
                acc.setOnLeaderboard(false);
                acc.setRank(251);
            } else {
                offLeaderboardAccountNames.remove(acc.getNameToLower());
                if (!acc.isOnLeaderboard()) {
                    System.err.println("Theres something wrong, account off leaderboard after loading leaderboard");
                }
            }
        }

        return map;
    }

    public ArrayList<GW2Account> getAllMatchingAccounts(String name) {
        if (allAccounts.containsKey(name.toLowerCase())) {
            return (ArrayList<GW2Account>) Collections.singletonList(allAccounts.get(name));
        } else {
            return hardSearchMultipleAccounts(name);
        }
    }

    public GW2Account getAccount(String name) {
        return allAccounts.getOrDefault(name.toLowerCase(), hardSearchAccount(name.toLowerCase()));
    }

    public ArrayList<GW2Account> hardSearchMultipleAccounts(String name) {
        ArrayList<GW2Account> list = new ArrayList<>();
        for (String fullName : allAccounts.keySet()) {
            if (fullName.contains(name)) {
                list.add(allAccounts.get(fullName));
            }
        }
        return list;
    }

    public GW2Account hardSearchAccount(String name) {
        if (name.contains(".")) return null;
        for (String fullName : allAccounts.keySet()) {
            if (fullName.contains(name)) {
                return allAccounts.get(fullName);
            }
        }
        return null;
    }

    public HashSet<String> getOffLeaderboardAccountNames() {
        return offLeaderboardAccountNames;
    }

    public ArrayList<GW2Account> getAllAccounts() {
        return new ArrayList<>(allAccounts.values());
    }

    public int getRatingFromAPIKey(String key) {
        return api.getCurrentRatingFromAPIKey(key);
    }

    public String getNameFromAPIKey(String key) {
        return api.getAccountNameFromAPIKey(key);
    }

    public APIHandler getApi() {
        return api;
    }

    public Leaderboard getCurrentLeaderboardObject() {
        return this.currentLeaderboard;
    }

}
