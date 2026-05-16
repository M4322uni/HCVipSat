package hCVipSat;

import it.uniroma1.di.tmancini.teaching.ai.SATCodec.SATModelDecoder;
import utils.GraphInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HCVipSatDecoder {

    private static List<String[]> argsPartition(String[] args) {
        if (args.length < 2)
            throw new IllegalArgumentException("Missing parameters");
        String[] left = null, right = new String[args.length-2];
        int rIdx = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--file")) {
                left = new String[]{args[++i]};
            }
            else
                try {
                    right[rIdx++] = args[i];
                }
                catch (IndexOutOfBoundsException e) {
                    throw new IllegalArgumentException("Missing --file argument (the file containing " +
                            "the graph)");
                }
        }
        assert left != null;
        return List.of(left, right);
    }

    public static void main(String[] args) throws IOException {
        List<String[]> partition = argsPartition(args);
        SATModelDecoder dec = new SATModelDecoder(partition.getLast());
        dec.run();
        Path path = Paths.get(partition.getFirst()[0]);
        GraphInfo save = new GraphInfo();
        save.getGraph(path);
        int maxVar = dec.getMaxVar();
        SATModelDecoder.Var var;
        List<Integer> coords;
        String leftNode, rightNode;
        ArrayList<List<String>> plan = new ArrayList<>(Collections.nCopies(save.getEdges().length,
                new ArrayList<>()));
        for  (int i = 1; i <= maxVar; i++) {
            var = dec.decodeVariable(i);
            if (dec.getModelValue(i)) {
                coords = save.getEdgeMapInv().get(var.getIndices().getLast()-1);
                leftNode = save.getVertexMap().get(coords.getFirst());
                rightNode = save.getVertexMap().get(coords.getLast());
                plan.set(var.getIndices().getFirst()-1, List.of(leftNode, rightNode));
            }
        }
        for (int i = 0; i < plan.size(); i++) {
            System.out.println("Step " + (i+1) + ": " + plan.get(i).getFirst()
                + " -> " + plan.get(i).getLast());
        }
    }
}
