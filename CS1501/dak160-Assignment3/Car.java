import java.lang.Integer;
import java.lang.String;

public class Car {
    private String vin;
    private String make;
    private String model;
    private int price;
    private int milage;
    private String color;


    /**
     * Initializers
     */

    public Car() {
        this.vin = "";
        this.make = "";
        this.model = "";
        this.price = 0;
        this.milage = 0;
        this.color = "";
    }

    public Car(String vim, String make, String model, int price, int milage, String color) {
        this.vin = vim;
        this.color = color;
        this.make = make;
        this.model = model;
        this.price = price;
        this.milage = milage;
    }

    /**
     * Setters
     */
    public void setVin(String v) { this.vin = v; }
    public void setMake(String m) { this.make = m; }
    public void setModel(String m) { this.model = m; }
    public void setPrice(int p) { this.price = p; }
    public void setMilage(int m) { this.milage = m; }
    public void setColor(String c) { this.color = c; }

    /**
     * Getters
     */
    public String getVin() { return this.vin; }
    public String getMake() { return this.make; }
    public String getModel() { return this.model; }
    public String getColor() { return this.color; }
    public int getPrice() { return this.price; }
    public int getMilage() { return this.milage; }


    /**
     * Functions
     */
    public void print() {
        System.out.println("VIM: " + this.vin);
        System.out.println(this.color + " " + this.make + " " + this.model);
        System.out.println("Milage: " + Integer.toString(this.milage));
        System.out.println("Price: $" + Integer.toString(this.price));
    }

}