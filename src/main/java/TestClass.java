import accounts.AccountContainer;
import accounts.DatabaseHelper;
import accounts.GW2Account;
import handlers.APIHandler;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
        AccountContainer accountContainer = new AccountContainer(api, db);
        db.writeAllAccountsToDB(accountContainer.getAllAccounts());
    }

    @Test
    void loadAndPrintAccountsFromDB() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("config.properties"));
        DatabaseHelper db = new DatabaseHelper(prop);
        HashMap<String, GW2Account> map = db.loadRawAccountMapFromDB();
        for (GW2Account account : map.values()) {
            System.out.println(account.reformattedToString());
        }
        System.out.println("count " + map.values().size());
    }
}
