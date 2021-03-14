import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws LoginException, IOException {
        String token = "ODIwNDg1OTA5MTQ4NTk4Mjgz.YE13DA.GWbE84fFLO0vjlkzzjFEzrG3qqQ";
        JDABuilder builder = JDABuilder.createDefault(token);
        APIHandler api = new APIHandler();
        builder.addEventListeners(new EventHandler(api));
        builder.build();
    }

}
