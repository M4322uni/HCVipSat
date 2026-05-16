package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class GraphInfo {
    private boolean[][] edges;
    private HashMap<Integer, String> vertexMap;
    private HashMap<List<Integer>, Integer> edgeMap;
    private HashMap<Integer, List<Integer>> edgeMapInv;
    private HashSet<Integer> vips;
    private int start;

    public void getGraph(Path path) throws IOException {
        HashMap<String, Integer> map = new HashMap<>();
        vertexMap = new HashMap<>();
        HashSet<List<String>> edges = new HashSet<>();
        vips = new HashSet<>();
        String line;
        String[] split;
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            while (true) {
                line = reader.readLine();
                line = line.strip();
                if (line.isEmpty()) break;
                split = line.split(" ");
                if (split.length != 2) {
                    throw new IllegalArgumentException("Invalid file format: the file should first represent a single edge " +
                            "at each line (<name_of_vertex_1> <name_of_vertex_2>),\n it should, then, be separated by and empty "+
                            "line, following which all the VIP nodes should be listed, one at a line");
                }
                map.putIfAbsent(split[0], map.size());
                if (!map.isEmpty())
                    vertexMap.putIfAbsent(map.size()-1, split[0]);
                map.putIfAbsent(split[1], map.size());
                if (!map.isEmpty())
                    vertexMap.putIfAbsent(map.size()-1, split[1]);
                edges.add(List.of(split[0], split[1]));
            }
            while (reader.ready()) {
                line = reader.readLine();
                line = line.strip();
                vips.add(map.get(line));
            }
        }
        if (!map.containsKey("start"))
            throw new IllegalArgumentException("Starting point not found");
        start = map.get("start");
        this.edges = new boolean[map.size()][map.size()];
        for (List<String> edge : edges)
            this.edges[map.get(edge.getFirst())][map.get(edge.getLast())] = true;
        edgeMap = new HashMap<>();
        edgeMapInv = new HashMap<>();
        List<Integer> temp;
        for (int i = 0; i < this.edges.length; i++) {
            for (int j = 0; j < this.edges.length; j++) {
                if (this.edges[i][j])  {
                    temp = List.of(i, j);
                    edgeMap.put(temp, edgeMap.size());
                    edgeMapInv.put(edgeMapInv.size(), temp);
                }
            }
        }
    }

    public boolean[][] getEdges() {
        return edges;
    }

    public Map<Integer, String> getVertexMap() {
        return vertexMap;
    }

    public Map<List<Integer>, Integer> getEdgeMap() {
        return edgeMap;
    }

    public Map<Integer, List<Integer>> getEdgeMapInv() {
        return edgeMapInv;
    }

    public HashSet<Integer> getVips() {
        return vips;
    }

    public int getStart() {
        return start;
    }
}
