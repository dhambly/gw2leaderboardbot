package handlers;

import accounts.AccountContainer;
import accounts.apiobjects.GW2Account;
import accounts.GameHistory;
import accounts.RatingSnapshot;
import com.mitchtalmadge.asciidata.graph.ASCIIGraph;
import leaderboardbot.AccountTracker;
import leaderboardbot.AutoUpdater;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.lang.String.*;

public class CommandHandler {
    private final AccountContainer accountContainer;
    private final MessageChannel channel;
    private final boolean isNA;
    private final String message;
//    private final String message;

    CommandHandler(AccountContainer accountContainer, MessageReceivedEvent messageReceivedEvent, boolean isNA) {
        this.accountContainer = accountContainer;
        this.channel = messageReceivedEvent.getChannel();
        this.isNA = isNA;
        this.message = messageReceivedEvent.getMessage().getContentDisplay();
//        this.message = message;
    }

    public void sendMessage(String msg) {
        if (msg.length() >= 2000) {
            String end = "\nMsg too long...";
            msg = msg.substring(0, 2000 - (end.length() + 1));
            msg += end;
        }
        System.out.println(msg);
        channel.sendMessage(msg).queue();
    }

    public void help() {
        String sb = "Current commands:\n" +
                "!chimpcheck\n" +
                "!rankedegirls\n" +
                "!biggestloser\n" +
                "!addict\n" +
                "!topaddicts\n" +
                "!top[X] or !top[X]-[Y]\n" +
                "!lookup [account]\n" +
                "!fallen\n" +
                "!shitter or !shitter [min games]\n" +
                "!getrating [API-KEY]\n" +
                "!rank [X]\n" +
                "!history [account]\n" +
                "!dailyhistory/!hourlygraph [account]\n" +
                "!historygraph [account]\n" +
                "!expose [account]\n" +
                "!buttbuddies";
        sendMessage(sb);
    }

    public void chimpCheck() {
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
            sendMessage(formattedMessage);
        } else {
            sendMessage("No chimps found!!!\ndid helio quit gw2....?");
        }
    }

    public void ariEbois() {
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
            sendMessage(formattedMessage);
        } else {
            sendMessage("No ebois found... Time to look for some more ari");
        }
    }

    public void loraHarem() {
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
            sendMessage(formattedMessage);
        } else {
            sendMessage("no one found... expand your harem lora");
        }
    }

    public void listEgirlRanks() {
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
            sendMessage(formattedMessage);
        } else {
            System.out.println("No egirls found :(\nstep it up simps");
            sendMessage("No egirls found :(\nstep it up simps");
        }

    }

    public void listTop10() {
        StringBuilder sb = new StringBuilder();
        for (GW2Account acc : accountContainer.getCurrentLeaderboard().subList(0, 10)) {
            sb.append(acc.reformattedToString()).append("\n");
        }
        String formattedMessage = (format("Current Top 10:\n%s", sb.toString()));
        sendMessage(formattedMessage);

    }

    public void winrateShitter(String wholemsg) {
        String[] splitMsg = wholemsg.split(" ");
        int minGames = 0;
        if (splitMsg.length > 1) {
            try {
                minGames = Integer.parseInt(splitMsg[1]);
            } catch (NumberFormatException e) {
                System.err.println(e.toString());
                char[] c = {102, 97, 103, 103, 111 ,116};
                sendMessage("put in numbers you " + String.copyValueOf(c));
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
                sendMessage("Nobody has that many games. Just wait a sec for Arrant Stark.");
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
        sendMessage(message);
    }

    public void biggestLoser() {
        ArrayList<GW2Account> list = accountContainer.getAllAccounts();
        GW2Account loser = list.get(0);
        for (GW2Account acc : list.subList(1, list.size())) {
            if (acc.getLosses() > loser.getLosses()) {
                loser = acc;
            }
        }
        String message = format("The biggest loser is %s with %d losses lmfao", loser.getName(), loser.getLosses());
        sendMessage(message);
    }

    public void gw2Addict() {
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
        sendMessage(message);
    }

    public void topAddicts() {
        ArrayList<GW2Account> list = accountContainer.getAllAccounts();
        PriorityQueue<GW2Account> priorityQueue = new PriorityQueue<>((b, a)
                -> Integer.compare(a.getWins() + a.getLosses(), b.getWins() + b.getLosses()));
        priorityQueue.addAll(list);
        StringBuilder sb = new StringBuilder("GW2's worst no lifers are:\n");
        int initialSize = priorityQueue.size();
        for (int i = 0; i < 10 && i < initialSize; i++) {
            GW2Account acc = priorityQueue.poll();
            if (acc != null)
                sb.append(acc.getName()).append(" with ").append(acc.getWins() + acc.getLosses()).append(" total games\n");
        }
        sendMessage(sb.toString());
    }

    public void kaypud() {
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
        sendMessage(message);
    }

    public void checkmoobs() {
        GW2Account moobs = null;
        sendMessage("i promise you he's not on there but ill check");
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
        sendMessage(message);
    }

    public void moobs() {
        String[] messages = {
                "https://cdn.discordapp.com/attachments/797310153073491979/895387318637166602/unknown.png"
        };
        defaultRandomCommand(messages, "!moobs");
    }

    public void getName(String name) {
        ArrayList<GW2Account> lookup = accountContainer.getAllMatchingAccounts(name.toLowerCase());
        StringBuilder sb = new StringBuilder();
        lookup.sort((a, b) -> Integer.compare(b.getRating(), a.getRating()));
        for (GW2Account acc : lookup) {
            String message;
            if (acc.isOnLeaderboard()) {
                message = acc.reformattedToString() + "\n";
            } else {
                message = format("%s was last seen with rating *%d* (%d-%d) on %s\n",
                        acc.getName(), acc.getRating(), acc.getWins(), acc.getLosses(), acc.getFormattedDate());
            }
            sb.append(message);
        }
        if (sb.length() > 0) {
            sendMessage(sb.toString());
        } else {
            sendMessage("Cannot find account");
        }

    }

    public void topX(String command) {
        int start = 0;
        int end;

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
            sendMessage(formattedMessage);
        } catch (NumberFormatException e) {
            System.err.println(e.toString());
            char[] c = {102, 97, 103, 103, 111 ,116};
            sendMessage("put in numbers you " + String.copyValueOf(c));
        }
    }

    public void lostAccounts() {
        System.out.println("Trying to find lost accounts");
        StringBuilder sb = new StringBuilder();
        PriorityQueue<GW2Account> droppedAccounts = accountContainer.getDroppedAccounts();
        int total = droppedAccounts.size();
        sb.append("Here are the top currently tracked accounts off the leaderboard:\n");
        for (int i = 0; i < 25 && !droppedAccounts.isEmpty(); i++) {
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
            sendMessage(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void calebSwag() {
        char[] c = {102, 97, 103, 103, 111 ,116};
        sendMessage("is a " + String.copyValueOf(c));
    }

    public void historyLookup(String name) {
        GW2Account acc = accountContainer.getAccount(name);
        if (acc == null) {
            sendMessage("cannot find account");
            return;
        }
        GameHistory gameHistory = accountContainer.getDb().loadGameHistory(acc, isNA);
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
        sendMessage(sb.toString());
    }

    public void forceHistoryUpdate() {
        System.out.println("Attemping unscheduled insertion...");
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.HOURS));
        for (GW2Account acc : accountContainer.getCurrentLeaderboard()) {
            accountContainer.getDb().runScheduledRatingSnapshotUpdate(acc, timestamp, isNA);
        }
    }

    public void getRatingFromAPIKey(String key) {
        int rating = accountContainer.getRatingFromAPIKey(key);
        String name = accountContainer.getNameFromAPIKey(key);
        if (rating < 0) {
            sendMessage("invalid key");
        } else {
            sendMessage(format("%s was found with rating %d", name, rating));
        }
    }


    public void forceUpdate() {
        accountContainer.updateLeaderboard();
        sendMessage("updated.");
    }

    public void getFromRank(String message) {
        message = message.replace("!rank", "").trim();
        try {
            int rank = Integer.parseInt(message);
            GW2Account cur = accountContainer.getCurrentLeaderboard().get(rank - 1);
            sendMessage(cur.reformattedToString());
        } catch (Exception e) {
            sendMessage("use a proper number noob");
        }
    }

    public void graphTest() {
        String a = ASCIIGraph.fromSeries(new double[]{1.0, 2.0, 3.0}).plot();
        sendMessage(a);
        sendMessage("```" + a + "```");
    }

    public void graphHistory(String name) {
        GW2Account acc = accountContainer.getAccount(name);
        if (acc == null) {
            sendMessage("cannot find account");
            return;
        }
        GameHistory gameHistory = accountContainer.getDb().loadGameHistory(acc, isNA);
        if (gameHistory == null) return;
        String graph = GraphHandler.graphRatingHistory(gameHistory.generateDailyRatingHistory(), 72);
        String msg = "Here's the full daily history for " + acc.getName() + "\n";
        sendMessage(msg + "```" + graph + "```");
    }

    public void graphHourlyHistory(String name) {
        int MAX_SIZE = 24;
        GW2Account acc = accountContainer.getAccount(name);
        if (acc == null) {
            sendMessage("cannot find account");
            return;
        }
        GameHistory gameHistory = accountContainer.getDb().loadGameHistory(acc, isNA);
        if (gameHistory == null) return;
        String graph = GraphHandler.getFormattedHourlyGraph(gameHistory.generateHourlyRatingHistory());
        String msg = "Here's the last " + MAX_SIZE + " hours of rating for " + acc.getName() + "\n";
        sendMessage(msg + "```" + graph + "```");
    }

    public void addExposeTracker(MessageReceivedEvent messageReceivedEvent, String name) {
        GW2Account acc = accountContainer.getAccount(name);
        if (acc == null) {
            sendMessage("cannot find account");
            return;
        }
        AccountTracker tracker = new AccountTracker(messageReceivedEvent, acc, isNA);
        AutoUpdater.addTracker(tracker);
        sendMessage("now tracking " + acc.getName() + " for 24 hours");
    }

    public void findButtBuddies() {
        ArrayList<GW2Account> leaderboard = accountContainer.getCurrentLeaderboard();
        ArrayList<List<GW2Account>> matches = new ArrayList<>();
        GW2Account first = leaderboard.get(0);
        GW2Account second;
        for (int i = 1; i < leaderboard.size(); i++) {
            second = leaderboard.get(i);
            if (first.getRating() == second.getRating() &&
                    first.getWins() == second.getWins() &&
                    first.getLosses() == second.getLosses()) {
                matches.add(Arrays.asList(first, second));
            }
            first = second;
        }
        StringBuilder sb = new StringBuilder();
        if (matches.size() > 0) {
            for (List<GW2Account> pairs : matches) {
                sb.append("**")
                        .append(pairs.get(0).getName())
                        .append("** and **")
                        .append(pairs.get(1).getName())
                        .append("** are butt buddies at rank ")
                        .append(pairs.get(0).getRank())
                        .append(" having played all their games together :)\n");
            }
        } else {
            sb.append("no buttbuddies could be found");
        }
        sendMessage(sb.toString());
    }

    public void patchNotes() {
        var eastern = ZoneId.of("America/New_York");
        ZonedDateTime now = Instant.now().atZone(eastern);
//        ZonedDateTime patchTime = ZonedDateTime
        LocalTime patchTime = LocalTime.of(15, 0);
        LocalTime nowTime = LocalTime.now(eastern);
        LocalDateTime nowDateTime = LocalDateTime.now(eastern);
        LocalDate today = LocalDate.now(eastern);
        LocalDateTime patchDateTime;
        if (nowTime.isAfter(patchTime)) {
            patchDateTime = LocalDateTime.of(today.plusDays(1), patchTime);
        } else {
            patchDateTime = LocalDateTime.of(today, patchTime);
        }
        long hours = nowDateTime.until(patchDateTime, ChronoUnit.HOURS);
        long minutes = nowDateTime.until(patchDateTime, ChronoUnit.MINUTES) % 60;
        if (minutes < 59) minutes++;
        if (hours > 0) {
            sendMessage("the patch notes are in " + hours + " hour" + (hours > 1 ? "s" : "") + " and " + minutes + " minute" + (minutes > 1 ? "s" : "") + " i swear <:copium:838853217206796339>");
        } else {
            sendMessage("the patch notes are in " + minutes + " minute" + (minutes > 1 ? "s" : "") + " i swear <:copium:838853217206796339>");
        }
    }

    public void steel() {
        String[] messages = {
                "https://cdn.discordapp.com/attachments/845036889261998161/889714889600405534/image0.jpg",
                "https://media.discordapp.net/attachments/396845083871412224/707081849465208872/steeldone.png"
        };
        defaultRandomCommand(messages, "!steel");
    }

    public void fishing() {

        sendMessage("\"bro omg did you get the new fish new to the poopoo pee pee vista\"\n \"no dude hop on my new gemstore skiff and lets go\"");
    }

    public void naru(int num) {
        String[] messages = {
                "https://cdn.discordapp.com/attachments/845036889261998161/889552546732081172/image0.png",
                "https://cdn.discordapp.com/attachments/845036889261998161/889715315724922900/Screenshot_140.png",
                "https://cdn.discordapp.com/attachments/845036889261998161/851881734030229534/Snapchat-1130386685.jpg",
                "https://cdn.discordapp.com/attachments/845036889261998161/890647065821069392/b200058f505f697f4106d8a40e2c49f8.png",
                "https://cdn.discordapp.com/attachments/845036889261998161/890650001024380999/image0.png"
        };
        defaultRandomCommand(messages, "!naru");
    }

    public void scrims() {
        String[] messages = {
                "https://cdn.discordapp.com/attachments/845036889261998161/884643671050436678/nubu.jpg",
                "https://cdn.discordapp.com/attachments/549358542566719534/558385420539527229/image0.png"
        };
        defaultRandomCommand(messages, "!scrims");
    }

    public void kat() {
        String message = "https://cdn.discordapp.com/attachments/652471470777171991/720784153326518302/kat.jpg";
        sendMessage(message);
    }

    public void zeromis() {
        String[] messages = {
                "https://cdn.discordapp.com/attachments/845036889261998161/856663731076333588/614222c556f22ae7e658c08792b4fb0a.png",
                "https://cdn.discordapp.com/attachments/652471470777171991/709568447994134568/xposeddd.png",
                "https://cdn.discordapp.com/attachments/549358542566719534/564107639835852801/image0.png"
        };
        defaultRandomCommand(messages, "!zeromis");
    }

    public void shorts() {
        String[] messages = {
                "https://c.tenor.com/avISvU9toQQAAAAC/make-wish.gif",
                "https://cdn.discordapp.com/attachments/845036889261998161/890653620226367518/17a801ee2ddcee63b747356cab85d0eb-png.jpg",
                "https://cdn.discordapp.com/attachments/845036889261998161/890671314820550736/image0.jpg"
        };
        defaultRandomCommand(messages, "!shorts");
    }

    public void toker() {
        String[] messages = {
                "https://cdn.discordapp.com/attachments/652471470777171991/720786952311275590/21om1g.png"
        };
        defaultRandomCommand(messages, "!toker");
    }

    public void nos() {
        String[] messages = {
                "https://cdn.discordapp.com/attachments/845036889261998161/890647324475424779/unknown.png",
                "https://cdn.discordapp.com/attachments/845036889261998161/890647617812435085/unknown.png"
        };
        defaultRandomCommand(messages, "!nos");
    }

    public void helio() {
        String[] messages = {
                "https://cdn.discordapp.com/attachments/845036889261998161/890650527891881994/unknown.png",
                "https://media.discordapp.net/attachments/584530187715346479/796606521941295114/nemuhelio2.png",
                "https://cdn.discordapp.com/attachments/845036889261998161/890653890893213706/collage_1.jpg"
        };
        defaultRandomCommand(messages, "!helio");
    }

    public void mark() {
        String[] messages = {
                "https://www.youtube.com/watch?v=keO9LqzCgU4",
                "https://cdn.discordapp.com/attachments/845036889261998161/890653906865094726/unknown-20.png"
        };
        defaultRandomCommand(messages, "!mark");
    }

    public void grim() {
        String[] messages = {
                "https://cdn.discordapp.com/attachments/690985565746757642/890654816659963904/image0.png"
        };
        defaultRandomCommand(messages, "!grim");
    }

    public void nemu() {
        String[] messages = {
                "https://cdn.discordapp.com/attachments/845036889261998161/890655205174161428/image0.png"
        };
        defaultRandomCommand(messages, "!nemu");
    }

    public void defaultRandomCommand(String[] messages, String command) {
        int num;
        try {
            num = Integer.parseInt(message.substring(command.length()).trim());
        } catch (Exception e) {
            num = -1;
        }
        if (num < 1 || num > messages.length) {
            num = new Random().nextInt(messages.length);
        } else {
            num--;
        }
        sendMessage(messages[num]);
    }

    public void clearComms() {
        String x = "x\n\n";
        sendMessage(x.repeat(25));
    }

    public void zooseNutsJar() {
        ArrayList<String> messages = accountContainer.getDb().getRandomMultiThing("zoosenuts");
        StringBuilder sb = new StringBuilder();
        sb.append("The zoose nuts jar has ")
                .append(messages.size())
                .append(" nuts references, including:\n");
        Random random = new Random();
        sb.append(messages.get(0));
        sb.append("\n");
        messages.remove(0);
        for (int i = 0; i < 5 && messages.size() > 0; i++) {
            int rand = random.nextInt(messages.size());
            sb.append(messages.get(rand));
            sb.append("\n");
            messages.remove(rand);
        }
        sendMessage(sb.toString());

    }
}
