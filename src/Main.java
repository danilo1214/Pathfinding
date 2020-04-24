import java.awt.*;
import java.io.File;
import java.util.*;


class Coordinate {
    int x;
    int y;

    Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class AstarObj implements Comparable<AstarObj> {
    Node node;
    int path;
    int heuristic;

    AstarObj(Node n, int p, int h) {
        node = n;
        path = p;
        heuristic = h;
    }

    @Override
    public int compareTo(AstarObj o) {
        if (this.heuristic + this.path > o.heuristic + o.path) {
            return 1;
        } else if (this.heuristic + this.path < o.heuristic + o.path) {
            return -1;
        } else {
            return 0;
        }
    }
}

class Node {
    int value;
    Coordinate coordinate;
    public ArrayList<Coordinate> childrenCords = new ArrayList<Coordinate>();

    public Node(int x, int y, int val, int[][] field) {
        this.coordinate = new Coordinate(x, y);
        this.value = val;

        //left child
        if (x > 0) {
            Coordinate child = new Coordinate(x - 1, y);
            childrenCords.add(child);
        }

        //right child
        if (x < field[0].length - 1) {
            Coordinate child = new Coordinate(x + 1, y);
            childrenCords.add(child);
        }

        // up child
        if (y > 0) {
            Coordinate child = new Coordinate(x, y - 1);
            childrenCords.add(child);
        }

        //bottom child
        if (y < field.length - 1) {
            Coordinate child = new Coordinate(x, y + 1);
            childrenCords.add(child);
        }
    }

    public ArrayList<Node> getChildren(Node[][] nodes) {
        ArrayList<Node> toReturn = new ArrayList<Node>();
        for (int i = 0; i < childrenCords.size(); i++) {
            Node node = nodes[childrenCords.get(i).y][childrenCords.get(i).x];
            if (node.value != -1) { // A wall is not a child
                toReturn.add(node);
            }
        }
        return toReturn;
    }
}

public class Main {

    public static boolean includes(PriorityQueue<AstarObj> pq, Node node) {
        for (AstarObj ob : pq) {
            if (ob.node.coordinate.x == node.coordinate.x && ob.node.coordinate.y == node.coordinate.y) return true;
        }
        return false;
    }

    public static Node getStartNode(Node[][] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[i].length; j++) {
                Node node = nodes[i][j];
                if (node.value == -2) { // You cannot go through a wall
                    return node;
                }
            }
        }
        return null;
    }

    public static AstarObj getAstarObj(int x, int y, PriorityQueue<AstarObj> closed) {
        for (AstarObj ob : closed) {
            if (ob.node.coordinate.x == x && ob.node.coordinate.y == y) return ob;
        }
        return null;
    }

    public static void drawPath(ArrayList<int[]> cords) {
        StdDraw.setPenColor(Color.cyan);
        for (int[] cord : cords) {
            StdDraw.filledSquare(cord[0] + 0.5, cord[1] + 0.5, 0.5);
        }
    }

    public static ArrayList<Node> getEnds(Node[][] nodes) {
        ArrayList<Node> goals = new ArrayList<Node>();
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[i].length; j++) {
                Node node = nodes[i][j];
                if (node.value == -3) {
                    goals.add(node);
                }
            }
        }
        return goals;
    }


    public static int[] FieldInfo(Scanner sc) {
        int[] field = new int[2]; //x and y
        int x = 0;
        int y = 0;
        while (sc.hasNextLine()) {
            if (x == 0) {
                x = sc.nextLine().split(",").length;
                y++;
            } else {
                sc.nextLine();
                y++;
            }
        }
        field[0] = x;
        field[1] = y;
        return field;
    }

    public static int[] convert(String[] items) {
        int[] nums = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            nums[i] = Integer.parseInt(items[i]);
        }
        return nums;
    }

    public static int[][] readFile(File file) throws Exception {
        int[][] field;
        int[] info = FieldInfo(new Scanner(file));
        field = new int[info[0]][info[1]];
        Scanner sc = new Scanner(file);
        for (int i = 0; i < field.length; i++) {
            String nxtLine = sc.nextLine();
            String[] items = nxtLine.split(",");
            int[] nums = convert(items);
            for (int j = 0; j < field.length; j++) {
                field[i][j] = nums[j];
            }
        }
        return field;
    }

    public static void drawMap(int[][] field) {
        int Yunits = field.length;
        int Xunits = field[0].length;
        StdDraw.setXscale(0, Xunits);
        StdDraw.setYscale(0, Yunits);


        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field.length; j++) {
                switch (field[i][j]) {
                    case -1:
                        StdDraw.setPenColor(Color.BLACK);
                        break;
                    case -2:
                        StdDraw.setPenColor(Color.blue);
                        break;
                    case -3:
                        StdDraw.setPenColor(Color.GREEN);
                        break;

                    default:
                        StdDraw.setPenColor(Color.WHITE);
                }
                StdDraw.filledSquare(j + 0.5, i + 0.5, 0.5);
            }
        }
    }

    public static void BFS(Node[][] nodes) {
        int[][][] path = new int[nodes.length][nodes.length][2];
        HashSet<Coordinate> visited = new HashSet<Coordinate>();
        int checked = 0;
        int pathLength = 0;

        Queue<Node> queue = new LinkedList<Node>();
        Node startNode = getStartNode(nodes);
        queue.add(startNode);
        while (!queue.isEmpty()) {
            Node curNode = queue.remove();
            visited.add(curNode.coordinate);

            if (curNode.value == -3) {
                // We found the solution
                System.out.println("checked: " + checked);
                System.out.print("path: " + curNode.value);
                int xCord = curNode.coordinate.x;
                int yCord = curNode.coordinate.y;
                int curVal;
                ArrayList<int[]> cords = new ArrayList<int[]>();
                while (true) {
                    // Looping from end to start of the found path
                    pathLength++;
                    int prevXCord = xCord;
                    xCord = path[yCord][xCord][0];
                    yCord = path[yCord][prevXCord][1];
                    curVal = nodes[yCord][xCord].value;
                    if (curVal == -2) {
                        //we got to the start - we can break the loop
                        break;
                    }
                    System.out.printf(String.format("<-(%d, %d)", xCord, yCord));
                    cords.add(new int[]{xCord, yCord});
                }
                drawPath(cords);
                System.out.printf(String.format("<-(%d, %d)\n", xCord, yCord));
                System.out.println(String.format("path length: %d", pathLength));
                return;
            }

            for (Node nextNode : curNode.getChildren(nodes)) {
                if (visited.contains(nextNode.coordinate)) {
                    // If we already visited this node, then skip
                    continue;
                } else {
                    checked++;
                    queue.add(nextNode);
                    visited.add(nextNode.coordinate);
                    path[nextNode.coordinate.y][nextNode.coordinate.x] = new int[]{curNode.coordinate.x, curNode.coordinate.y};
                    // Add it to the list of visited nodes, and set the neighbor from which we reached it
                }

            }
        }
    }

    public static void DFS(Node[][] nodes) {
        int[][][] path = new int[nodes.length][nodes.length][2];
        int checked = 0;
        int pathLength = 0;

        HashSet<Coordinate> visited = new HashSet<Coordinate>();
        Stack<Node> stack = new Stack<Node>();
        Node start = getStartNode(nodes);
        stack.push(start);

        while (!stack.isEmpty()) {
            Node curNode = stack.peek();
            visited.add(curNode.coordinate);
            if (curNode.value == -3) {
                // Print the path to the beginning
                System.out.println("Checked: " + checked);

                int xCord = curNode.coordinate.x;
                int yCord = curNode.coordinate.y;
                int curVal;

                ArrayList<int[]> cords = new ArrayList<int[]>();
                while (true) {
                    pathLength++;
                    int prevXCord = xCord;
                    xCord = path[yCord][xCord][0];
                    yCord = path[yCord][prevXCord][1];
                    curVal = nodes[yCord][xCord].value;
                    if (curVal == -2) {
                        // We are at the beginning
                        break;
                    }
                    System.out.printf(String.format("<-(%d, %d)", xCord, yCord));
                    cords.add(new int[]{xCord, yCord});
                }

                drawPath(cords);
                System.out.printf(String.format("<-(%d, %d)\n", xCord, yCord));
                System.out.println(String.format("Path length: %d", pathLength));
                return;
            }

            // Find the next unvisited child
            boolean found = false;
            for (int nextNode = 0; nextNode < curNode.getChildren(nodes).size(); nextNode++) {
                if (visited.contains(curNode.getChildren(nodes).get(nextNode).coordinate)) {
                    // skip if it is visited
                    continue;
                } else {
                    Node child = curNode.getChildren(nodes).get(nextNode);

                    visited.add(child.coordinate);
                    stack.push(child); // Add it to the stack

                    path[child.coordinate.y][child.coordinate.x] = new int[]{curNode.coordinate.x, curNode.coordinate.y}; //Set the path from which we got to the child
                    checked++;
                    found = true;
                    break;
                }
            }

            if (!found) {
                stack.pop();
                //We have visited all children of curNode
            }
        }
    }

    public static void As(Node[][] nodes) {
        int visited = 0; // a counter of all visited nodes
        int pathLength = 0; //the length of the path
        int i, j;

        int[][] g = new int[nodes.length][nodes[0].length]; //the path cost
        int[][] h = new int[nodes.length][nodes[0].length]; //the heuristic path cost
        int[][][] pathTo = new int[nodes.length][nodes[0].length][2]; //the coordinates that led to a certain coordinate

        PriorityQueue<AstarObj> open = new PriorityQueue<AstarObj>(); //the open nodes
        PriorityQueue<AstarObj> closed = new PriorityQueue<AstarObj>(); //the closed nodes
        Node start = getStartNode(nodes);

        for (i = 0; i < nodes.length; i++) {
            for (j = 0; j < nodes[i].length; j++) {

                g[i][j] = Integer.MAX_VALUE; //the initial path  cost is set to max
                int minCost = Integer.MAX_VALUE;
                Node node = nodes[i][j];
                ArrayList<Node> ends = node.getChildren(nodes);

                for (int k = 0; k < ends.size(); k++) {

                    Coordinate curCords = node.coordinate;
                    Coordinate endNode = ends.get(k).coordinate;

                    int distance = Math.abs(curCords.x - endNode.x) + Math.abs(curCords.y - endNode.y);
                    if (distance < minCost) {
                        minCost = distance;
                    }

                }
                h[i][j] = minCost; //Initiallizing the heuristic values
            }
        }

        AstarObj curNode = new AstarObj(start, 0, 0);
        open.add(curNode);
        //Start a star on thr first node

        while (!open.isEmpty()) {

            curNode = open.peek();
            open.remove(open.peek());

            if (curNode.node.value == -3) { // We found the goal
                int xCord = curNode.node.coordinate.x;
                int yCord = curNode.node.coordinate.y;
                int curVal;

                ArrayList<int[]> cords = new ArrayList<int[]>();

                System.out.println("Success");
                System.out.printf(String.format("(%d, %d)", xCord, yCord)); //The coordinates where we found the goal
                while (true) {
                    // Print the path from the end to the start - using the pathTo array

                    pathLength++;
                    int prevXCord = xCord;
                    xCord = pathTo[yCord][xCord][0];
                    yCord = pathTo[yCord][prevXCord][1];
                    curVal = nodes[yCord][xCord].value;
                    if (curVal == -2) {
                        // The start node
                        break;
                    }
                    System.out.printf(String.format("<-(%d, %d)", xCord, yCord));
                    cords.add(new int[]{xCord, yCord});
                }
                drawPath(cords);
                System.out.printf(String.format("<-(%d, %d)\n", xCord, yCord));
                System.out.println(String.format("Visited: %d", visited));
                System.out.println(String.format("Path length: %d", pathLength));
                return;
            }
            for (Node succ : curNode.node.getChildren(nodes)) {
                int sucCur = curNode.path + 1;

                if (includes(open, succ)) {
                    // If it is an open node and the current path in g is greater or equal to the path found in the current iteration
                    if (g[succ.coordinate.y][succ.coordinate.x] <= sucCur) continue;
                } else if (includes(closed, succ)) {
                    // If it is a closed node and the current path in g is greater or equal to the path found in the current iteration
                    // Remove it from the closed list,
                    if (g[succ.coordinate.y][succ.coordinate.x] <= sucCur) continue;
                    closed.remove(getAstarObj(succ.coordinate.x, succ.coordinate.y, closed));
                } else {
                    // It has never been opened, neither has it been closed
                    visited++;
                    open.add(new AstarObj(succ, sucCur, h[succ.coordinate.y][succ.coordinate.x]));
                }
                g[succ.coordinate.y][succ.coordinate.x] = sucCur;
                pathTo[succ.coordinate.y][succ.coordinate.x] = new int[]{curNode.node.coordinate.x, curNode.node.coordinate.y};
            }
            closed.add(curNode);
            // Add it to the closed list
        }

    }

    public static void Greedy(Node[][] nodes) {
        int visited = 0;
        int pathLength = 0;

        Stack<Node> queue = new Stack<Node>();
        int[][] h = new int[nodes.length][nodes[0].length];
        int[][][] pathTo = new int[nodes.length][nodes[0].length][2];
        boolean[][] checked = new boolean[nodes.length][nodes[0].length];
        Node current = getStartNode(nodes);
        queue.push(current);
        int i, j;
        for (i = 0; i < nodes.length; i++) {
            for (j = 0; j < nodes[i].length; j++) {
                Node node = nodes[i][j];
                int minCost = Integer.MAX_VALUE;
                ArrayList<Node> ends = node.getChildren(nodes);
                for (int k = 0; k < ends.size(); k++) {
                    Coordinate curCords = node.coordinate;
                    Coordinate endNode = ends.get(k).coordinate;
                    int distance = Math.abs(curCords.x - endNode.x) + Math.abs(curCords.y - endNode.y);
                    if (distance < minCost) {
                        minCost = distance;
                    }
                }
                checked[i][j] = false;
                h[i][j] = minCost;
            }
        }
        checked[current.coordinate.y][current.coordinate.x] = true;
        while (!queue.isEmpty()) {
            Node parent = queue.pop();

            if (parent.value == -3) {
                System.out.println("Success");
                int xCord = parent.coordinate.x;
                int yCord = parent.coordinate.y;
                int curVal;
                ArrayList<int[]> cords = new ArrayList<int[]>();
                System.out.printf(String.format("(%d, %d)", xCord, yCord));
                while (true) {
                    pathLength++;
                    int prevXCord = xCord;
                    xCord = pathTo[yCord][xCord][0];
                    yCord = pathTo[yCord][prevXCord][1];
                    curVal = nodes[yCord][xCord].value;
                    System.out.printf(String.format("<-(%d, %d)", xCord, yCord));
                    if (curVal == -2) {
                        break;
                    }

                    cords.add(new int[]{xCord, yCord});
                }
                drawPath(cords);
                System.out.printf(String.format("<-(%d, %d)\n", xCord, yCord));
                System.out.println(String.format("Poseteni: %d", visited));
                System.out.println(String.format("path length: %d", pathLength));
                return;
            }
            ArrayList<Node> children = parent.getChildren(nodes);
            children.sort(new Comparator<Node>() {
                @Override
                public int compare(Node o1, Node o2) {
                    return h[o1.coordinate.y][o1.coordinate.x] - h[o2.coordinate.y][o2.coordinate.x];
                }
            });
            for (Node child : children) {
                if (checked[child.coordinate.y][child.coordinate.x]) {
                    continue;
                }
                visited++;
                checked[child.coordinate.y][child.coordinate.x] = true;
                pathTo[child.coordinate.y][child.coordinate.x] = new int[]{parent.coordinate.x, parent.coordinate.y};
                queue.push(child);
            }
        }
        return;
    }

    public static Node[][] initNodes(String file){
        StdDraw.setCanvasSize(900, 900);
        File f = new File(file);
        try{
            int[][] field = readFile(f);
            drawMap(field);
            Node[][] nodes = new Node[field.length][field[0].length];

            for (int i = 0; i < nodes.length; i++) {
                for (int j = 0; j < nodes[i].length; j++) {
                    nodes[i][j] = new Node(j, i, field[i][j], field);
                }
            }

            return nodes;
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    public static void main(String[] args) {
        String algorithm = args[1];
        String file = args[0];
        Node[][] nodes = initNodes(file);

        long startTime = System.currentTimeMillis();
        switch (algorithm){
            case "BFS":
                BFS(nodes);
                break;
            case "DFS":
                DFS(nodes);
                break;
            case "As":
                As(nodes);
                break;
            case "Greedy":
                Greedy(nodes);
                break;
            default:
                System.out.println("Please select from DFS BFS Greedy or As");
        }
        long executionTime = System.currentTimeMillis() - startTime;
        System.out.println("Execution time: " + executionTime + "ms");

    }
}
