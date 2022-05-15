package edu.festu.ivan.kuznetsov.myapplication.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

public class DijkstraShortestPath {

    public void computeShortestPaths(Vertex sourceVertex){

        sourceVertex.setDistance(0);
        PriorityQueue<Vertex> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(sourceVertex);
        sourceVertex.setVisited(true);

        while( !priorityQueue.isEmpty() ){
            // Getting the minimum distance vertex from priority queue
            Vertex actualVertex = priorityQueue.poll();

            for(Edge edge : actualVertex.getAdjacenciesList()){

                Vertex v = edge.getTargetVertex();
                if(!v.isVisited())
                {
                    double newDistance = actualVertex.getDistance() + edge.getWeight();

                    if( newDistance < v.getDistance() ){
                        priorityQueue.remove(v);
                        v.setDistance(newDistance);
                        v.setPredecessor(actualVertex);
                        priorityQueue.add(v);
                    }
                }
            }
            actualVertex.setVisited(true);
        }
    }

    public List<Vertex> getShortestPathTo(Vertex targetVertex){
        List<Vertex> path = new ArrayList<>();

        for(Vertex vertex=targetVertex;vertex!=null;vertex=vertex.getPredecessor()){
            path.add(vertex);
        }

        Collections.reverse(path);
        return path;
    }
    public static List<Vertex> getPath(String from, String to){
        Map<String, Vertex> vertexMap = new HashMap<>();
        vertexMap.put("441",new Vertex("441"));
        vertexMap.put("439",new Vertex("439"));
        vertexMap.put("437",new Vertex("437"));
        vertexMap.put("437a",new Vertex("437a"));
        vertexMap.put("435",new Vertex("435"));
        vertexMap.put("433",new Vertex("433"));
        vertexMap.put("431",new Vertex("431"));
        vertexMap.put("428",new Vertex("428"));
        vertexMap.put("426",new Vertex("426"));
        vertexMap.put("422",new Vertex("422"));
        vertexMap.put("420",new Vertex("420"));
        vertexMap.put("Лестница №4",new Vertex("Лестница №4"));


        for (Vertex vertex : Arrays.asList(vertexMap.get("439"), vertexMap.get("428"))) {
            Objects.requireNonNull(vertexMap.get("441")).addNeighbour(vertex);
        }

        for (Vertex vertex : Arrays.asList(vertexMap.get("441"), vertexMap.get("439"))) {
            Objects.requireNonNull(vertexMap.get("428")).addNeighbour(vertex);
        }

        for (Vertex vertex : Arrays.asList(vertexMap.get("441"), vertexMap.get("428"), vertexMap.get("426"), vertexMap.get("437"))) {
            Objects.requireNonNull(vertexMap.get("439")).addNeighbour(vertex);
        }

        for (Vertex vertex : Arrays.asList(vertexMap.get("439"), vertexMap.get("437"))) {
            Objects.requireNonNull(vertexMap.get("426")).addNeighbour(vertex);
        }

        for (Vertex vertex : Arrays.asList(vertexMap.get("439"), vertexMap.get("426"), vertexMap.get("437a"))) {
            Objects.requireNonNull(vertexMap.get("437")).addNeighbour(vertex);
        }

        for (Vertex vertex : Arrays.asList(vertexMap.get("437"), vertexMap.get("Лестница №4"))) {
            Objects.requireNonNull(vertexMap.get("437a")).addNeighbour(vertex);
        }

        for (Vertex vertex : Arrays.asList(vertexMap.get("422"), vertexMap.get("437a"), vertexMap.get("435"))) {
            Objects.requireNonNull(vertexMap.get("Лестница №4")).addNeighbour(vertex);
        }

        for (Vertex vertex : Arrays.asList(vertexMap.get("Лестница №4"), vertexMap.get("435"))) {
            Objects.requireNonNull(vertexMap.get("422")).addNeighbour(vertex);
        }

        for (Vertex vertex : Arrays.asList(vertexMap.get("Лестница №4"), vertexMap.get("422"), vertexMap.get("420"), vertexMap.get("433"))) {
            Objects.requireNonNull(vertexMap.get("435")).addNeighbour(vertex);
        }

        for (Vertex vertex : Arrays.asList(vertexMap.get("435"), vertexMap.get("433"))) {
            Objects.requireNonNull(vertexMap.get("420")).addNeighbour(vertex);
        }

        for (Vertex vertex : Arrays.asList(vertexMap.get("435"), vertexMap.get("420"), vertexMap.get("431"))) {
            Objects.requireNonNull(vertexMap.get("433")).addNeighbour(vertex);
        }

        Objects.requireNonNull(vertexMap.get("431")).addNeighbour(vertexMap.get("433"));


        DijkstraShortestPath shortestPath = new DijkstraShortestPath();
        shortestPath.computeShortestPaths(Objects.requireNonNull(vertexMap.get(from)));




        return shortestPath.getShortestPathTo(vertexMap.get(to));
    }
}