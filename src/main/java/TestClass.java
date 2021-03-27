import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.String.format;

public class TestClass {

    @org.junit.jupiter.api.Test
    void main() throws IOException {
        ObjectInputStream objectinputstream = null;
        try {
            FileInputStream streamIn = new FileInputStream("accounts.gw2");
            objectinputstream = new ObjectInputStream(streamIn);
            HashMap<String, GW2Account> readCase = (HashMap<String, GW2Account>) objectinputstream.readObject();
            System.out.println(readCase.values().toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(objectinputstream != null){
                objectinputstream.close();
            }
        }
    }

    @Test
    void outputAccountsFile() {
        ObjectInputStream objectinputstream = null;
        try {
            FileInputStream streamIn = new FileInputStream("accounts.gw2");
            objectinputstream = new ObjectInputStream(streamIn);
            HashMap<String, GW2Account> readCase = (HashMap<String, GW2Account>) objectinputstream.readObject();
            for (GW2Account acc: readCase.values()) {
                System.out.println(acc);
            }
//
//            for (String acc: readCase.keySet()) {
//                System.out.println(acc);
//            }
            System.out.println(format("Map size %d", readCase.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
