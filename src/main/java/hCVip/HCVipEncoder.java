package hCVip;

import it.uniroma1.di.tmancini.teaching.ai.SATCodec.IntRange;
import it.uniroma1.di.tmancini.teaching.ai.SATCodec.SATEncoder;
import it.uniroma1.di.tmancini.utils.CmdLineOptions;
import utils.GraphInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HCVipEncoder {

    public static void main(String[] args) throws IOException {
        CmdLineOptions cmd = new CmdLineOptions("HC-Vip Encoder", "16/05/26",
                "Matteo Piscitello", "A simple SAT encoder for the HC-Vip problem");
        cmd.addOption("file", "The name of the file containing the graph", true);
        cmd.addOption("o", "The output file name");
        cmd.parse(args);
        Path path = Paths.get(cmd.getOptionValue("file"));
        GraphInfo graph = new GraphInfo();
        graph.getGraph(path);
        IntRange edges = new IntRange("edges", 1, graph.getEdgeMap().size()),
                stops = new IntRange("stops", 1, graph.getEdges().length);
        SATEncoder cnf = new SATEncoder("HC_Vip", cmd.getOptionValue("o"));
        cnf.defineFamilyOfVariables("X", stops, edges);
        for (int stop: stops.values()) {
            for (int i = 1; i < edges.size(); i++) {
                for (int j = i+1; j <= edges.size(); j++) {
                    cnf.addNegToClause("X", stop, i);
                    cnf.addNegToClause("X", stop, j);
                    cnf.endClause();
                }
            }
        }
        boolean first = true;
        for (int k = 1; k < stops.size(); k++) {
            for (int i = 0; i < graph.getEdges().length; i++) {
                for (int j = 0; j < graph.getEdges().length; j++) {
                    if (graph.getEdges()[i][j]) {
                        for (int f = 0; f < graph.getEdges().length; f++) {
                            if (graph.getEdges()[j][f]) {
                                if (first) {
                                    cnf.addNegToClause("X", k, graph.getEdgeMap()
                                            .get(List.of(i, j))+1);
                                    first = false;
                                }
                                cnf.addToClause("X", k+1, graph.getEdgeMap()
                                        .get(List.of(j, f))+1);
                            }
                        }
                        cnf.endClause();
                        first = true;
                    }
                }
            }
        }
        for (int i = 0; i < graph.getEdges().length; i++) {
            for (int j = 0; j < graph.getEdges().length; j++) {
                if (graph.getEdges()[j][i]) {
                    for (int f = 0; f < graph.getEdges().length; f++) {
                        if (graph.getEdges()[f][i]) {
                            for (int k1 = 1; k1 < stops.size(); k1++) {
                                for (int k2 = k1+1; k2 <= stops.size(); k2++) {
                                    cnf.addNegToClause("X", k1, graph.getEdgeMap()
                                            .get(List.of(j, i))+1);
                                    cnf.addNegToClause("X", k2, graph.getEdgeMap()
                                            .get(List.of(f, i))+1);
                                    cnf.endClause();
                                }
                            }
                        }
                    }
                }

            }
        }
        for (int i = 0; i < graph.getEdges().length; i++) {
            if (graph.getEdges()[graph.getStart()][i]) {
                cnf.addToClause("X", 1, graph.getEdgeMap()
                        .get(List.of(graph.getStart(), i))+1);
            }
        }
        cnf.endClause();
        for (int edge : edges.values()) {
            if (graph.getEdgeMapInv().get(edge-1).getLast() != graph.getStart()) {
                cnf.addNegToClause("X", graph.getEdges().length, edge);
                cnf.endClause();
            }
        }
        for (int edge : edges.values()) {
            if (graph.getVips().contains(graph.getEdgeMapInv().get(edge-1).getLast())) {
                for (int k = graph.getEdges().length / 2 + 1; k <= graph.getEdges().length; k++) {
                    cnf.addNegToClause("X", k, edge);
                    cnf.endClause();
                }
            }
        }
        cnf.end();
    }
}
