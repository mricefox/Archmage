package com.mricefox.archmage.runtime;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.mricefox.archmage.runtime.Utils.checkNull;


/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:Directed acyclic graph, contains topological ordering
 * <p>Date:2018/1/2
 */

class DAG<Node> {
    private final List<Node> nodes = new LinkedList<>();
    private final List<Edge> edges = new LinkedList<>();

    final List<Edge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    final List<Node> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    final void addNode(Node node) {
        checkNull(node, "Node");
        if (nodes.contains(node)) {
            throw new IllegalArgumentException("Duplicate node:" + node);
        }
        nodes.add(node);
    }

    final void addEdge(Node from, Node to) {
        checkNull(from, "From");
        checkNull(to, "To");

        if (!nodes.contains(from) || !nodes.contains(to)) {
            throw new IllegalArgumentException("Node not found, should add to graph first");
        }

        Edge edge = new Edge(from, to);
        if (edges.contains(edge)) {
            throw new IllegalArgumentException("Duplicate edge:" + edge);
        }

        edges.add(edge);
    }

    final boolean containsNode(Node node) {
        return nodes.contains(node);
    }

    //Kahn's algorithm
    final LinkedList<Node> topologicalSorting() {
        //shadow copy this dag's edges and elements
        LinkedList<Node> zeroInDegreeNodes = new LinkedList<>(nodes);
        LinkedList<Edge> edgeList = new LinkedList<>(edges);
        LinkedList<Node> sortedNodes = new LinkedList<>();

        for (Node node : nodes) {
            for (Edge edge : edgeList) {
                if (edge.to.equals(node)) {
                    zeroInDegreeNodes.remove(node);
                }
            }
        }

        while (!zeroInDegreeNodes.isEmpty()) {
            Node zeroInNode = zeroInDegreeNodes.pop();
            sortedNodes.add(zeroInNode);

            for (int i = edgeList.size() - 1; i >= 0; --i) {
                Edge edge = edgeList.get(i);

                if (edge.from.equals(zeroInNode)) {
                    Node n = edge.to;
                    edgeList.remove(i);

                    //If n has no incoming edge
                    boolean incoming = false;
                    for (Edge e : edgeList) {
                        if (e.to.equals(n)) {
                            incoming = true;
                            break;
                        }
//                        incoming |= edge_.to.equals(n);
                    }
                    if (!incoming) {
                        zeroInDegreeNodes.add(n);
                    }
                }
            }
        }

        if (!edgeList.isEmpty()) {
            throw new RuntimeException("Cycle dependency!");
        }

        return sortedNodes;
    }

    private final class Edge {
        Node from, to;

        public Edge(Node from, Node to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return "Edge{" +
                    "from=" + from +
                    ", to=" + to +
                    '}';
        }

        @Override
        public int hashCode() {
            int result = from.hashCode();
            result = 31 * result + to.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Edge that = (Edge) o;
            return this.from.equals(that.from) && this.to.equals(that.to);
        }
    }
}
