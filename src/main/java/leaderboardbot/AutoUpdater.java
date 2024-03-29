package leaderboardbot;

import accounts.AccountContainer;
import accounts.DatabaseHelper;
import accounts.apiobjects.GW2Account;
import handlers.APIHandler;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoUpdater {
    private final AccountContainer accountContainer;
    private final AccountContainer accountContainer_EU;
    private final DatabaseHelper databaseHelper;
    private static final ArrayList<AccountTracker> trackers = new ArrayList<>();

    AutoUpdater(AccountContainer accountContainer, AccountContainer accountContainer_EU) {
        this.accountContainer = accountContainer;
        this.accountContainer_EU = accountContainer_EU;
        this.databaseHelper = accountContainer.getDb();
    }

    public void schedule() {
        ScheduledExecutorService apiCallSchedule = Executors.newSingleThreadScheduledExecutor();
        apiCallSchedule.scheduleAtFixedRate(() -> {
            if (System.currentTimeMillis() - accountContainer.getApi().getLatestSeason().getEnd() < 86400000) {
                accountContainer.updateLeaderboard();
                accountContainer_EU.updateLeaderboard();
                for (AccountTracker tracker : trackers) {
                    tracker.checkForUpdates(tracker.isNA() ? accountContainer : accountContainer_EU);
                }
                System.out.println("Automatically updated leaderboard.");
            } else {
                System.out.println("Skipped update due to season being over for a day.");
            }
        }, timeUntilNextMinute(), 60 * 1000, TimeUnit.MILLISECONDS);

        ScheduledExecutorService updateRatingSnapshotsSchedule = Executors.newSingleThreadScheduledExecutor();
        updateRatingSnapshotsSchedule.scheduleAtFixedRate(() -> {
            System.out.println("Attempting to write rating snapshots.");
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.HOURS));
            for (GW2Account acc : accountContainer.getCurrentLeaderboard()) {
                databaseHelper.runScheduledRatingSnapshotUpdate(acc, timestamp, true);
            }
            for (GW2Account acc : accountContainer_EU.getCurrentLeaderboard()) {
                databaseHelper.runScheduledRatingSnapshotUpdate(acc, timestamp, false);
            }

        }, timeUntilTopOfHour(), 60 * 60 * 1000, TimeUnit.MILLISECONDS);

        ScheduledExecutorService checkForNewSeason = Executors.newSingleThreadScheduledExecutor();
        checkForNewSeason.scheduleAtFixedRate(() -> {
            APIHandler api = accountContainer.getApi();
            api.reinitializeSeasons();
            databaseHelper.setSeasonIDs(api.getSeasons());
        }, timeUntilTopOfHour() - 60000, 24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);

    }

    private long timeUntilNextMinute() {
        LocalDateTime nextStart = LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MINUTES);
        return LocalDateTime.now().until(nextStart, ChronoUnit.MILLIS);
    }

    private long timeUntil5Mins() {
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE);
        int minutesTilNext5 = 5 - (minutes % 5);
        LocalDateTime nextStart = LocalDateTime.now().plusMinutes(minutesTilNext5).truncatedTo(ChronoUnit.MINUTES);
        return LocalDateTime.now().until(nextStart, ChronoUnit.MILLIS);
    }

    private long timeUntilTopOfHour() {
        LocalDateTime nextStart = LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.HOURS).plusMinutes(1);
        return LocalDateTime.now().until(nextStart, ChronoUnit.MILLIS);
    }

    public static void addTracker(AccountTracker tracker) {
        trackers.add(tracker);
    }

    public static boolean removeTracker(AccountTracker tracker) {
        return trackers.remove(tracker);
    }

    public static boolean removeTrackerByName(String name) {
        return trackers.removeIf(at -> at.getAccountName().equalsIgnoreCase(name));
    }
}
