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
		private int x;
		private int y;
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

		public int distanceTo(Node other) {
			int dx = this.x - other.x;
			int dy = this.y - other.y;
			return (int) Math.sqrt(dx * dx + dy * dy);
		}

		public int estimatedDistanceToGoal(Node goalNode) {
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
						if (x < grid.width() - 1) {
							Node rightNode = getNodeAt(nodes, x + 1, y);
							node.getNeighbors().add(rightNode);
						}
						if (x > 0) {
							Node leftNode = getNodeAt(nodes, x - 1, y);
							node.getNeighbors().add(leftNode);
						}
						if (y < grid.height() - 1) {
							Node bottomNode = getNodeAt(nodes, x, y + 1);
							node.getNeighbors().add(bottomNode);
						}
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
        List<Node> openList = new ArrayList<>();
        List<Node> closedList = new ArrayList<>();
        Map<Node, Node> cameFrom = new HashMap<>();
        Map<Node, Integer> gScore = new HashMap<>();
        Map<Node, Integer> fScore = new HashMap<>();

        openList.add(start);
        gScore.put(start, 0);
        fScore.put(start, start.estimatedDistanceToGoal(goal));

        while (!openList.isEmpty()) {
            Node current = getLowestFScoreNode(openList, fScore);
            if (current.equals(goal)) {
                return reconstructPath(cameFrom, goal);
            }
            openList.remove(current);
            closedList.add(current);
            for (Node neighbor : current.getNeighbors()) {
                if (closedList.contains(neighbor)) {
                    continue;
                }
                int tentativeGScore = gScore.get(current) + current.distanceTo(neighbor);
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
        Collections.reverse(path);
        return path;
    }
}