package handlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Stream;

public class TextWidthHelper {
    static private HashMap<Character, Integer> standardWidthMap;
    static private HashMap<Character, Integer> boldWidthMap;

    static {
        standardWidthMap = new HashMap<>();
        boldWidthMap = new HashMap<>();
        try (Stream<String> lines = Files.lines(Paths.get("textwidth/textwidthmap"))) {
            lines.forEachOrdered(line -> {
                char character = line.charAt(0);
                String[] ints = line.split(":")[1].split(",");
                int standard = Integer.parseInt(ints[0]);
                int bold = Integer.parseInt(ints[1]);
                standardWidthMap.put(character, standard);
                boldWidthMap.put(character, bold);
            });
        } catch (IOException e) {
            System.out.println("Failed to find file");
            System.err.println(e.toString());
        }
    }

    public static int getStandardWidth(String string) {
        int width = 0;
        for (char c : string.toCharArray()) {
            //width += standardWidthMap.getOrDefault(c, 0);
            try {
                width += standardWidthMap.get(c);
            } catch (NullPointerException e) {
                System.out.println("Null pointer with character '" + c + "' when looking up text width.");
                width += 5;
            }
        }
        return width;
    }

    public static int getBoldWidth(String string) {
        int width = 0;
        for (char c : string.toCharArray()) {
            //width += standardWidthMap.getOrDefault(c, 0);
            try {
                width += boldWidthMap.get(c);
            } catch (NullPointerException e) {
                System.out.println("Null pointer with character '" + c + "' when looking up text width.");
                width += 5;
            }
        }
        return width;
    }
}
