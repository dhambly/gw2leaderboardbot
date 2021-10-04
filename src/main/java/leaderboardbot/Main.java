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
        db.setSeasonIDs(api.getSeasons());
        AccountContainer accountContainerNA = new AccountContainer(api, db, true);
        AccountContainer accountContainerEU = new AccountContainer(api, db, false);
        builder.addEventListeners(new EventHandler(accountContainerNA, accountContainerEU));
        builder.build();
        AutoUpdater autoUpdater = new AutoUpdater(accountContainerNA, accountContainerEU);
        autoUpdater.schedule();
        System.out.println("Currently running");
    }

}
