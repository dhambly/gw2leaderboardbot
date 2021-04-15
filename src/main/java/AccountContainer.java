import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class AccountContainer {
    private ArrayList<GW2Account> currentLeaderboard;
    private HashMap<String, GW2Account> allAccounts;
    private HashSet<String> offLeaderboardAccountNames;
    private APIHandler api;

    public AccountContainer(APIHandler api) {
        this.api = api;
        this.offLeaderboardAccountNames = new HashSet<>();
        this.currentLeaderboard = api.getLeaderboard();
        this.allAccounts = readAccountMapFile();
        HashSet<String> tempHash = new HashSet<>();
        for (GW2Account acc : currentLeaderboard) {
            if (!tempHash.contains(acc.getNameToLower())) {
                allAccounts.put(acc.getNameToLower(), acc);
                tempHash.add(acc.getNameToLower());
            }
        }
    }

    public PriorityQueue<GW2Account> getDroppedAccounts() {
        PriorityQueue<GW2Account> accountPriorityQueue =
                new PriorityQueue<>(50, (b,a) -> Integer.compare(a.getRating(), b.getRating()));
        for (String accountString : offLeaderboardAccountNames) {
            try {
                accountPriorityQueue.add(allAccounts.get(accountString));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return accountPriorityQueue;
    }

    public ArrayList<GW2Account> updateLeaderboard() {
        ArrayList<GW2Account> newLeaderboard = api.getLeaderboard();
        HashSet<String> newLeaderboardSet = new HashSet<>();
        for (GW2Account acc : newLeaderboard) {
            if (!newLeaderboardSet.contains(acc.getNameToLower())) {
                newLeaderboardSet.add(acc.getNameToLower());
                allAccounts.put(acc.getNameToLower(), acc);
                offLeaderboardAccountNames.remove(acc.getNameToLower());
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
        writeAccountMapToFile();
        return this.currentLeaderboard;
    }

    public void writeAccountMapToFile() {
        try {
            FileOutputStream fout = new FileOutputStream("accounts.gw2");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(this.allAccounts);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private HashMap readAccountMapFile() {
        ObjectInputStream objectinputstream = null;
        try {
            FileInputStream streamIn = new FileInputStream("accounts.gw2");
            objectinputstream = new ObjectInputStream(streamIn);
            HashMap<String, GW2Account> readCase = (HashMap<String, GW2Account>) objectinputstream.readObject();
            ArrayList<GW2Account> sortedList = new ArrayList<>(readCase.values());
            sortedList.sort(Comparator.comparingInt(GW2Account::getRank));
            StringBuilder sb = new StringBuilder("Loaded account list file with values: \n");
            sortedList.forEach(acc -> sb.append(acc.toString()).append("\n"));
            System.out.println(sb.toString());
            return checkForDrops(readCase);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Cannot find account list file");
        return new HashMap(1000);
    }

    public HashMap checkForDrops(HashMap<String, GW2Account> map) {
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
                if (offLeaderboardAccountNames.contains(acc.getNameToLower()))
                    offLeaderboardAccountNames.remove(acc.getNameToLower());
                if (!acc.isOnLeaderboard()) {
                    System.err.println("Theres something wrong, account off leaderboard after loading leaderboard");
                }
            }
        }

        return map;
    }

    public GW2Account getAccount(String name) {
        return allAccounts.getOrDefault(name.toLowerCase(), hardSearchAccount(name.toLowerCase()));
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

}
