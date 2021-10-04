package handlers;

import accounts.AccountContainer;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Random;

public class EventHandler extends ListenerAdapter {
    private final AccountContainer na_container;
    private final AccountContainer eu_container;

    public EventHandler(AccountContainer na, AccountContainer eu) {
        this.na_container = na;
        this.eu_container = eu;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        try {
            chooseCommand(event);
        } catch (IOException e) {
            System.out.println("Failed to connect to API");
            e.printStackTrace();
        }
    }

    public boolean isSteel(MessageReceivedEvent event) {
        return event.getMessage().getAuthor().getName().toLowerCase().contains("steel");
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
            if (event.getMessage().getAuthor().getName().toLowerCase().contains("zoose")
                    && (message.contains("balls") || message.contains("nuts")) && (!message.startsWith("!zoose"))) {
                container.getDb().insertMultiRandomThing("zoosenuts", event.getMessage().getContentDisplay());
            }
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
            } else if (message.startsWith("!steel")
                    || (isSteel(event) && (message.contains("copium") || message.contains("cope") ||
                    message.contains("xpose")
                    || (new Random().nextDouble()) < .01))) { //1% chance of just doing it anyway lmfao
                commands.steel();
            } else if (message.equalsIgnoreCase("!fishing")) {
                commands.fishing();
            } else if (message.startsWith("!naru") || message.startsWith("!nubu")) {
                int num;
                try {
                    num = Integer.parseInt(message.substring(5).trim());
                } catch (Exception e) {
                    num = -1;
                }
                commands.naru(num);
            } else if (message.startsWith("!scrims") || message.startsWith("!inhouses")) {
                commands.scrims();
            } else if (message.startsWith("!kat")) {
                commands.kat();
            } else if (message.startsWith("!zeromis")) {
                commands.zeromis();
            } else if (message.startsWith("!shorts")) {
                commands.shorts();
            } else if (message.startsWith("!toker")) {
                commands.toker();
            } else if (message.startsWith("!nos")) {
                commands.nos();
            } else if (message.startsWith("!helio")) {
                commands.helio();
            } else if (message.startsWith("!mark")) {
                commands.mark();
            } else if (message.startsWith("!grim")) {
                commands.grim();
            } else if (message.startsWith("!nemu")) {
                commands.nemu();
            } else if (message.equalsIgnoreCase("!clearcomms")) {
                commands.clearComms();
            } else if (message.equalsIgnoreCase("!zoosenutsjar") || message.equalsIgnoreCase("!zooseballsjar")) {
                commands.zooseNutsJar();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
