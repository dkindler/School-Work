import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.lang.Double;
import java.lang.Integer;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.System;
import java.util.Iterator;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Airline {
    public static EdgeWeightedDigraph graph;
    public static String[] cities;
    public static final String NULL_CITY_STRING = "null";
    public static final int NULL_CITY_INT = 0;
    public static ArrayList<DirectedEdge> edges = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Hello. Please enter an airline data file: ");
        Scanner inputScanner = new Scanner(System.in);
        String inputFileName = inputScanner.nextLine();
        readData(inputFileName);

        printMenu();
        int input = getIntInput();

        while (input != 8) {
            switch (input) {
                case 0:
                    showAllRoutes();
                    break;
                case 1:
                    minimumSpanTree();
                    break;
                case 2:
                    menuShortestPath();
                    break;
                case 3:
                    menuShortestPathOnPrice();
                    break;
                case 4:
                    menuShortestPathOnHops();
                    break;
                case 5:
                    menuAllRoutesBelowPrice();
                    break;
                case 6:
                    menuAddRoute();
                    break;
                case 7:
                    menuRemoveRoute();
                    break;
                default:
                    break;
            }

            printMenu();
            input = getIntInput();
        }


        System.out.println("Saving any updates to: " + inputFileName);
        try {
            save(inputFileName);
            //save("test.txt");
        } catch (IOException e) {
            System.out.println("ERROR: " + e);
        }
    }

    /**
     * HELP METHODS
     */
    public static int cityStringToInt(String cityName) {
        for (int i = 1; i < cities.length; i++) {
            String c = cities[i];
            if (c.equalsIgnoreCase(cityName)) return i;
        }

        return NULL_CITY_INT;
    }

    private static int getIntInput() {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            try {
                String s ;
                s = sc.nextLine();
                int i = Integer.parseInt(s);
                return i;
            } catch (java.lang.NumberFormatException e) {
                System.out.println("Please enter an integer.");
            }
        }
        return 9; // we return 9 here as a hack, since the menu accepts input 0-8
    }

    public static void printMenu() {
        System.out.println("\n----------MENU----------");
        System.out.println("[0]\tSHOW ALL ROUTES");
        System.out.println("[1]\tDISPLAY SPANING TREE");
        System.out.println("[2]\tSHORTEST PATH ON MILAGE");
        System.out.println("[3]\tSHORTEST PATH ON PRICE");
        System.out.println("[4]\tSHORTEST PATH ON LAYOVERS");
        System.out.println("[5]\tROUTES LESS THAN PRICE");
        System.out.println("[6]\tADD ROUTE");
        System.out.println("[7]\tREMOVE ROUTE");
        System.out.println("[8]\tSAVE AND QUIT");
    }

    public static void readData(String fileName) throws FileNotFoundException {
        File f = new File(fileName);

        try {
            Scanner fileScanner = new Scanner(f);
            int cityCount = Integer.parseInt(fileScanner.nextLine());

            cities = new String[cityCount + 1];
            cities[NULL_CITY_INT] = NULL_CITY_STRING;
            for (int i = 1; i <= cityCount; i++) {
                cities[i] = fileScanner.nextLine();
            }

            graph = new EdgeWeightedDigraph(cityCount + 1);

            while (fileScanner.hasNext()) {
                String data = fileScanner.nextLine();
                String[] dataArr = data.split(" ");

                int origin = Integer.parseInt(dataArr[0]);
                int destination = Integer.parseInt(dataArr[1]);
                int dist = Integer.parseInt(dataArr[2]);
                double price = Double.parseDouble(dataArr[3]);

                DirectedEdge edge1 = new DirectedEdge(origin, destination, dist, price);
                DirectedEdge edge2 = new DirectedEdge(destination, origin, dist, price);

                edges.add(edge1);
                edges.add(edge2);

                graph.addEdge(edge1);
                graph.addEdge(edge2);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.toString());
        }
    }

    private static String routeToString(DirectedEdge e) {
        return "Cost: $" + e.weightTwo() + " Path: " + cities[e.from()] + " " + e.weight() + " " + cities[e.to()];
    }

    public static DirectedEdge getRoute(int origin, int destination) {
        for (DirectedEdge e : edges) {
            if (origin == e.from() && destination == e.to()) return e;
        }

        return null;
    }


    public static ArrayList<ArrayList<Integer>> getAllPaths(int source, int destination) {
        ArrayList<ArrayList<Integer>> paths = new ArrayList<ArrayList<Integer>>();
        recursive(source, destination, paths, new LinkedHashSet<Integer>());
        return paths;
    }

    private static void recursive (int current, int destination, ArrayList<ArrayList<Integer>> paths, LinkedHashSet<Integer> path) {
        path.add(current);

        if (current == destination) {
            paths.add(new ArrayList<Integer>(path));
            path.remove(current);
            return;
        }

        ArrayList<Integer> edges = new ArrayList<>();
        Iterator<DirectedEdge> e = graph.adj(current).iterator();

        while(e.hasNext())  edges.add(e.next().to());
        for (int t : edges) if (!path.contains(t)) recursive (t, destination, paths, path);

        path.remove(current);
    }


    /**
     * MENU METHODS
     */
    public static void menuShortestPath() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter origin city: ");
        String origin = sc.nextLine();
        System.out.println("Enter destination city: ");
        String destination = sc.nextLine();

        int o = cityStringToInt(origin);
        int d = cityStringToInt(destination);

        System.out.println();
        shortestPath(o, d);
        System.out.println();
    }

    public static void menuShortestPathOnPrice() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter origin city: ");
        String origin = sc.nextLine();
        System.out.println("Enter destination city: ");
        String destination = sc.nextLine();

        int o = cityStringToInt(origin);
        int d = cityStringToInt(destination);

        System.out.println();
        shortestPathOnPrice(o, d);
        System.out.println();
    }

    public static void menuShortestPathOnHops() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter origin city: ");
        String origin = sc.nextLine();
        System.out.println("Enter destination city: ");
        String destination = sc.nextLine();

        int o = cityStringToInt(origin);
        int d = cityStringToInt(destination);

        System.out.println();
        shortestPathByStops(o, d);
        System.out.println();
    }

    public static void menuAllRoutesBelowPrice() {
        System.out.println("Please enter a maximum price: ");
        int p = getIntInput();

        System.out.println();
        routesLessThan(p);
        System.out.println();
    }

    public static void menuAddRoute() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter origin city: ");
        String origin = sc.nextLine();
        System.out.println("Enter destination city: ");
        String destination = sc.nextLine();

        int o = cityStringToInt(origin);
        int d = cityStringToInt(destination);

        System.out.println("Enter distance: ");
        int milage = getIntInput();
        System.out.println("Enter price: ");
        int price = getIntInput();

        System.out.println();
        addFlight(o, d, milage, price);
        System.out.println();
    }

    public static void menuRemoveRoute() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter origin city: ");
        String origin = sc.nextLine();
        System.out.println("Enter destination city: ");
        String destination = sc.nextLine();

        int o = cityStringToInt(origin);
        int d = cityStringToInt(destination);

        System.out.println();
        removeRouteBetween(o, d);
        System.out.println();
    }

    /**
     * REQUIREMENTS
     */
    //REQUIREMENT 1
    public static void showAllRoutes() {
        for (int i = 1; i < cities.length; i++) {
            Iterator<DirectedEdge> e = graph.adj(i).iterator();
            if (e != null) {
                while (e.hasNext()) System.out.println(routeToString(e.next()));
            }
        }

        System.out.println();
    }

    // REQUIREMENT 2
    public static void minimumSpanTree() {
        PrimMST mst = new PrimMST(graph);
        Iterator<DirectedEdge> edges = mst.edges().iterator();

        while (edges.hasNext()) {
            DirectedEdge e = edges.next();
            System.out.println(cities[e.to()] + ", " + cities[e.from()] + " : " + e.weight());
        }

        System.out.println();
    }

    //REQUIREMENT 3.1
    public static void shortestPath(int origin, int destination) {
        if (origin <= 0 || origin >= cities.length) return;
        if (destination <= 0 || destination >= cities.length) return;

        DijkstraSP dsp = new DijkstraSP(graph, origin, true);
        Iterator<DirectedEdge> edges = dsp.pathTo(destination).iterator();
        System.out.println("Shortest disœtance from " + cities[origin] + " to " + cities[destination] + " is " + edges.next().weight());
        System.out.println("Path with edges (in reverse order):");
        String print = "";

        while (edges.hasNext()){
            DirectedEdge e = edges.next();
            print = cities[e.to()]+ " " + e.weight() +" "+  print;
        }

        print += cities[origin];
        System.out.println(print);
    }

    //REQUIREMENT 3.2
    public static void shortestPathOnPrice(int origin, int destination) {
        if (origin <= 0 || origin >= cities.length) return;
        if (destination <= 0 || destination >= cities.length) return;

        DijkstraSP dsp = new DijkstraSP(graph, origin, false);
        Iterator<DirectedEdge> nodes = dsp.pathToTwo(destination).iterator();
        System.out.println("Cheapest from " + cities[origin]+ " to "+cities[destination]+ " is " + (nodes.next().weight()));
        System.out.println("Path with edges (in reverse order):");
        String print = "";

        while (nodes.hasNext()) {
            DirectedEdge e = nodes.next();
            print = cities[e.to()]+ " " + e.weightTwo() + " " + print;
        }

        print += cities[origin];
        System.out.println(print);
    }

    //REQUIREMENT 3.3
    public static void shortestPathByStops(int origin, int destination) {
        if (origin <= 0 || origin >= cities.length) return;
        if (destination <= 0 || destination >= cities.length) return;

        BreadthFirstPaths bfsSearch = new BreadthFirstPaths(graph, origin);
        Iterable<Integer> path = bfsSearch.pathTo(destination);

        String[] secondPath = path.toString().split(" ");
        System.out.println("Least number of flights from " + cities[origin] + " to " + cities[destination] + " is: " + (secondPath.length - 1));
        System.out.println("Path (In reverse order) ");

        for (int i = secondPath.length - 1; i >= 0; i--){
            System.out.print(cities[Integer.parseInt(secondPath[i])] + " ");
        }
    }

    //REQUIREMNET 4
    public static void routesLessThan(int price) {
        ArrayList<StringBuilder> routesToPrint = new ArrayList<>();
        for (int i = 1; i < cities.length; i++) {
            for (int j = 1; j < cities.length; j++) {
                if (j == i) {
                    j++;
                    if (j == cities.length) break;
                }

                ArrayList<ArrayList<Integer>> routesList = getAllPaths(i, j);
                for (int a = 0; a < routesList.size(); a++) {
                    Iterator<Integer> path = routesList.get(a).iterator();

                    int costSum = 0;
                    int origin = path.next();
                    int destination = path.next();

                    StringBuilder outputString = new StringBuilder(cities[origin]);

                    while (true) {
                        Iterator<DirectedEdge> edges = graph.adj(origin).iterator();

                        while (edges.hasNext()) {
                            DirectedEdge edge = edges.next();

                            if(edge.to() == destination) {
                                costSum += edge.weightTwo();
                                outputString.append(" " + (int)edge.weightTwo() + " " + cities[destination]);
                            }
                        }

                        if (!path.hasNext()) break;

                        origin = destination;
                        destination = path.next();
                    }

                    if (costSum <= price) {
                        StringBuilder out = new StringBuilder();
                        out.append("Cost: " + costSum + " Path (reversed): " + outputString.toString() + "\n");
                        if(!routesToPrint.contains(out)) {
                            routesToPrint.add(out);
                            System.out.println(out);
                        }
                    }
                }
            }
        }
    }

    //REQUIREMENT 5
    public static void addFlight(int origin, int destination, int milage, int price) {
        if (origin <= 0 || origin >= cities.length) return;
        if (destination <= 0 || destination >= cities.length) return;

        graph.addEdge(new DirectedEdge(origin, destination, milage, price));
        graph.addEdge(new DirectedEdge(destination, origin, milage, price));
        System.out.println("Added route for " + cities[origin] + ", " + cities[destination]);
    }

    //REQUIREMENT 6
    public static void removeRouteBetween(int origin, int destination) {
        if (origin <= 0 || origin >= cities.length) return;
        if (destination <= 0 || destination >= cities.length) return;

        Iterator<DirectedEdge> edges = graph.adj[origin].iterator();
        Stack<DirectedEdge> edgeStack = new Stack<>();

        while (edges.hasNext()) {
            DirectedEdge e = edges.next();
            if (e.to() == destination) continue;
            edgeStack.push(e);
        }

        graph.adj[origin] = new Bag<>();
        while (!edgeStack.isEmpty()) {
            graph.adj[origin].add(edgeStack.pop());
        }

        edges = graph.adj[destination].iterator();
        edgeStack = new Stack<>();

        while (edges.hasNext()) {
            DirectedEdge e = edges.next();
            if (e.to() == origin) continue;
            edgeStack.push(e);
        }

        graph.adj[destination] = new Bag<>();
        while(!edgeStack.isEmpty()){
            graph.adj[destination].add(edgeStack.pop());
        }
    }

    //REQUIREMENT 7
    public static void save(String fileName) throws IOException {
        File f = new File(fileName);

        try {
            FileWriter writer = new FileWriter(f);

            //Write length
            writer.write(cities.length - 1 + "\n");

            //Write each city out
            for (String city : cities) {
                if (city.equals(NULL_CITY_STRING)) continue;
                writer.write(city + "\n");
            }

            //Write Routes
            ArrayList<DirectedEdge> routes = new ArrayList<>();

            for (int i = 1; i < cities.length; i++) {
                Iterator<DirectedEdge> edges = graph.adj(i).iterator();
                if (edges == null) continue;

                while (edges.hasNext()) {
                    DirectedEdge e = edges.next();

                    int origin = e.from();
                    int destination = e.to();
                    int dist = (int)e.weight();
                    double price = e.weightTwo();
                    String routeToWrite = origin + " " + destination + " " + dist + " " + price + "\n";

                    if (routes.size() == 0) writer.write(routeToWrite);

                    int j = -1;
                    for (DirectedEdge r : routes) {
                        j++;
                        if ((r.to() == origin) && (r.from() == destination)) break;
                        if (j == routes.size() - 1) writer.write(routeToWrite);
                    }

                    routes.add(e);
                }
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("ERROR: " + e);
        }
    }
}