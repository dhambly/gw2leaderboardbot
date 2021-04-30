package leaderboardbot;

import accounts.AccountContainer;
import accounts.DatabaseHelper;
import accounts.GW2Account;
import org.w3c.dom.CDATASection;

import javax.xml.crypto.Data;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoUpdater {
    AccountContainer accountContainer;
    DatabaseHelper databaseHelper;

    AutoUpdater(AccountContainer accountContainer) {
        this.accountContainer = accountContainer;
        this.databaseHelper = accountContainer.getDb();
    }

    public void schedule() {
        ScheduledExecutorService apiCallSchedule = Executors.newSingleThreadScheduledExecutor();
        apiCallSchedule.scheduleAtFixedRate(() -> {
            accountContainer.updateLeaderboard();
            System.out.println("Automatically updated leaderboard.");
        }, timeUntil5Mins(), 5*60*1000, TimeUnit.MILLISECONDS);

        ScheduledExecutorService updateRatingSnapshotsSchedule = Executors.newSingleThreadScheduledExecutor();
        updateRatingSnapshotsSchedule.scheduleAtFixedRate(() -> {
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.HOURS));
            for (GW2Account acc : accountContainer.getAllAccounts()) {
                databaseHelper.runScheduledRatingSnapshotUpdate(acc, timestamp);
            }
            System.out.println("Automatically updated leaderboard.");
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
