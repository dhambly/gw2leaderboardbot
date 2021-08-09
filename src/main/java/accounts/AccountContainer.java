package accounts;

import accounts.apiobjects.GW2Account;
import handlers.APIHandler;

import java.util.*;

public class AccountContainer {
    private ArrayList<GW2Account> currentLeaderboard;
    private final HashMap<String, GW2Account> allAccounts;
    private final HashSet<String> offLeaderboardAccountNames;
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
        for (GW2Account acc : currentLeaderboard) {
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
        return currentLeaderboard;
    }

    public ArrayList<GW2Account> updateLeaderboard() {
        ArrayList<GW2Account> newLeaderboard = api.getLeaderboard(isNA);
        HashSet<String> newLeaderboardSet = new HashSet<>();
        for (GW2Account acc : newLeaderboard) {
            if (!newLeaderboardSet.contains(acc.getNameToLower())) {
                newLeaderboardSet.add(acc.getNameToLower());
                if (allAccounts.containsKey(acc.getNameToLower())) {
                    acc.setAccount_id(allAccounts.get(acc.getNameToLower()).getAccount_id());
                }
                allAccounts.put(acc.getNameToLower(), acc);
                offLeaderboardAccountNames.remove(acc.getNameToLower());
            } else {
                acc.setAccount_id(allAccounts.get(acc.getNameToLower()).getAccount_id());
            }
        }
        for (GW2Account acc : currentLeaderboard) {
            if (!newLeaderboardSet.contains(acc.getNameToLower())) {
                offLeaderboardAccountNames.add(acc.getNameToLower());
                acc.setOnLeaderboard(false);
                acc.setRank(251);
            }
        }
        this.currentLeaderboard = newLeaderboard;
        System.out.println("attempting write to db....");
        db.writeAllAccountsToDB(this.currentLeaderboard, isNA);
        System.out.println("finished write to db");
        return this.currentLeaderboard;
    }

    private HashMap<String, GW2Account> readAccountMapFromDB() {
        HashMap<String, GW2Account> map = db.loadRawAccountMapFromDB(isNA);
        printHashMapOrderedByRank(map);
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
        currentLeaderboard.forEach(acc -> leaderboardSet.add(acc.getNameToLower()));

        System.out.println(currentLeaderboard.size());
        for (GW2Account acc : map.values()) {
            if (!leaderboardSet.contains(acc.getNameToLower())) {
                System.out.println("Found someone off leaderboard while loading " + acc.getName());
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
}
