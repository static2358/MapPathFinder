
# MapPathFinder

> Projet d'Algorithmique S5 2025

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)

---

## Table des matières

1. [Informations Projet](#informations-projet)
2. [Prérequis et Dépendances](#prérequis-et-dépendances)
3. [Installation et Compilation](#installation-et-compilation)
4. [Exécution avec JAR](#exécution-avec-jar)
---

## Informations Projet

Ce projet implémente et compare les algorithmes de plus court chemin **Dijkstra** et **A*** sur des graphes pondérés et des labyrinthes. Il permet d'analyser l'efficacité des différentes heuristiques et de visualiser les résultats sur différents types de cartes.

### Arborescence du Projet

```
Algo_Partie_B/
│
├── src/                                   CODE SOURCE
│   └── up/MainApp/
│       ├── App.java                       Point d'entrée, logique principale
│       └── WeightedGraph.java             Structure de graphe pondéré
│
├── maps/                                  FICHIERS DE CARTES
│
└── README.md                              Ce fichier
```

### Point d'Entrée

La classe principale contenant la méthode `main` est :

```
up.MainApp.App
```

Fichier source : `src/up/MainApp/App.java`

---

## Prérequis et Dépendances

### Java

- **JDK 8** ou supérieur (recommandé : JDK 17+)
- Vérifier l'installation : `java --version`

### Dépendances

- Aucun package externe requis
- Les fichiers de cartes doivent être placés dans le dossier `maps/`

---

## Installation et Compilation

### Compilation

```bash
javac -encoding UTF-8 -d bin src/up/MainApp/*.java
```

### Exécution

```bash
java -cp bin up.MainApp.App
```

---

## Exécution avec JAR

```bash
# Aide
java -jar MapPathFinder.jar --help

# Dijkstra (défaut)
java -jar MapPathFinder.jar graphe.txt

# A* avec heuristique euclidienne
java -jar MapPathFinder.jar -a astar graphe.txt

# A* avec heuristique de Manhattan
java -jar MapPathFinder.jar -a astar -h manhattan graphe.txt

# A* avec heuristique de Chebyshev
java -jar MapPathFinder.jar -a astar -h chebyshev graphe.txt
```

### Sortie

Le programme génère :
- Le chemin trouvé (dans un fichier `out.txt`)
- Le coût total du chemin
- Le temps d'exécution
