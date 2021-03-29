import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import static java.lang.String.*;

public class CommandHandler {
    private APIHandler api;
    private AccountContainer accountContainer;

    CommandHandler(APIHandler api, AccountContainer accountContainer) {
        this.api = api;
        this.accountContainer = accountContainer;
    }

    public void sendMessage(MessageChannel channel, String msg) {
        System.out.println(msg);
        channel.sendMessage(msg).queue();
    }

    public void help(MessageChannel channel) {
        StringBuilder sb = new StringBuilder();
        sb.append("Current commands:\n").
                append("!chimpcheck\n")
                .append("!rankedegirls\n")
                .append("!biggestloser\n")
                .append("!addict\n")
                .append("!top[X] or !top[X]-[Y]\n")
                .append("!lookup [account]");
        sendMessage(channel, sb.toString());
    }

    public void chimpCheck(MessageChannel channel) {
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (GW2Account acc : accountContainer.updateLeaderboard()) {
            if (acc.isAChimp()) {
                counter++;
                sb.append(acc.reformattedToString()).append("\n");
            }
        }
        if (counter > 0) {
            String formattedMessage = (format("%d Chimps Found: \n%s", counter, sb.toString()));
            sendMessage(channel, formattedMessage);
        } else {
            sendMessage(channel, "No chimps found!!!\ndid helio quit gw2....?");
        }
    }

    public void ariEbois(MessageChannel channel) {
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (GW2Account acc : accountContainer.updateLeaderboard()) {
            if (acc.isA("ebois")) {
                counter++;
                sb.append(acc.reformattedToString()).append("\n");
            }
        }
        if (counter > 0) {
            String formattedMessage = (format("%d of ari's ebois found: \n%s", counter, sb.toString()));
            sendMessage(channel, formattedMessage);
        } else {
            sendMessage(channel, "No ebois found... Time to look for some more ari");
        }
    }

    public void listEgirlRanks(MessageChannel channel) {
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (GW2Account acc : accountContainer.updateLeaderboard()) {
            if (acc.isAnEgirl()) {
                counter++;
                sb.append(acc.reformattedToString()).append("\n");
            }
        }
        if (counter > 0) {
            String formattedMessage = (format("%d egirls in the top 50: \n%s", counter, sb.toString()));
            System.out.println(formattedMessage);
            channel.sendMessage(formattedMessage).queue();
        } else {
            System.out.println("No egirls found :(\nstep it up simps");
            channel.sendMessage("No egirls found :(\nstep it up simps").queue();
        }

    }

    public void listTop10(MessageChannel channel) {
        StringBuilder sb = new StringBuilder();
        for (GW2Account acc : accountContainer.updateLeaderboard().subList(0, 10)) {
            sb.append(acc.reformattedToString()).append("\n");
        }
        String formattedMessage = (format("Current Top 10:\n%s", sb.toString()));
        sendMessage(channel, formattedMessage);

    }

    public void biggestLoser(MessageChannel channel) {
        ArrayList<GW2Account> list = accountContainer.updateLeaderboard();
        GW2Account loser = list.get(0);
        for (GW2Account acc : list.subList(1, list.size())) {
            if (acc.getLosses() > loser.getLosses()) {
                loser = acc;
            }
        }
        String message = format("The biggest loser is %s with %d losses lmfao", loser.getName(), loser.getLosses());
        sendMessage(channel, message);
    }

    public void gw2Addict(MessageChannel channel) {
        ArrayList<GW2Account> list = accountContainer.updateLeaderboard();
        GW2Account addict = list.get(0);
        int addictTotal = addict.getLosses() + addict.getWins();
        for (GW2Account acc : list.subList(1, list.size())) {
            int total = acc.getLosses() + acc.getWins();
            if (addictTotal < total) {
                addict = acc;
                addictTotal = total;
            }
        }
        String message = format("GW2's worst no lifer is %s with a whopping %d total games", addict.getName(), addictTotal);
        sendMessage(channel, message);
    }

    public void kaypud(MessageChannel channel) {
        GW2Account kaypud = null;
        for (GW2Account acc : accountContainer.updateLeaderboard()) {
            if (acc.getName().toLowerCase().contains("kaypud")) {
                kaypud = acc;
                break;
            }
        }
        String message;
        if (kaypud != null) message = format("he's rank %d", kaypud.getRank());
        else message = "cant find him";
        sendMessage(channel, message);
    }

    public void checkmoobs(MessageChannel channel) {
        GW2Account moobs = null;
        sendMessage(channel, "i promise you he's not on there but ill check");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println(e.toString());
        }
        for (GW2Account acc : accountContainer.updateLeaderboard()) {
            if (acc.getName().toLowerCase().contains("davinci")) {
                moobs = acc;
                break;
            }
        }
        String message;
        if (moobs != null) message = format("lmfao exposed he's rank %d", moobs.getRank());
        else message = "yeah he's not there";
        sendMessage(channel, message);
    }

    public void getName(MessageChannel channel, String name) {
        GW2Account lookup = accountContainer.getAccount(name.toLowerCase());
        if (lookup != null) {
            String message;
            if (lookup.isOnLeaderboard()) {
                message = lookup.reformattedToString();
            } else {
                message = format("%s was last seen with rating *%d* (%d-%d) on %s",
                        lookup.getName(), lookup.getRating(), lookup.getWins(), lookup.getLosses(), lookup.getFormattedDate());
            }
            sendMessage(channel, message);
        } else {
            sendMessage(channel, "Cannot find account");
        }

    }

    public void topX(MessageChannel channel, String command) {
        int start = 0;
        int end = 250;

        try {
            String numbers = command.replace("!top", "").trim();
            if (numbers.contains("-")) {
                String[] split = numbers.split("-");
                start = Integer.parseInt(split[0]) - 1;
                end = Integer.parseInt(split[1]);
                if (end > 250) end = 250;
                if (start < 0) start = 0;
                if (start > 250) start = 250;
                if (end < 1) end = 1;
                if (end < start) return;
            } else {
                end = Integer.parseInt(numbers);
                if (end <= start) end = 1;
                if (end > 250) end = 250;
            }

            StringBuilder sb = new StringBuilder();
            for (GW2Account acc : accountContainer.updateLeaderboard().subList(start, end)) {
                sb.append(acc.reformattedToString()).append("\n");
            }
            String intro = start == 0 ? format("Current Top %d:\n", end) : format("Current %d-%d:\n", start + 1, end);
            String formattedMessage = intro + sb.toString();
            sendMessage(channel, formattedMessage);
        } catch (Exception e) {
            System.err.println(e.toString());
            sendMessage(channel, "message too long, try 30 or less total accounts");
            return;
        }
    }

}
