package handlers;

import accounts.AccountContainer;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;

public class EventHandler extends ListenerAdapter {
    private final AccountContainer na_container;
    private final AccountContainer eu_container;

    public EventHandler(AccountContainer na, AccountContainer eu) {
        this.na_container = na;
        this.eu_container = eu;
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
        try {
            String message = event.getMessage().getContentDisplay();
            message = message.trim();
            message = message.toLowerCase();
            AccountContainer container = na_container;
            boolean isNA = true;
            if (message.startsWith("!eu")) {
                message = "!" + message.substring(3);
                container = eu_container;
                isNA = false;
            }
            CommandHandler commands = new CommandHandler(container, event, isNA);
            if (message.trim().equalsIgnoreCase("!chimpcheck")) {
                commands.chimpCheck();
            } else if (message.equalsIgnoreCase("!topaddicts")) {
                commands.topAddicts();
            } else if (message.equalsIgnoreCase("!rankedegirls")) {
                commands.listEgirlRanks();
            } else if (message.equalsIgnoreCase("!top10")) {
                commands.listTop10();
            } else if (message.equalsIgnoreCase("!biggestloser") || message.equalsIgnoreCase("!loser")) {
                commands.biggestLoser();
            } else if (message.equalsIgnoreCase("!addict")) {
                commands.gw2Addict();
            } else if (message.equalsIgnoreCase("!leaderboardcommands") || message.equalsIgnoreCase("!commands")
                    || message.equalsIgnoreCase("!help")) {
                commands.help();
            } else if (message.equalsIgnoreCase("!kaypud")) {
                commands.kaypud();
            } else if (message.equalsIgnoreCase("!moobs")) {
                commands.checkmoobs();
            } else if (message.equalsIgnoreCase("!ari ebois")) {
                commands.ariEbois();
            } else if (message.toLowerCase().startsWith("!top")) {
                commands.topX(message);
            } else if (message.toLowerCase().startsWith("!lookup")) {
                commands.getName(message.split(" ", 2)[1]);
            } else if (message.equalsIgnoreCase("!fallen")) {
                commands.lostAccounts();
            } else if (message.toLowerCase().startsWith("!shitter")) {
                commands.winrateShitter(message);
            } else if (message.equalsIgnoreCase("!loraharem")) {
                commands.loraHarem();
            } else if (message.equalsIgnoreCase("calebswag") || message.equalsIgnoreCase("!calebswag")) {
                commands.calebSwag();
            } else if (message.startsWith("!historygraph") || message.startsWith("!graph")) {
                commands.graphHistory(message.split(" ", 2)[1]);
            } else if (message.startsWith("!history")) {
                commands.historyLookup(message.split(" ", 2)[1]);
            } else if (message.equalsIgnoreCase("!forcehistoryupdate")) {
                commands.forceHistoryUpdate();
            } else if (message.toLowerCase().startsWith("!getrating")) {
                commands.getRatingFromAPIKey(message.split(" ", 2)[1]);
            } else if (message.equalsIgnoreCase("!forceupdate")) {
                commands.forceUpdate();
            } else if (message.toLowerCase().startsWith("!rank")) {
                commands.getFromRank(message);
            } else if (message.equalsIgnoreCase("!graphtest")) {
                commands.graphTest();
            } else if (message.startsWith("!hourlygraph") || message.startsWith("!dailyhistory") || message.startsWith("!recenthistory")) {
                commands.graphHourlyHistory(message.split(" ", 2)[1]);
            } else if (message.startsWith("!expose")) {
                commands.addExposeTracker(event, message.split(" ", 2)[1]);
            } else if (message.equalsIgnoreCase("!buttbuddies")) {
                commands.findButtBuddies();
            } else if (message.equalsIgnoreCase("!patchnotes")) {
                commands.patchNotes();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
