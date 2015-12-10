/**
 *
 * Daniel Kindler
 * CS1501 Project 3
 * dak160@pitt.edu
 *
 **/

import java.lang.System;
import java.util.*;
import java.util.Stack;

public class DPQueue {

    public enum FieldType { VIN, MILAGE, COLOR, MAKE, MODEL, PRICE }

    public static ArrayList<Car> carsList = new ArrayList<>();
    public static MinPQ pricePQ = new MinPQ();
    public static MinPQ milesPQ = new MinPQ();
    public static int size;

    public DPQueue() {
        int size = 0;
    }

    public void add(Car c){

        String vin = c.getVin();

        if (vin.length() != 17 || vin.contains("o") || vin.contains("i")) {
            System.out.println("\nERROR: Could not add car. Invalid VIN\nLength must be 17 and contain no 'i' or 'o'");
            return;
        }

        if (vinInUse(vin)) {
            System.out.println("\nERROR: This VIN is already in use. Please use another.");
            return;
        }

        carsList.add(c);

        SortCar cm = new SortCar(size, c.getMilage());
        milesPQ.insert(cm);

        SortCar cp = new SortCar(size, c.getPrice());
        pricePQ.insert(cp);

        size++;
    }

    public Car minMiles() {
        if (size <= 0) {
            System.out.println("No cars found");
            return null;
        }

        int num = ((SortCar) milesPQ.min()).index;
        return carsList.get(num);
    }

    public Car minPrice() {
        if (size <= 0) {
            System.out.println("No cars found");
            return null;
        }

        int num = ((SortCar)pricePQ.min()).index;
        return carsList.get(num);
    }

    public static void print() {
        if (size <= 0) {
            System.out.println("No cars found");
            return;
        }

        for (Car c : carsList) {
            if (c != null) {
                c.print();
                System.out.print("\n");
            }
        }
    }

    public static void remove(String vin){
        if (size <= 0) {
            System.out.println("No cars found");
            return;
        }

        vin = vin.toLowerCase();

        Stack<SortCar> mileStack = new Stack<>();
        Stack<SortCar> priceStack = new Stack<>();

        boolean found = false;
        int i = 0;
        for (Car c : carsList) {
            if (vin.equals(c.getVin())) {
                carsList.set(i, null);
                found = true;
                break;
            }

            i++;
        }

        if (!found) {
            System.out.println("VIN NOT FOUND");
            return;
        }

        while (true) {
            SortCar temp = (SortCar)pricePQ.delMin();
            if (temp.index == i) break;
            priceStack.add(temp);
        }
        while (!priceStack.isEmpty()) pricePQ.insert(priceStack.pop());
        while (true) {
            SortCar temp = (SortCar)milesPQ.delMin();
            if (temp.index == i) break;
            mileStack.add(temp);
        }
        while(!mileStack.isEmpty()) milesPQ.insert(mileStack.pop());

        size = size - 1;
    }


    public void update(String vin, FieldType type, String val){
        if (size <= 0) {
            System.out.println("No cars found");
            return;
        }

        Car c = null;
        vin = vin.toLowerCase();

        boolean found = false;
        int i = 0;

        for (Car ci : carsList) {
            if (vin.equals(ci.getVin())) {
                found = true;
                c = ci;
                break;
            }
            i++;
        }

        if (!found) {
            System.out.println("VIN NOT FOUND");
            return;
        }

        Stack<SortCar> temp = null;

        switch (type) {
            case VIN:
                c.setVin(val);
                break;
            case COLOR:
                c.setColor(val);
                break;
            case MAKE:
                c.setMake(val);
                break;
            case MODEL:
                c.setModel(val);
                break;
            case PRICE:
                c.setPrice(Integer.parseInt(val));
                temp = new Stack<>();
                while (true) {
                    SortCar sc = (SortCar)pricePQ.delMin();
                    if (sc.index == i) {
                        sc.sortValue = Integer.parseInt(val);
                        temp.add(sc);
                        break;
                    }
                    temp.add(sc);
                }
                while (!temp.isEmpty()) pricePQ.insert(temp.pop());
                break;
            case MILAGE:
                c.setMilage(Integer.parseInt(val));
                temp = new Stack<>();
                while (true) {
                    SortCar sc = (SortCar)milesPQ.delMin();
                    if (sc.index == i) {
                        sc.sortValue = Integer.parseInt(val);
                        temp.add(sc);
                        break;
                    }
                    temp.add(sc);
                }
                while(!temp.isEmpty()) pricePQ.insert(temp.pop());
                break;
            default:
                break;
        }
    }

    public Car priceMakeModel (String make, String model) {
        if (size <= 0) {
            return null;
        }
        Car c = new Car();
        Stack<SortCar> temp = new Stack<>();
        boolean found = false;

        int count = 0;
        while (count < size) {
            SortCar sc = (SortCar)pricePQ.delMin();
            Car indexCar = carsList.get(sc.index);
            if(indexCar.getMake().equals(make) && indexCar.getModel().equals(model)) {
                c = indexCar;
                temp.add(sc);
                found = true;
                break;
            }
            temp.add(sc);
            count++;
        }

        if (!found) return null;
        while (!temp.isEmpty()) pricePQ.insert(temp.pop());
        return c;
    }

    public Car milageMakeModel(String make, String model) {
        if (size <= 0) {
            return null;
        }
        Car c = new Car();
        Stack<SortCar> temp = new Stack<>();
        boolean found = false;

        int count = 0;
        while (count < size) {
            SortCar sc = (SortCar)milesPQ.delMin();
            Car indexCar = carsList.get(sc.index);
            if(indexCar.getMake().equals(make) && indexCar.getModel().equals(model)) {
                c = indexCar;
                temp.add(sc);
                found = true;
                break;
            }
            temp.add(sc);
            count++;
        }

        if (!found) return null;
        while (!temp.isEmpty()) milesPQ.insert(temp.pop());
        return c;
    }

    /**
    PRIVATE
     */

    public static boolean vinInUse(String vin) {
        for (Car c : carsList) {
            if (vin.equals(c.getVin())) return true;
        }

        return false;
    }
}