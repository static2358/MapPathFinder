package up.MainApp;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Classe representant un graphe pondere pour les algorithmes de plus court chemin.
 */
public class WeightedGraph {

    /**
     * Classe representant une arete du graphe avec source, destination et poids.
     */
    static class Edge {
        int source;
        int destination;
        double weight;

        /**
         * Constructeur d'une arete.
         * @param source sommet source
         * @param destination sommet destination
         * @param weight poids de l'arete
         */
        public Edge(int source, int destination, double weight) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }
    }

    /**
     * Classe representant un sommet du graphe.
     * Contient les informations necessaires pour Dijkstra et A*.
     */
    static class Vertex {
        /** Cout individuel du sommet (type de terrain) */
        double indivTime;
        /** Distance depuis la source (initialisee a +infini) */
        double timeFromSource;
        /** Valeur heuristique pour A* */
        double heuristic;
        /** Sommet precedent dans le plus court chemin */
        Vertex prev;
        /** Liste des aretes adjacentes */
        LinkedList<Edge> adjacencylist;
        /** Numero du sommet */
        int num;

        /**
         * Constructeur d'un sommet.
         * @param num numero du sommet
         * @param indivTime cout individuel du sommet
         */
        public Vertex(int num, double indivTime) {
            this.indivTime = indivTime;
            this.timeFromSource = Double.POSITIVE_INFINITY;
            this.heuristic = 0;
            this.prev = null;
            this.adjacencylist = new LinkedList<>();
            this.num = num;
        }
    }

    /**
     * Classe representant le graphe complet.
     * Contient la liste des sommets et les methodes pour construire le graphe.
     */
    static class Graph {
        /** Liste de tous les sommets */
        ArrayList<Vertex> vertexlist;
        /** Nombre de sommets */
        int num_v;

        /**
         * Constructeur du graphe.
         */
        Graph() {
            this.num_v = 0;
            vertexlist = new ArrayList<>();
        }

        /**
         * Ajoute un sommet au graphe.
         * @param indivTime cout du terrain
         */
        public void addVertex(double indivTime) {
            Vertex v = new Vertex(this.num_v, indivTime);
            this.vertexlist.add(v);
            this.num_v++;
        }

        /**
         * Ajoute une arete au graphe.
         * @param source sommet source
         * @param destination sommet destination
         * @param weight poids de l'arete
         */
        public void addEgde(int source, int destination, double weight) {
            Edge edge = new Edge(source, destination, weight);
            vertexlist.get(source).adjacencylist.addFirst(edge);
        }
    }
}
