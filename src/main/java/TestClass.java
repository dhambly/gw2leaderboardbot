import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Locale;
import java.util.stream.Stream;

public class TestClass {

    @org.junit.jupiter.api.Test
    void main() {
        assert AccountListHandler.isAnEgirl("Tyrael.6081");
    }
}
