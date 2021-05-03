package handlers;

import accounts.AccountContainer;
import accounts.GW2Account;
import accounts.GameHistory;
import accounts.RatingSnapshot;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.lang.String.*;

public class CommandHandler {
    private final AccountContainer accountContainer;

    CommandHandler(AccountContainer accountContainer) {
        this.accountContainer = accountContainer;
    }

    public void sendMessage(MessageChannel channel, String msg) {
        if (msg.length() >= 2000) {
            String end = "\nMsg too long...";
            msg = msg.substring(0, 2000 - (end.length() + 1));
            msg += end;
        }
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
                .append("!lookup [account]\n")
                .append("!fallen\n")
                .append("!shitter or !shitter [min games]");
        sendMessage(channel, sb.toString());
    }

    public void chimpCheck(MessageChannel channel) {
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (GW2Account acc : accountContainer.getCurrentLeaderboard()) {
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
        for (GW2Account acc : accountContainer.getCurrentLeaderboard()) {
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

    public void loraHarem(MessageChannel channel) {
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (GW2Account acc : accountContainer.getCurrentLeaderboard()) {
            if (acc.isA("theharem")) {
                counter++;
                sb.append(acc.reformattedToString()).append("\n");
            }
        }
        if (counter > 0) {
            String formattedMessage = (format("%d of lora's harem members found: \n%s", counter, sb.toString()));
            sendMessage(channel, formattedMessage);
        } else {
            sendMessage(channel, "no one found... expand your harem lora");
        }
    }

    public void listEgirlRanks(MessageChannel channel) {
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (GW2Account acc : accountContainer.getCurrentLeaderboard()) {
            if (acc.isAnEgirl()) {
                counter++;
                sb.append(acc.reformattedToString()).append("\n");
            }
        }
        if (counter > 0) {
            String formattedMessage = (format("%d egirls in the top 250: \n%s", counter, sb.toString()));
            System.out.println(formattedMessage);
            channel.sendMessage(formattedMessage).queue();
        } else {
            System.out.println("No egirls found :(\nstep it up simps");
            channel.sendMessage("No egirls found :(\nstep it up simps").queue();
        }

    }

    public void listTop10(MessageChannel channel) {
        StringBuilder sb = new StringBuilder();
        for (GW2Account acc : accountContainer.getCurrentLeaderboard().subList(0, 10)) {
            sb.append(acc.reformattedToString()).append("\n");
        }
        String formattedMessage = (format("Current Top 10:\n%s", sb.toString()));
        sendMessage(channel, formattedMessage);

    }

    public void winrateShitter(MessageChannel channel, String wholemsg) {
        String[] splitMsg = wholemsg.split(" ");
        int minGames = 0;
        if (splitMsg.length > 1) {
            try {
                minGames = Integer.parseInt(splitMsg[1]);
            } catch (NumberFormatException e) {
                System.err.println(e.toString());
                sendMessage(channel, "put in numbers you fucking faggot");
            }
        }
        ArrayList<GW2Account> list = accountContainer.getAllAccounts();

        //find the first account that fits
        GW2Account loser = list.get(0);
        int worstTotal = loser.getWins() + loser.getLosses();
        double worstWinRate = (double) loser.getWins() / worstTotal;
        int counter = 1;
        while (worstTotal < minGames) {
            loser = list.get(counter++);
            worstTotal = loser.getWins() + loser.getLosses();
            worstWinRate = (double) loser.getWins() / worstTotal;

            if (counter == list.size()) {
                sendMessage(channel, "Nobody has that many games. Just wait a sec for Arrant Stark.");
                return;
            }
        }

        //find the best account that fits
        for (GW2Account acc : list.subList(counter, list.size())) {
            int totalGames = acc.getWins() + acc.getLosses();
            double winRate = (double) acc.getWins() / totalGames;
            if (winRate < worstWinRate && totalGames > minGames) {
                loser = acc;
                worstWinRate = winRate;
            }
        }
        String message = format("the most garbage player%s has to be %s with a win rate of %.3f over %d games (%d-%d)",
                minGames > 0 ? " with " + minGames + " games" : "",
                loser.getName(), worstWinRate, loser.getWins() + loser.getLosses(), loser.getWins(), loser.getLosses());
        sendMessage(channel, message);
    }

    public void biggestLoser(MessageChannel channel) {
        ArrayList<GW2Account> list = accountContainer.getAllAccounts();
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
        ArrayList<GW2Account> list = accountContainer.getAllAccounts();
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

    public void topAddicts(MessageChannel channel) {
        ArrayList<GW2Account> list = accountContainer.getAllAccounts();
        PriorityQueue<GW2Account> priorityQueue = new PriorityQueue<>((b, a)
                -> Integer.compare(a.getWins() + a.getLosses(), b.getWins() + b.getLosses()));
        priorityQueue.addAll(list);
        StringBuilder sb = new StringBuilder("GW2's worst no lifers are:\n");
        int initialSize = priorityQueue.size();
        for (int i = 0; i < 10 && i < initialSize;i++) {
            GW2Account acc = priorityQueue.poll();
            sb.append(acc.getName()).append(" with ").append(acc.getWins() + acc.getLosses()).append(" total games\n");
        }
        sendMessage(channel, sb.toString());
    }

    public void kaypud(MessageChannel channel) {
        GW2Account kaypud = null;
        for (GW2Account acc : accountContainer.getCurrentLeaderboard()) {
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
        for (GW2Account acc : accountContainer.getCurrentLeaderboard()) {
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
            for (GW2Account acc : accountContainer.getCurrentLeaderboard().subList(start, end)) {
                sb.append(acc.reformattedToString()).append("\n");
            }
            String intro = start == 0 ? format("Current Top %d:\n", end) : format("Current %d-%d:\n", start + 1, end);
            String formattedMessage = intro + sb.toString();
            sendMessage(channel, formattedMessage);
        } catch (NumberFormatException e) {
            System.err.println(e.toString());
            sendMessage(channel, "put in numbers you fucking faggot");
        }
    }

    public void lostAccounts(MessageChannel channel) {
        System.out.println("Trying to find lost accounts");
        StringBuilder sb = new StringBuilder();
        PriorityQueue<GW2Account> droppedAccounts = accountContainer.getDroppedAccounts();
        int total = droppedAccounts.size();
        sb.append("Here are the top currently tracked accounts off the leaderboard:\n");
        for (int i = 0; i < 25; i++) {
            GW2Account acc = droppedAccounts.poll();
            String message = format("%s was last seen with rating *%d* (%d-%d) on %s",
                    acc.getName(), acc.getRating(), acc.getWins(), acc.getLosses(), acc.getFormattedDate());
            if (sb.length() + message.length() < 1980)
                sb.append(message).append("\n");
            else
                break;
        }
        sb.append("Total: ").append(total);
        try {
            sendMessage(channel, sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void calebSwag(MessageChannel channel) {
        sendMessage(channel, "is a faggot");
    }

    public void historyLookup(MessageChannel channel, String name) {
        GW2Account acc = accountContainer.getAccount(name);
        if (acc == null) {
            sendMessage(channel, "cannot find account");
            return;
        }
        GameHistory gameHistory = accountContainer.getDb().loadGameHistory(acc);
        if (gameHistory == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("Heres what I have on ").append(acc.getName()).append(":\n");
        DateFormat dateFormat = new SimpleDateFormat("MMM d haa");
        for (RatingSnapshot rs : gameHistory.getRatingHistory()) {
            String time = dateFormat.format(new Date(rs.time.getTime() + (60 * 60 * 1000)));
            sb.append(time)
                    .append(": ")
                    .append(rs.rating)
                    .append(" rating")
                    .append(" (")
                    .append(rs.wins)
                    .append("-")
                    .append(rs.losses)
                    .append(")\n");
        }
        sendMessage(channel, sb.toString());
    }

    public void forceHistoryUpdate() {
        System.out.println("Attemping unscheduled insertion...");
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.HOURS));
        for (GW2Account acc : accountContainer.getAllAccounts()) {
            accountContainer.getDb().runScheduledRatingSnapshotUpdate(acc, timestamp);
        }
    }


}
