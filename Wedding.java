import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

public class Wedding {

  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    int numCities = in.nextInt();
    int numTrainRoutes = in.nextInt();
    int capitalId = in.nextInt()-1;

    Graph graph = new Graph(numCities);

    for (int i = 0; i < numTrainRoutes; i++) {
      int src = in.nextInt();
      int dest = in.nextInt();
      int dur = in.nextInt();

      // we have to reverse all edges, beacuse capital will be our source
      graph.addEdge(dest-1, src-1, dur);
    }

    int[] distances = dijkstra(numCities, graph, capitalId);

    for (int i = 0; i < distances.length; i++) {
      System.out.print(distances[i] + " ");
    }
  }

  public static int[] dijkstra(int numCities, Graph graph, int orig) {
    int[] distances = new int[numCities];

    MinHeap heap = new MinHeap();
    heap.setSize(numCities);

    for (int i = 0; i < numCities; i++) {
      distances[i] = Integer.MAX_VALUE;
      heap.addNode(heap.newNode(i, distances[i]));
      heap.addPosition(i);
    }

    heap.setPosition(orig, orig);
    distances[orig] = 0;
    heap.relax(orig, distances[orig]);

    while (!heap.empty()) {
      int[] city = heap.popMin();
      int cityId = city[0];

      ArrayList<int[]> neighbors = graph.getDestNodes(cityId);

      if (neighbors.size() > 0) {
        for (int[] neighbor : graph.getDestNodes(cityId)) {
          if (neighbor != null) {
            int neighborId = neighbor[0];
            int distToNeighbor = neighbor[1];

            int newDist;
            if (distToNeighbor >= Integer.MAX_VALUE || distances[cityId] >= Integer.MAX_VALUE
                || distToNeighbor + distances[cityId] >= Integer.MAX_VALUE) {
              newDist = Integer.MAX_VALUE;
            } else {
              newDist = distToNeighbor + distances[cityId];
            }

            int currDist = distances[neighborId];

            if (newDist < currDist) {
              distances[neighborId] = newDist;
              heap.relax(neighborId, newDist);
            }
          }
        }
      }
    }

    for (int i = 0; i < distances.length; i++) {
      if (distances[i] == Integer.MAX_VALUE) {
        distances[i] = -1;
      }
    }

    return distances;
  }
}

class MinHeap {

  ArrayList<int[]> nodes;
  int size;
  ArrayList<Integer> positions;

  public MinHeap() {
    this.size = 0;
    this.nodes = new ArrayList<int[]>();
    this.positions = new ArrayList<Integer>();
  }

  public int[] newNode(int id, int weight) {
    int[] t = {id, weight};

    return t;
  }

  public void swap_nodes(int x, int y) {
    int[] temp = this.nodes.get(x);
    this.nodes.set(x, this.nodes.get(y));
    this.nodes.set(y, temp);
  }

  public void heapify(int id) {
    int smallest = id;
    int left = 2 * id + 1;
    int right = 2 * id + 2;

    if (left < this.size && this.nodes.get(left)[1] < this.nodes.get(smallest)[1]) {
      smallest = left;
    }

    if (right < this.size && this.nodes.get(right)[1] < this.nodes.get(smallest)[1]) {
      smallest = right;
    }

    if (smallest != id) {
      this.positions.set(this.nodes.get(smallest)[0], id);
      this.positions.set(this.nodes.get(id)[0], smallest);

      this.swap_nodes(smallest, id);

      this.heapify(smallest);
    }
  }

  public int[] popMin() {
    if (this.size == 0) {
      return null;
    }

    int[] root = this.nodes.get(0);

    int[] lastNode = this.nodes.get(this.size - 1);
    this.nodes.set(0, lastNode);
    this.positions.set(lastNode[0], 0);
    this.positions.set(root[0], this.size - 1);
    this.size -= 1;
    this.heapify(0);

    return root;
  }

  public void relax(int id, int weight) {
    int i = this.positions.get(id);
    this.nodes.get(i)[1] = weight;

    while (i > 0 && this.nodes.get(i)[1] < this.nodes.get((i - 1) / 2)[1]) {
      this.positions.set(this.nodes.get(i)[0], (i - 1) / 2);
      this.positions.set(this.nodes.get((i - 1) / 2)[0], i);
      this.swap_nodes(i, (i - 1) / 2);

      i = (i - 1) / 2;
    }
  }

  public void setSize(int size) {
    this.size = size;
  }

  public void addNode(int[] node) {
    this.nodes.add(node);
  }

  public void setPosition(int i, int p) {
    this.positions.set(i, p);
  }

  public void addPosition(int p) {
    this.positions.add(p);
  }

  public boolean empty() {
    return this.size == 0;
  }
}

class Graph {
  TreeMap<Integer, ArrayList<int[]>> nodes;

  public Graph(int numNodes) {
    nodes = new TreeMap<Integer, ArrayList<int[]>>();
  }

  public void addEdge(int src, int dest, int weight) {
    int[] temp = {dest, weight};
    if (!this.nodes.containsKey(src)) {
      ArrayList<int[]> n = new ArrayList<int[]>();
      n.add(temp);
      this.nodes.put(src, n);
    } else {
      ArrayList<int[]> t = this.nodes.get(src);
      t.add(temp);
      this.nodes.put(src, t);
    }
  }

  public ArrayList<int[]> getDestNodes(int src) {
    ArrayList<int[]> t = this.nodes.get(src);

    if (t == null || t.size() == 0) {
      return new ArrayList<int[]>();
    } else {
      return t;
    }
  }
}
