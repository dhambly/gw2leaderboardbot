import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

public class CommandHandler {
    private APIHandler api;

    CommandHandler(APIHandler api) {
        this.api = api;
    }

    public void chimpCheck(MessageReceivedEvent event) {
        System.out.println("Called chimpcheck");
        try {
            var channel = event.getChannel();
            StringBuilder sb = new StringBuilder();
            int counter = 0;
            for (GW2Account acc : api.getLeaderboard()) {
                System.out.println(acc.getName());
                if (FuckingHelios.isAChimp(acc.getName())) {
                    System.out.println("Is a chimp");
                    counter++;
                    sb.append(acc.toString()).append("\n");
                }
            }
            if (counter > 0) {
                String formattedMessage = (String.format("%d Chimps Found: %s", counter, sb.toString()));
                System.out.println(formattedMessage);
                channel.sendMessage(formattedMessage).queue();
            } else {
                System.out.println("No Chimps Found");
            }
        } catch (IOException e) {
            System.out.println("Cannot connect to API");
            System.err.println(e.toString());
        }
    }
}
