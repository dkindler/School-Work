/**
 *
 * Daniel Kindler
 * CS1501 Project 3
 * dak160@pitt.edu
 *
 **/

import java.lang.System;
import java.util.Scanner;
import java.util.Random;

public class CarTracker {

    public static DPQueue database = new DPQueue();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean run = true;

        while (run) {
            printMenu();
            int option = getIntInput();

            switch (option) {
                case 0:
                    add();
                    break;
                case 1:
                    update();
                    break;
                case 2:
                    remove();
                    break;
                case 3:
                    printCheapest();
                    break;
                case 4:
                    printLeastUsed();
                    break;
                case 5:
                    printCheapestWithMakeModel();
                    break;
                case 6:
                    printLeastWithMakeModel();
                    break;
                case 7:
                    printAll();
                    break;
                case 8:
                    addRandomCars();
                    break;
                case 9:
                    run = false;
                    break;
                default: System.out.println("\nPlease enter a valid selection between 0 and 9: ");
                    break;
            }
        }
        System.out.println("Goodbye!");
    }

    public static void printMenu() {
        System.out.println("------------MAIN MENU------------");
        System.out.println("[0]\tADD CAR");
        System.out.println("[1]\tUPDATE CAR");
        System.out.println("[2]\tREMOVE CAR");
        System.out.println("[3]\tFIND CHEAPEST CAR");
        System.out.println("[4]\tFIND LEAST USED CAR");
        System.out.println("[5]\tFIND CHEAPEST CAR BY MAKE & MODEL");
        System.out.println("[6]\tFIND LOWEST MILAGE CAR BY MAKE & MODEL");
        System.out.println("[7]\tPRINT ALL");
        System.out.println("[8]\tADD RANDOM CARS TO DATABASE");
        System.out.println("[9]\tQUIT");
    }

    public static void add() {
        Scanner sc = new Scanner(System.in);
        Car c = new Car();
        System.out.println("\n-------------ADD CAR-------------");

        System.out.println("Please enter the following information.");

        System.out.print("VIN: ");
        c.setVin(sc.nextLine().toLowerCase());

        System.out.print("Make: ");
        c.setMake(sc.nextLine());

        System.out.print("Model: ");
        c.setModel(sc.nextLine());

        System.out.print("Color: ");
        c.setColor(sc.nextLine());

        System.out.print("Milage: ");
        c.setMilage(getIntInput());

        System.out.print("Price: ");
        c.setPrice(getIntInput());

        database.add(c);
        System.out.print("\n");
    }

    public static void printAll() {
        System.out.println("\n-------------ALL CARS-------------");
        database.print();
        System.out.print("\n");
    }

    public static void printCheapest() {
        System.out.println("\n-------------CHEAPEST CAR-------------");
        Car c = (Car)database.minPrice();
        if (c == null) {
            System.out.print("\n");
            return;
        }

        c.print();
        System.out.print("\n");
    }

    public static void printLeastUsed() {
        System.out.println("\n-------------LOWEST MILAGE CAR-------------");
        Car c = (Car)database.minMiles();
        if (c == null) {
            System.out.print("\n");
            return;
        }

        c.print();
        System.out.print("\n");
    }

    public static void remove() {
        System.out.println("\n-------------REMOVE CAR-------------");
        System.out.println("Please enter the VIN of the car you wish to remove: ");
        Scanner sc = new Scanner(System.in);
        String vin = sc.nextLine();
        database.remove(vin);
        System.out.print("\n");
    }

    public static void update() {
        System.out.println("\n-------------UPDATE CAR-------------");

        System.out.println("Please enter the VIN: ");
        Scanner sc = new Scanner(System.in);
        String vin = sc.nextLine();

        System.out.println("What would you like to update?");
        System.out.println("[0]\tVIN\n[1]\tMAKE\n[2]\tMODEL\n[3]\tCOLOR\n[4]\tPRICE\n[5]\tMILAGE");

        int response = getIntInput();
        while (response < 0 || response > 5) {
            System.out.println("Please enter a number between 0 and 5: ");
            response = getIntInput();
        }

        System.out.println("Please enter a value: ");
        String val = sc.nextLine();

        switch (response) {
            case 0:
                database.update(vin, DPQueue.FieldType.VIN, val);
                break;
            case 1:
                database.update(vin, DPQueue.FieldType.MAKE, val);
                break;
            case 2:
                database.update(vin, DPQueue.FieldType.MODEL, val);
                break;
            case 3:
                database.update(vin, DPQueue.FieldType.COLOR, val);
                break;
            case 4:
                database.update(vin, DPQueue.FieldType.PRICE, val);
                break;
            case 5:
                database.update(vin, DPQueue.FieldType.MILAGE, val);
                break;
            default:
                break;
        }
        System.out.print("\n");
    }

    public static void printLeastWithMakeModel() {
        System.out.println("\n-------------MAKE/MODEL MILAGE FIND-------------");
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter the make: ");
        String make = sc.nextLine();
        System.out.println("Please enter the model: ");
        String model = sc.nextLine();

        Car c = database.milageMakeModel(make, model);

        if (c == null) {
            System.out.print("No car found.\n");
            return;
        }

        c.print();
        System.out.print("\n");
    }

    public static void printCheapestWithMakeModel() {
        System.out.println("\n-------------MAKE/MODEL PRICE FIND-------------");
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter the make: ");
        String make = sc.nextLine();
        System.out.println("Please enter the model: ");
        String model = sc.nextLine();

        Car c = database.priceMakeModel(make, model);
        if (c == null) {
            System.out.print("No car found.\n");
            return;
        }

        System.out.print("\n");
        c.print();
        System.out.print("\n");
    }

    /**
     * Private
     */
    private static int getIntInput() {
        Scanner sc = new Scanner(System.in);
        int val = 0;
        while (sc.hasNext()) {
            if (sc.hasNextInt()) {
                return sc.nextInt();
            } else {
                System.out.println("\nPlease enter an integer: ");
                sc.next();
            }
        }

        return 0;
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    private static String randomString(int len) {
        Random rnd = new Random();
        String AB = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder( len );
        for (int i = 0; i < len; i++ ) sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public static void addRandomCars() {
        System.out.println("");
        String[] makes = {"Honda", "Ferrari", "Mercedes", "Audi", "BMW", "Ford", "Accura", "Chevy"};
        String[] colors = {"Red", "Blue", "Orange", "Black", "White"};
        String[] models = {"Spider", "F150", "X3", "X4", "X5", "Bolt", "Ryder", "FastLane"};

        for (int i = 0; i < 200; i++) {
            Car c = new Car();
            c.setVin(randomString(17).toLowerCase());
            c.setMake(makes[randInt(0, makes.length - 1)]);
            c.setModel(models[randInt(0, models.length - 1)]);
            c.setColor(colors[randInt(0, colors.length - 1)]);

            Car b = new Car(randomString(17).toLowerCase(), c.getMake(), c.getModel(), randInt(0, 100000), randInt(0, 100000), c.getColor());

            c.setMilage(randInt(0, 100000));
            c.setPrice(randInt(0, 100000));

            database.add(c);
            database.add(b);
        }

        System.out.println("ADDED RANDOM CARS\n");
    }
}