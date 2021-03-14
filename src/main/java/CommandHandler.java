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
                //System.out.println(acc.getName());
                if (AccountListHandler.isAChimp(acc.getName())) {
                    System.out.println("Is a chimp");
                    counter++;
                    sb.append(acc.toString()).append("\n");
                }
            }
            if (counter > 0) {
                String formattedMessage = (String.format("%d Chimps Found: \n%s", counter, sb.toString()));
                System.out.println(formattedMessage);
                channel.sendMessage(formattedMessage).queue();
            } else {
                System.out.println("No Chimps Found");
                channel.sendMessage("No chimps found!!!\ndid helio quit gw2....?").queue();
            }
        } catch (IOException e) {
            System.out.println("Cannot connect to API");
            System.err.println(e.toString());
        }
    }

    public void listEgirlRanks(MessageReceivedEvent event) {
        System.out.println("Called Egirl check");
        try {
            var channel = event.getChannel();
            StringBuilder sb = new StringBuilder();
            int counter = 0;
            for (GW2Account acc : api.getLeaderboard()) {
                if (AccountListHandler.isAnEgirl(acc.getName())) {
                    counter++;
                    sb.append(acc.toString()).append("\n");
                }
            }
            if (counter > 0) {
                String formattedMessage = (String.format("%d Egirls currently in the top 50: \n%s", counter, sb.toString()));
                System.out.println(formattedMessage);
                channel.sendMessage(formattedMessage).queue();
            } else {
                System.out.println("No egirls found :(\nstep it up simps");
                channel.sendMessage("No egirls found :(\nstep it up simps").queue();
            }
        } catch (IOException e) {
            System.out.println("Cannot connect to API");
            System.err.println(e.toString());
        }
    }
}
