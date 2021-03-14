import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws LoginException, IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("config.properties"));
        String token = prop.getProperty("token");
        JDABuilder builder = JDABuilder.createDefault(token);
        APIHandler api = new APIHandler();
        builder.addEventListeners(new EventHandler(api));
        builder.build();
        System.out.println("Currently running");
    }

}
