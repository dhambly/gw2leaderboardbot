import java.util.Arrays;
import java.util.HashSet;

public class FuckingHelios {
    public static String[] CHIMPS = {
            "Helio.3054",
            "Zalpharx.9235",
            "Luna Windstar.9432",
            "EVILSWARM.4537",
            "Gungnir.8295",
            "ChaiQuanGou.5217",
            "Xtrverz.4069",
            "Metamon.5901",
            "Xtrverz.3579",
            "Malediktus.6283"
    };
    public static HashSet<String> CHIMPSET = new HashSet<>();

    public static boolean isAChimp(String acc) {
        if (CHIMPSET.isEmpty()) {
            CHIMPSET = new HashSet<>(Arrays.asList(CHIMPS));
        }
        return CHIMPSET.contains(acc);
    }
}
