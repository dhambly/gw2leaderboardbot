import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;

public class EventHandler extends ListenerAdapter {
    private APIHandler api;
    private CommandHandler commands;

    public EventHandler(APIHandler a) {
        this.api = a;
        this.commands = new CommandHandler(api);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        try {
            chooseCommand(event);
        } catch (IOException e) {
            System.out.println("Failed to connect to API");
            e.printStackTrace();
        }
    }

    private void chooseCommand(MessageReceivedEvent event) throws IOException {
        String message = event.getMessage().getContentDisplay();
        MessageChannel channel = event.getChannel();
        if (message.trim().equalsIgnoreCase("!chimpcheck")) {
            commands.chimpCheck(channel);
        } else if (message.equalsIgnoreCase("!rankedegirls")) {
            commands.listEgirlRanks(channel);
        } else if (message.equalsIgnoreCase("!top10")) {
            commands.listTop10(channel);
        } else if (message.equalsIgnoreCase("!biggestloser") || message.equalsIgnoreCase("!loser")) {
            commands.biggestLoser(channel);
        } else if (message.equalsIgnoreCase("!addict")) {
            commands.gw2Addict(channel);
        } else if (message.equalsIgnoreCase("!leaderboardcommands") || message.equalsIgnoreCase("!commands")) {
            commands.help(channel);
        }
    }
}
