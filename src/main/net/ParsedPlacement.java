package main.net;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public class ParsedPlacement {

    private final String name;
    private final int x, y, subblock;

    private ParsedPlacement(String name, int x, int y, int subblock) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.subblock = subblock;
    }

    public static Map<String, ParsedPlacement> Parse(String file) {

        Path path = Path.of(file);
        HashMap<String, ParsedPlacement> pads = new HashMap<>();

        try (Stream<String> lines = Files.lines(path)) {
            Iterator<String> it = lines.iterator();

            String line;
            String[] elems = null;
            String padName;

            while (it.hasNext()) {
                line = it.next();
                if (!line.isBlank() && !line.startsWith("#") && !line.startsWith("Netlist file:")
                        && !line.startsWith("Array size:")) {
                    elems = line.replaceAll("\t+", " ").split(" ");
                    padName = elems[0];
                    int x = Integer.parseInt(elems[1]);
                    int y = Integer.parseInt(elems[2]);
                    int subblock = Integer.parseInt(elems[3]);
                    pads.put(padName, new ParsedPlacement(padName, x, y, subblock));
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

    public int getSubblock() {
        return subblock;
    }

}