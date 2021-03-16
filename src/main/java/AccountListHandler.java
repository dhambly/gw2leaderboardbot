import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Stream;

public class AccountListHandler {
    private static final String ACCOUNT_PATH = "accountsets/";
    private static final HashMap<String, HashSet<String>> accountListMap = new HashMap<>();


    public static boolean accountIsA(String account, String accountListName) {
        if (!accountListMap.containsKey(accountListName)) {
            initializeGeneralSet(accountListName);
        }
        return accountListMap.get(accountListName).contains(account.toLowerCase());
    }

    public static Path getAccounts(String filename) {
        return Paths.get(ACCOUNT_PATH + filename);
    }
    public static boolean isAChimp(String acc) {
        return accountIsA(acc, "chimps");
    }

    public static boolean isAnEgirl(String acc) {
        return accountIsA(acc, "egirls");
    }


    public static void initializeGeneralSet(String setName) {
        Path path = getAccounts(setName);
        try (Stream<String> lines = Files.lines(path)) {
            HashSet<String> tempSet = new HashSet<>();
            lines.forEachOrdered(line-> tempSet.add(line.toLowerCase()));
            accountListMap.put(setName,tempSet);
        } catch (IOException e) {
            System.out.println("Failed to find file");
            System.err.println(e.toString());
        }
    }
}
