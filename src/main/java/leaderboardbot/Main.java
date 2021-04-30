package leaderboardbot;

import accounts.AccountContainer;
import accounts.DatabaseHelper;
import handlers.APIHandler;
import handlers.EventHandler;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import javax.xml.crypto.Data;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws LoginException, IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("config.properties"));
        DatabaseHelper db = new DatabaseHelper(prop);
        String token = prop.getProperty("token");
        JDABuilder builder = JDABuilder.createDefault(token);
        APIHandler api = new APIHandler();
        AccountContainer accountContainer = new AccountContainer(api, db);
        builder.addEventListeners(new EventHandler(api, accountContainer));
        builder.build();
        AutoUpdater autoUpdater = new AutoUpdater(accountContainer);
        autoUpdater.schedule();
        System.out.println("Currently running");
    }

}
