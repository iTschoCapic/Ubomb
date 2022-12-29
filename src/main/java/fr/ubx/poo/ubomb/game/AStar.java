package fr.ubx.poo.ubomb.game;

import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Grid;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.decor.*;
import fr.ubx.poo.ubomb.go.character.Monster;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AStar {

	private Game game;
	public List<Node> nodes;
	public Node goalNode;

	public AStar(Game game, Monster monster){
		this.game = game;
		this.nodes = convert(game.grid(), monster);
	}

	public static class Node {
		// coordonnées du noeud
		private int x;
		private int y;
		// liste des noeuds voisins
		private List<Node> neighbors = new ArrayList<>();
		private Node goalNode;

		public Node(int x, int y, Node goalNode) {
			this.x = x;
			this.y = y;
			this.goalNode = goalNode;
		}

		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public Node(Position position) {
			this(position.getX(), position.getY());
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public Position getPosition(){
			return new Position(x, y);
		}

		public List<Node> getNeighbors() {
			return neighbors;
		}

		public void setNeighbors(List<Node> neighbors) {
			this.neighbors = neighbors;
		}

		// distance euclidienne entre ce noeud et un autre noeud donné
		public int distanceTo(Node other) {
			int dx = this.x - other.x;
			int dy = this.y - other.y;
			return (int) Math.sqrt(dx * dx + dy * dy);
		}

		// distance estimée entre ce noeud et le noeud objectif (heuristique)
		public int estimatedDistanceToGoal(Node goalNode) {
			// par exemple, on peut utiliser la distance euclidienne comme heuristique
			this.goalNode = goalNode;
			return distanceTo(this.goalNode);
		}
	}

		public static List<Node> convert(Grid grid, Monster monster) {
			List<Node> nodes = new ArrayList<>();
			for (int y = 0; y < grid.height(); y++) {
				for (int x = 0; x < grid.width(); x++) {
					Node node = new Node(x, y);
					nodes.add(node);
				}
			}
			for (int y = 0; y < grid.height(); y++) {
				for (int x = 0; x < grid.width(); x++) {
					Node node = getNodeAt(nodes, x, y);
					if (grid.get(new Position(x, y)) == null || grid.get(new Position(x, y)).walkableBy(monster)){
						// si le noeud de droite existe, on le relie au noeud courant
						if (x < grid.width() - 1) {
							Node rightNode = getNodeAt(nodes, x + 1, y);
							node.getNeighbors().add(rightNode);
						}
						// si le noeud de gauche existe, on le relie au noeud courant
						if (x > 0) {
							Node leftNode = getNodeAt(nodes, x - 1, y);
							node.getNeighbors().add(leftNode);
						}
						// si le noeud du dessous existe, on le relie au noeud courant
						if (y < grid.height() - 1) {
							Node bottomNode = getNodeAt(nodes, x, y + 1);
							node.getNeighbors().add(bottomNode);
						}
						// si le noeud du dessus existe, on le relie au noeud courant
						if (y > 0) {
							Node topNode = getNodeAt(nodes, x, y - 1);
							node.getNeighbors().add(topNode);
						}
					}
				}
			}
			return nodes;
		}

		public static Node getNodeAt(List<Node> nodes, int x, int y) {
			for (Node node : nodes) {
				if (node.getX() == x && node.getY() == y) {
					return node;
				}
			}
			return null;
		}

    public static List<Node> findPath(Node start, Node goal) {
        // liste des noeuds ouverts
        List<Node> openList = new ArrayList<>();
        // liste des noeuds fermés
        List<Node> closedList = new ArrayList<>();
        // map pour retrouver le noeud précédent à partir d'un noeud donné
        Map<Node, Node> cameFrom = new HashMap<>();
        // map pour stocker le coût total pour arriver à chaque noeud (g)
        Map<Node, Integer> gScore = new HashMap<>();
        // map pour stocker le coût estimé pour arriver au noeud objectif (f)
        Map<Node, Integer> fScore = new HashMap<>();

        // ajout du noeud de départ à la liste des noeuds ouverts et initialisation de ses scores g et f
        openList.add(start);
        gScore.put(start, 0);
        fScore.put(start, start.estimatedDistanceToGoal(goal));

        while (!openList.isEmpty()) {
            // on récupère le noeud avec le fScore le plus faible
            Node current = getLowestFScoreNode(openList, fScore);
            // si le noeud courant est le noeud objectif, on a trouvé le chemin le plus court
			//System.out.println(new Position(current.getX(), current.getY()));
			//System.out.println(new Position(getLowestFScoreNode(openList, fScore).getX(), getLowestFScoreNode(openList, fScore).getY()));
            if (current.equals(goal)) {
                return reconstructPath(cameFrom, goal);
            }
            // on retire le noeud courant de la liste des noeuds ouverts et on l'ajoute à la liste des noeuds fermés
            openList.remove(current);
            closedList.add(current);
            // pour chaque noeud voisin du noeud courant
            for (Node neighbor : current.getNeighbors()) {
                // si le noeud voisin est déjà dans la liste des noeuds fermés, on passe au suivant
                if (closedList.contains(neighbor)) {
                    continue;
                }
                // calcul du coût g pour arriver au noeud voisin en passant par le noeud courant
                int tentativeGScore = gScore.get(current) + current.distanceTo(neighbor);
                // si le noeud voisin n'est pas encore dans la liste des noeuds ouverts ou si le nouveau coût g est inférieur au précédent,
                // on met à jour le coût g et le noeud précédent pour ce noeud voisin
                if (!openList.contains(neighbor) || tentativeGScore < gScore.get(neighbor)) {
					cameFrom.put(neighbor, current);
					gScore.put(neighbor, tentativeGScore);
					fScore.put(neighbor, gScore.get(neighbor) + neighbor.estimatedDistanceToGoal(goal));
					if (!openList.contains(neighbor)) {
						openList.add(neighbor);
					}
                }
            }
        }
        // si on sort de la boucle while, cela signifie qu'il n'y a pas de chemin possible entre le noeud de départ et le noeud objectif
        return null;
    }

    private static Node getLowestFScoreNode(List<Node> nodes, Map<Node, Integer> fScore) {
        Node lowestNode = null;
        int lowestScore = Integer.MAX_VALUE;
        for (Node node : nodes) {
            int nodeScore = fScore.get(node);
            if (nodeScore < lowestScore) {
                lowestScore = nodeScore;
                lowestNode = node;
            }
        }
        return lowestNode;
    }

    private static List<Node> reconstructPath(Map<Node, Node> cameFrom, Node current) {
        List<Node> path = new ArrayList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current);
        }
        // on inverse la liste pour avoir le chemin dans l'ordre allant du départ au but
        Collections.reverse(path);
        return path;
    }
}