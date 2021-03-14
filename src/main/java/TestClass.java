import org.junit.jupiter.api.Assertions;

import java.util.HashSet;

public class TestClass {

    @org.junit.jupiter.api.Test
    void main() {
        String a = "Helio.3054";
        HashSet<String> b = FuckingHelios.CHIMPSET;
        String[] c = FuckingHelios.CHIMPS;
        Assertions.assertTrue(FuckingHelios.isAChimp(a));
    }
}
