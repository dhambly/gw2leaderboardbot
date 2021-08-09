package leaderboardbot;

import accounts.AccountContainer;
import accounts.apiobjects.GW2Account;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class AccountTracker {
    private final MessageChannel messageChannel;
    private final String accountName;
    private GW2Account prevAccount;
    private final String userMention;
    private final boolean isNA;
    private final Instant timeStarted;

    public AccountTracker(MessageReceivedEvent messageReceivedEvent, GW2Account initialAccount, boolean isNA) {
        this.messageChannel = messageReceivedEvent.getChannel();
        this.accountName = initialAccount.getNameToLower();
        this.prevAccount = initialAccount;
        this.userMention = messageReceivedEvent.getMessage().getAuthor().getAsMention();
        this.isNA = isNA;
        this.timeStarted = Instant.now();
    }

    public void checkForUpdates(AccountContainer accountContainer) {
        GW2Account newAccount = accountContainer.getAccount(accountName);
        if (newAccount == null) {
            System.err.println("serious error in account tracker for " + accountName);
            return;
        }
        int ratingDiff = newAccount.getRating() - prevAccount.getRating();
        int winDiff = newAccount.getWins() - prevAccount.getWins();
        int lossDiff = newAccount.getLosses() - prevAccount.getLosses();
        if (ratingDiff != 0 || winDiff > 0 || lossDiff > 0) {
            String sb = userMention +
                    "\n" +
                    newAccount.getName() +
                    " played " +
                    (winDiff + lossDiff) +
                    " game" +
                    (winDiff + lossDiff > 1 ? "s" : "") +
                    (ratingDiff > 0 ? " gaining " : " losing ") +
                    Math.abs(ratingDiff) +
                    " rating.";
            messageChannel.sendMessage(sb).queue();
            prevAccount = newAccount;
        }
        if(Instant.now().minus(1, ChronoUnit.DAYS).isAfter(timeStarted)) {
            if (!AutoUpdater.removeTracker(this)) {
                System.err.println("failed to remove tracker for " + newAccount.getName());
            } else {
                System.out.println("removed tracker for " + newAccount.getName());;
            }
        }
    }

    public String getAccountName() {
        return accountName;
    }

    public boolean isNA() {
        return isNA;
    }
}
