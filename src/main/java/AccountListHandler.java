import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.stream.Stream;

public class AccountListHandler {
    private static final String ACCOUNT_PATH = "src/main/accountsets/";
    private static HashSet<String> chimpSet = new HashSet<>();
    private static HashSet<String> egirlSet = new HashSet<>();

    public static Path getAccounts(String filename) {
        return Paths.get(ACCOUNT_PATH + filename);
    }
    public static boolean isAChimp(String acc) {
        if (chimpSet.isEmpty()) {
            initializeChimpSet();
        }
        return chimpSet.contains(acc.toLowerCase());
    }

    public static boolean isAnEgirl(String acc) {
        if (egirlSet.isEmpty()) {
            initializeEgirlSet();
        }
        return egirlSet.contains(acc.toLowerCase());
    }

    private static void initializeEgirlSet() {
        Path path = getAccounts("egirls");
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEachOrdered(line-> egirlSet.add(line.toLowerCase()));
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    public static void initializeChimpSet() {
        Path path = getAccounts("chimps");
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEachOrdered(line-> chimpSet.add(line.toLowerCase()));
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
}
