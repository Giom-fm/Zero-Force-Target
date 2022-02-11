import java.io.IOException;
import java.util.List;

import net.ParsedBlock;
import net.Net;

public class Main {
    public static void main(String[] args) throws IOException {
        List<ParsedBlock> blocks =  ParsedBlock.Parse("Benchmarks/net/alu4.net");
        Net net = new Net(blocks);
    }
}