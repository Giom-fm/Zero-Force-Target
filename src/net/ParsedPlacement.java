package net;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ParsedPlacement {

    private final String name;
    private final int x, y;

    private ParsedPlacement(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public static Map<String, ParsedPlacement> Parse(String file) {

        Path path = Path.of(file);
        HashMap<String, ParsedPlacement> pads = new HashMap<>();

        try (Stream<String> lines = Files.lines(path)) {
            Iterator<String> it = lines.iterator();

            String line;
            String[] elems = null;
            String padName;
            int x = 0, y = 0;

            while (it.hasNext()) {
                line = it.next();
                if (!line.isBlank() && !line.startsWith("#") && !line.startsWith("Netlist file:")
                        && !line.startsWith("Array size:")) {
                    elems = line.replaceAll("\t+", " ").split(" ");
                    padName = elems[0];
                    x = Integer.parseInt(elems[1]);
                    y = Integer.parseInt(elems[2]);
                    pads.put(padName, new ParsedPlacement(padName, x, y));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pads;

    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}