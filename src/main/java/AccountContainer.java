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
        this.currentLeaderboard = api.getLeaderboard();
        this.allAccounts = readAccountMapFile();
        this.offLeaderboardAccountNames = new HashSet<>();
        for (GW2Account acc : currentLeaderboard) {
            allAccounts.put(acc.getName().toLowerCase(), acc);
        }
    }

    public ArrayList<GW2Account> updateLeaderboard() {
        ArrayList<GW2Account> newLeaderboard = api.getLeaderboard();
        HashSet<String> tempNewAccountHash = new HashSet<>();
        for (GW2Account acc : newLeaderboard) {
            tempNewAccountHash.add(acc.getName());
            allAccounts.put(acc.getName().toLowerCase(), acc);
            offLeaderboardAccountNames.remove(acc.getName());
        }
        for (GW2Account acc : currentLeaderboard) {
                //If the new leaderboard has this account from the old leaderboard
            if (tempNewAccountHash.contains(acc.getName())) {
                //allAccounts.replace(acc.getName(), acc);
            } else {
                /*
                    If the new leaderboard does not have this account from the old leaderboard
                    AKA, this account has been knocked out
                */
                offLeaderboardAccountNames.add(acc.getName());
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
            StringBuilder sb = new StringBuilder("Loaded account list file with values: \n");
            readCase.values().forEach(acc -> sb.append(acc.toString()).append("\n"));
            System.out.println(sb.toString());
            return readCase;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Cannot find account list file");
        return new HashMap(300);
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
        return getOffLeaderboardAccountNames();
    }

    public ArrayList<GW2Account> getAllAccounts() {
        return (ArrayList<GW2Account>) allAccounts.values();
    }

}
