package leaderboardbot;

import accounts.AccountContainer;
import accounts.DatabaseHelper;
import accounts.apiobjects.GW2Account;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoUpdater {
    AccountContainer accountContainer;
    AccountContainer accountContainer_EU;
    DatabaseHelper databaseHelper;

    AutoUpdater(AccountContainer accountContainer, AccountContainer accountContainer_EU) {
        this.accountContainer = accountContainer;
        this.accountContainer_EU = accountContainer_EU;
        this.databaseHelper = accountContainer.getDb();
    }

    public void schedule() {
        ScheduledExecutorService apiCallSchedule = Executors.newSingleThreadScheduledExecutor();
        apiCallSchedule.scheduleAtFixedRate(() -> {
            accountContainer.updateLeaderboard();
            accountContainer_EU.updateLeaderboard();
            System.out.println("Automatically updated leaderboard.");
        }, timeUntil5Mins(), 5*60*1000, TimeUnit.MILLISECONDS);

        ScheduledExecutorService updateRatingSnapshotsSchedule = Executors.newSingleThreadScheduledExecutor();
        updateRatingSnapshotsSchedule.scheduleAtFixedRate(() -> {
            System.out.println("Attempting to write rating snapshots.");
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.HOURS));
            for (GW2Account acc : accountContainer.getAllAccounts()) {
                databaseHelper.runScheduledRatingSnapshotUpdate(acc, timestamp, true);
            }
            for (GW2Account acc : accountContainer_EU.getAllAccounts()) {
                databaseHelper.runScheduledRatingSnapshotUpdate(acc, timestamp, false);
            }

        }, timeUntilTopOfHour(), 60*60*1000, TimeUnit.MILLISECONDS);

    }

    private long timeUntil5Mins() {
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE);
        int minutesTilNext5 = 5-(minutes%5);
        LocalDateTime nextStart = LocalDateTime.now().plusMinutes(minutesTilNext5).truncatedTo(ChronoUnit.MINUTES);
        return LocalDateTime.now().until(nextStart, ChronoUnit.MILLIS);
    }

    private long timeUntilTopOfHour() {
        LocalDateTime nextStart = LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.HOURS).plusMinutes(1);
        return LocalDateTime.now().until(nextStart, ChronoUnit.MILLIS);
    }


}
