package up.MainApp;

import up.MainApp.WeightedGraph.Graph;
import up.MainApp.WeightedGraph.Vertex;
import up.MainApp.WeightedGraph.Edge;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Composant graphique pour l'affichage de la carte et du chemin.
 * Herite de JComponent pour permettre le dessin personnalise.
 */
class Board extends JComponent {
    private static final long serialVersionUID = 1L;
    Graph graph;
    int pixelSize;
    int ncols;
    int nlines;
    HashMap<Integer, String> colors;
    int start;
    int end;
    double max_distance;
    int current;
    LinkedList<Integer> path;

    /**
     * Constructeur du composant d'affichage.
     * @param graph le graphe representant la carte
     * @param pixelSize taille d'une case en pixels
     * @param ncols nombre de colonnes de la grille
     * @param nlines nombre de lignes de la grille
     * @param colors correspondance entre types de terrain et couleurs
     * @param start indice du sommet de depart
     * @param end indice du sommet d'arrivee
     */
    public Board(Graph graph, int pixelSize, int ncols, int nlines, HashMap<Integer, String> colors, int start, int end) {
        super();
        this.graph = graph;
        this.pixelSize = pixelSize;
        this.ncols = ncols;
        this.nlines = nlines;
        this.colors = colors;
        this.start = start;
        this.end = end;
        this.max_distance = ncols * nlines;
        this.current = -1;
        this.path = null;
    }

    /**
     * Dessine la carte, les sommets explores et le chemin optimal.
     * @param g contexte graphique fourni par Swing
     */
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.cyan);
        g2.fill(new Rectangle2D.Double(0, 0, this.ncols * this.pixelSize, this.nlines * this.pixelSize));

        int num_case = 0;
        for (Vertex v : this.graph.vertexlist) {
            double type = v.indivTime;
            int i = num_case / this.ncols;
            int j = num_case % this.ncols;

            if (colors.get((int) type).equals("green"))
                g2.setPaint(Color.green);
            if (colors.get((int) type).equals("gray"))
                g2.setPaint(Color.gray);
            if (colors.get((int) type).equals("blue"))
                g2.setPaint(Color.blue);
            if (colors.get((int) type).equals("yellow"))
                g2.setPaint(Color.yellow);
            g2.fill(new Rectangle2D.Double(j * this.pixelSize, i * this.pixelSize, this.pixelSize, this.pixelSize));

            if (num_case == this.current) {
                g2.setPaint(Color.red);
                g2.draw(new Ellipse2D.Double(j * this.pixelSize + this.pixelSize / 2, i * this.pixelSize + this.pixelSize / 2, 6, 6));
            }
            if (num_case == this.start) {
                g2.setPaint(Color.white);
                g2.fill(new Ellipse2D.Double(j * this.pixelSize + this.pixelSize / 2, i * this.pixelSize + this.pixelSize / 2, 4, 4));
            }
            if (num_case == this.end) {
                g2.setPaint(Color.black);
                g2.fill(new Ellipse2D.Double(j * this.pixelSize + this.pixelSize / 2, i * this.pixelSize + this.pixelSize / 2, 4, 4));
            }
            num_case += 1;
        }

        num_case = 0;
        for (Vertex v : this.graph.vertexlist) {
            int i = num_case / this.ncols;
            int j = num_case % this.ncols;
            if (v.timeFromSource < Double.POSITIVE_INFINITY) {
                float g_value = (float) (1 - v.timeFromSource / this.max_distance);
                if (g_value < 0)
                    g_value = 0;
                g2.setPaint(new Color(g_value, g_value, g_value));
                g2.fill(new Ellipse2D.Double(j * this.pixelSize + this.pixelSize / 2, i * this.pixelSize + this.pixelSize / 2, 4, 4));
                Vertex previous = v.prev;
                if (previous != null) {
                    int i2 = previous.num / this.ncols;
                    int j2 = previous.num % this.ncols;
                    g2.setPaint(Color.black);
                    g2.draw(new Line2D.Double(j * this.pixelSize + this.pixelSize / 2, i * this.pixelSize + this.pixelSize / 2, j2 * this.pixelSize + this.pixelSize / 2, i2 * this.pixelSize + this.pixelSize / 2));
                }
            }
            num_case += 1;
        }

        int prev = -1;
        if (this.path != null) {
            g2.setStroke(new BasicStroke(3.0f));
            for (int cur : this.path) {
                if (prev != -1) {
                    g2.setPaint(Color.red);
                    int i = prev / this.ncols;
                    int j = prev % this.ncols;
                    int i2 = cur / this.ncols;
                    int j2 = cur % this.ncols;
                    g2.draw(new Line2D.Double(j * this.pixelSize + this.pixelSize / 2, i * this.pixelSize + this.pixelSize / 2, j2 * this.pixelSize + this.pixelSize / 2, i2 * this.pixelSize + this.pixelSize / 2));
                }
                prev = cur;
            }
        }
    }

    /**
     * Met a jour l'affichage avec le sommet en cours d'exploration.
     * @param graph le graphe mis a jour
     * @param current indice du sommet actuellement explore
     */
    public void update(Graph graph, int current) {
        this.graph = graph;
        this.current = current;
        repaint();
    }

    /**
     * Ajoute le chemin optimal a afficher.
     * @param graph le graphe final
     * @param path liste des indices des sommets du chemin
     */
    public void addPath(Graph graph, LinkedList<Integer> path) {
        this.graph = graph;
        this.path = path;
        this.current = -1;
        repaint();
    }
}

/**
 * Classe principale de l'application PathFinder.
 * Charge une carte depuis un fichier, construit le graphe pondere,
 * et execute l'algorithme de plus court chemin choisi (Dijkstra ou A*).
 * 
 */
public class App {

    /**
     * Types d'heuristiques disponibles pour l'algorithme A*.
     * EUCLIDEAN : distance a vol d'oiseau (admissible)
     * MANHATTAN : somme des ecarts (non admissible en 8-connexite)
     * CHEBYSHEV : maximum des ecarts (admissible)
     */
    public enum Heuristic { EUCLIDEAN, MANHATTAN, CHEBYSHEV }

    /**
     * Affiche l'aide du programme.
     */
    public static void printHelp() {
        System.out.println("Usage: java -jar PathFinder.jar [options] <fichier_carte>");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -a, --algorithme <algo>    Algorithme a utiliser: dijkstra ou astar (defaut: dijkstra)");
        System.out.println("  -h, --heuristique <heur>   Heuristique pour A*: euclidean, manhattan, chebyshev (defaut: euclidean)");
        System.out.println("      --help                 Affiche cette aide");
        System.out.println();
        System.out.println("Exemples:");
        System.out.println("  java -jar PathFinder.jar graph.txt");
        System.out.println("  java -jar PathFinder.jar -a dijkstra graph.txt");
        System.out.println("  java -jar PathFinder.jar -a astar -h manhattan graph.txt");
        System.out.println("  java -jar PathFinder.jar --algorithme astar --heuristique chebyshev maze.txt");
    }

    /**
     * Initialise et affiche la fenetre graphique.
     * @param board composant d'affichage de la carte
     * @param nlines nombre de lignes de la grille
     * @param ncols nombre de colonnes de la grille
     * @param pixelSize taille d'une case en pixels
     */
    private static void drawBoard(Board board, int nlines, int ncols, int pixelSize) {
        JFrame window = new JFrame("Plus court chemin");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(0, 0, ncols * pixelSize + 20, nlines * pixelSize + 40);
        window.getContentPane().add(board);
        window.setVisible(true);
    }

    /**
     * Algorithme de Dijkstra pour trouver le plus court chemin.
     * Explore les sommets par ordre croissant de distance depuis la source.
     * Garantit l'optimalite du chemin trouve.
     * 
     * @param graph le graphe pondere
     * @param start indice du sommet de depart
     * @param end indice du sommet d'arrivee
     * @param ncols nombre de colonnes (pour l'affichage)
     * @param numberV nombre total de sommets
     * @param board composant d'affichage pour la visualisation
     * @return liste des indices des sommets du chemin optimal
     */
    private static LinkedList<Integer> Dijkstra(Graph graph, int start, int end, int ncols, int numberV, Board board) {
        graph.vertexlist.get(start).timeFromSource = 0;
        int number_tries = 0;

        // File de priorite triee par timeFromSource
        PriorityQueue<Vertex> pq = new PriorityQueue<>(
            Comparator.comparingDouble(v -> v.timeFromSource)
        );
        HashSet<Integer> visited = new HashSet<>();
        pq.add(graph.vertexlist.get(start));

        while (!pq.isEmpty()) {
            Vertex current = pq.poll();

            if (visited.contains(current.num)) {
                continue;
            }
            visited.add(current.num);
            number_tries++;

            // Arret si destination atteinte
            if (current.num == end) {
                break;
            }

            // Relaxation des voisins
            for (Edge edge : current.adjacencylist) {
                Vertex neighbor = graph.vertexlist.get(edge.destination);
                double newDist = current.timeFromSource + edge.weight;

                if (newDist < neighbor.timeFromSource) {
                    neighbor.timeFromSource = newDist;
                    neighbor.prev = current;
                    pq.add(neighbor);
                }
            }

            // Mise a jour de l'affichage
            try {
                board.update(graph, current.num);
                Thread.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("stop");
            }
        }

        System.out.println("Done! Using Dijkstra:");
        System.out.println("    Number of nodes explored: " + number_tries);
        System.out.println("    Total time of the path: " + graph.vertexlist.get(end).timeFromSource);

        // Reconstruction du chemin
        LinkedList<Integer> path = new LinkedList<>();
        Vertex current = graph.vertexlist.get(end);
        while (current != null) {
            path.addFirst(current.num);
            current = current.prev;
        }

        board.addPath(graph, path);
        return path;
    }

    /**
     * Algorithme A* pour trouver le plus court chemin.
     * Utilise une heuristique pour guider l'exploration vers la destination.
     * Optimal si l'heuristique est admissible (Euclidean ou Chebyshev).
     * 
     * @param graph le graphe pondere
     * @param start indice du sommet de depart
     * @param end indice du sommet d'arrivee
     * @param ncols nombre de colonnes (pour l'affichage)
     * @param numberV nombre total de sommets
     * @param board composant d'affichage pour la visualisation
     * @param heuristicType type d'heuristique a utiliser
     * @return liste des indices des sommets du chemin trouve
     */
    private static LinkedList<Integer> AStar(Graph graph, int start, int end, int ncols, int numberV, Board board, Heuristic heuristicType) {
        graph.vertexlist.get(start).timeFromSource = 0;
        int number_tries = 0;
        int endLine = end / ncols;
        int endCol = end % ncols;

        // Calcul de l'heuristique pour tous les sommets
        for (int i = 0; i < numberV; i++) {
            int currentLine = i / ncols;
            int currentCol = i % ncols;
            double h;
            switch (heuristicType) {
                case MANHATTAN:
                    h = Math.abs(currentLine - endLine) + Math.abs(currentCol - endCol);
                    break;
                case CHEBYSHEV:
                    h = Math.max(Math.abs(currentLine - endLine), Math.abs(currentCol - endCol));
                    break;
                case EUCLIDEAN:
                default:
                    h = Math.sqrt(Math.pow(currentLine - endLine, 2) + Math.pow(currentCol - endCol, 2));
                    break;
            }
            graph.vertexlist.get(i).heuristic = h;
        }

        // File de priorite triee par f(n) = g(n) + h(n)
        PriorityQueue<Vertex> pq = new PriorityQueue<>(
            Comparator.comparingDouble(v -> v.timeFromSource + v.heuristic)
        );
        HashSet<Integer> visited = new HashSet<>();
        pq.add(graph.vertexlist.get(start));

        while (!pq.isEmpty()) {
            Vertex current = pq.poll();

            if (visited.contains(current.num)) {
                continue;
            }
            visited.add(current.num);
            number_tries++;

            // Arret si destination atteinte
            if (current.num == end) {
                break;
            }

            // Relaxation des voisins
            for (Edge edge : current.adjacencylist) {
                Vertex neighbor = graph.vertexlist.get(edge.destination);
                double newDist = current.timeFromSource + edge.weight;

                if (newDist < neighbor.timeFromSource) {
                    neighbor.timeFromSource = newDist;
                    neighbor.prev = current;
                    pq.add(neighbor);
                }
            }

            // Mise a jour de l'affichage
            try {
                board.update(graph, current.num);
                Thread.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("stop");
            }
        }

        System.out.println("Done! Using A* with " + heuristicType + " heuristic:");
        System.out.println("    Number of nodes explored: " + number_tries);
        System.out.println("    Total time of the path: " + graph.vertexlist.get(end).timeFromSource);

        // Reconstruction du chemin
        LinkedList<Integer> path = new LinkedList<>();
        Vertex current = graph.vertexlist.get(end);
        while (current != null) {
            path.addFirst(current.num);
            current = current.prev;
        }

        board.addPath(graph, path);
        return path;
    }

    /**
     * Point d'entree du programme.
     * Parse les arguments, charge la carte, execute l'algorithme choisi
     * et affiche le resultat graphiquement.
     * 
     * @param args arguments de la ligne de commande
     *             -a/--algorithme : dijkstra ou astar
     *             -h/--heuristique : euclidean, manhattan ou chebyshev
     *             --help : affiche l'aide
     *             dernier argument : chemin du fichier carte
     */
    public static void main(String[] args) {
        // Valeurs par defaut
        String filename = null;
        String algorithme = "dijkstra";
        Heuristic heuristic = Heuristic.EUCLIDEAN;

        // Parsing des arguments
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.equals("--help")) {
                printHelp();
                return;
            } else if (arg.equals("-a") || arg.equals("--algorithme")) {
                if (i + 1 >= args.length) {
                    System.err.println("Erreur: l'option " + arg + " necessite une valeur.");
                    printHelp();
                    System.exit(1);
                }
                algorithme = args[++i].toLowerCase();
                if (!algorithme.equals("dijkstra") && !algorithme.equals("astar")) {
                    System.err.println("Erreur: algorithme invalide '" + algorithme + "'. Valeurs acceptees: dijkstra, astar");
                    printHelp();
                    System.exit(1);
                }
            } else if (arg.equals("-h") || arg.equals("--heuristique")) {
                if (i + 1 >= args.length) {
                    System.err.println("Erreur: l'option " + arg + " necessite une valeur.");
                    printHelp();
                    System.exit(1);
                }
                String heuristiqueStr = args[++i].toLowerCase();
                switch (heuristiqueStr) {
                    case "euclidean":
                    case "euclidienne":
                        heuristic = Heuristic.EUCLIDEAN;
                        break;
                    case "manhattan":
                        heuristic = Heuristic.MANHATTAN;
                        break;
                    case "chebyshev":
                        heuristic = Heuristic.CHEBYSHEV;
                        break;
                    default:
                        System.err.println("Erreur: heuristique invalide '" + heuristiqueStr + "'. Valeurs acceptees: euclidean, manhattan, chebyshev");
                        printHelp();
                        System.exit(1);
                }
            } else if (arg.startsWith("-")) {
                System.err.println("Erreur: option inconnue '" + arg + "'");
                printHelp();
                System.exit(1);
            } else {
                if (filename != null) {
                    System.err.println("Erreur: plusieurs fichiers specifies.");
                    printHelp();
                    System.exit(1);
                }
                filename = arg;
            }
        }

        if (filename == null) {
            System.err.println("Erreur: aucun fichier de carte specifie.");
            printHelp();
            System.exit(1);
        }

        // Lecture de la carte et creation du graphe 
        try {
            File myObj = new File(filename);
            if (!myObj.exists()) {
                System.err.println("Erreur: fichier non trouve '" + filename + "'");
                System.exit(1);
            }
            Scanner myReader = new Scanner(myObj);
            String data = "";
            
            // On ignore les deux premieres lignes
            for (int i = 0; i < 3; i++)
                data = myReader.nextLine();

            // Lecture du nombre de lignes
            int nlines = Integer.parseInt(data.split("=")[1]);
            // Et du nombre de colonnes
            data = myReader.nextLine();
            int ncols = Integer.parseInt(data.split("=")[1]);

            // Initialisation du graphe
            Graph graph = new Graph();

            HashMap<String, Integer> groundTypes = new HashMap<>();
            HashMap<Integer, String> groundColor = new HashMap<>();
            data = myReader.nextLine();
            data = myReader.nextLine();
            
            // Lire les differents types de cases
            while (!data.equals("==Graph==")) {
                String name = data.split("=")[0];
                int time = Integer.parseInt(data.split("=")[1]);
                data = myReader.nextLine();
                String color = data;
                groundTypes.put(name, time);
                groundColor.put(time, color);
                data = myReader.nextLine();
            }

            // On ajoute les sommets dans le graphe (avec le bon type)
            for (int line = 0; line < nlines; line++) {
                data = myReader.nextLine();
                for (int col = 0; col < ncols; col++) {
                    graph.addVertex(groundTypes.get(String.valueOf(data.charAt(col))));
                }
            }

            // Ajout des aretes (8 voisins)
            for (int line = 0; line < nlines; line++) {
                for (int col = 0; col < ncols; col++) {
                    int source = line * ncols + col;
                    int dest;
                    double weight;

                    // Voisin haut-gauche (diagonale)
                    if (line > 0 && col > 0) {
                        dest = (line - 1) * ncols + col - 1;
                        weight = (graph.vertexlist.get(source).indivTime + graph.vertexlist.get(dest).indivTime) / 2.0 * Math.sqrt(2);
                        graph.addEgde(source, dest, weight);
                    }
                    // Voisin haut
                    if (line > 0) {
                        dest = (line - 1) * ncols + col;
                        weight = (graph.vertexlist.get(source).indivTime + graph.vertexlist.get(dest).indivTime) / 2.0;
                        graph.addEgde(source, dest, weight);
                    }
                    // Voisin haut-droite (diagonale)
                    if (line > 0 && col < ncols - 1) {
                        dest = (line - 1) * ncols + col + 1;
                        weight = (graph.vertexlist.get(source).indivTime + graph.vertexlist.get(dest).indivTime) / 2.0 * Math.sqrt(2);
                        graph.addEgde(source, dest, weight);
                    }
                    // Voisin gauche
                    if (col > 0) {
                        dest = line * ncols + col - 1;
                        weight = (graph.vertexlist.get(source).indivTime + graph.vertexlist.get(dest).indivTime) / 2.0;
                        graph.addEgde(source, dest, weight);
                    }
                    // Voisin droite
                    if (col < ncols - 1) {
                        dest = line * ncols + col + 1;
                        weight = (graph.vertexlist.get(source).indivTime + graph.vertexlist.get(dest).indivTime) / 2.0;
                        graph.addEgde(source, dest, weight);
                    }
                    // Voisin bas-gauche (diagonale)
                    if (line < nlines - 1 && col > 0) {
                        dest = (line + 1) * ncols + col - 1;
                        weight = (graph.vertexlist.get(source).indivTime + graph.vertexlist.get(dest).indivTime) / 2.0 * Math.sqrt(2);
                        graph.addEgde(source, dest, weight);
                    }
                    // Voisin bas
                    if (line < nlines - 1) {
                        dest = (line + 1) * ncols + col;
                        weight = (graph.vertexlist.get(source).indivTime + graph.vertexlist.get(dest).indivTime) / 2.0;
                        graph.addEgde(source, dest, weight);
                    }
                    // Voisin bas-droite (diagonale)
                    if (line < nlines - 1 && col < ncols - 1) {
                        dest = (line + 1) * ncols + col + 1;
                        weight = (graph.vertexlist.get(source).indivTime + graph.vertexlist.get(dest).indivTime) / 2.0 * Math.sqrt(2);
                        graph.addEgde(source, dest, weight);
                    }
                }
            }

            // On obtient les noeuds de depart et d'arrivee
            data = myReader.nextLine();
            data = myReader.nextLine();
            int startV = Integer.parseInt(data.split("=")[1].split(",")[0]) * ncols + Integer.parseInt(data.split("=")[1].split(",")[1]);
            data = myReader.nextLine();
            int endV = Integer.parseInt(data.split("=")[1].split(",")[0]) * ncols + Integer.parseInt(data.split("=")[1].split(",")[1]);

            myReader.close();

            int pixelSize = 10;
            Board board = new Board(graph, pixelSize, ncols, nlines, groundColor, startV, endV);
            drawBoard(board, nlines, ncols, pixelSize);
            board.repaint();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("stop");
            }

            // Appel de l'algorithme choisi
            LinkedList<Integer> path;
            if (algorithme.equals("astar")) {
                path = AStar(graph, startV, endV, ncols, nlines * ncols, board, heuristic);
            } else {
                path = Dijkstra(graph, startV, endV, ncols, nlines * ncols, board);
            }

            // Ecriture du chemin dans un fichier de sortie
            try {
                File file = new File("out.txt");
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);

                for (int i : path) {
                    bw.write(String.valueOf(i));
                    bw.write('\n');
                }
                bw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            System.err.println("Erreur: fichier non trouve.");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
