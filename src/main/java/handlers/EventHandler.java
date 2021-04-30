package handlers;

import accounts.AccountContainer;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;

public class EventHandler extends ListenerAdapter {
    private APIHandler api;
    private CommandHandler commands;

    public EventHandler(APIHandler a, AccountContainer accountContainer) {
        this.api = a;
        this.commands = new CommandHandler(accountContainer);
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
        } else if (message.equalsIgnoreCase("!topaddicts")) {
            commands.topAddicts(channel);
        } else if (message.equalsIgnoreCase("!rankedegirls")) {
            commands.listEgirlRanks(channel);
        } else if (message.equalsIgnoreCase("!top10")) {
            commands.listTop10(channel);
        } else if (message.equalsIgnoreCase("!biggestloser") || message.equalsIgnoreCase("!loser")) {
            commands.biggestLoser(channel);
        } else if (message.equalsIgnoreCase("!addict")) {
            commands.gw2Addict(channel);
        } else if (message.equalsIgnoreCase("!leaderboardcommands") || message.equalsIgnoreCase("!commands")
                || message.equalsIgnoreCase("!help")) {
            commands.help(channel);
        } else if (message.equalsIgnoreCase("!kaypud")) {
            commands.kaypud(channel);
        } else if (message.equalsIgnoreCase("!moobs")) {
            commands.checkmoobs(channel);
        } else if (message.equalsIgnoreCase("!ari ebois")) {
            commands.ariEbois(channel);
        } else if (message.toLowerCase().startsWith("!top")) {
            commands.topX(channel, message);
        } else if (message.toLowerCase().startsWith("!lookup")) {
            commands.getName(channel, message.split(" ", 2)[1]);
        } else if (message.equalsIgnoreCase("!fallen")) {
            commands.lostAccounts(channel);
        } else if (message.toLowerCase().startsWith("!shitter")) {
            commands.winrateShitter(channel, message);
        } else if (message.equalsIgnoreCase("!loraharem")) {
            commands.loraHarem(channel);
        } else if (message.equalsIgnoreCase("calebswag") || message.equalsIgnoreCase("!calebswag")) {
            commands.calebSwag(channel);
        } else if (message.startsWith("!history")) {
            commands.historyLookup(channel, message.split(" ", 2)[1]);
        } else if (message.equalsIgnoreCase("!forcehistoryupdate")) {
            commands.forceHistoryUpdate();
        }
     }
}
