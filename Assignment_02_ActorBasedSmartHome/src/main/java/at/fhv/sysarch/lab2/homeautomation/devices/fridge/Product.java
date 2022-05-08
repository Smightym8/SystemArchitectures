package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

public class Product {
    private String name;
    private int weight;
    private int space;

    public Product(String aName, int aWeight, int aSpace) {
        this.name = aName;
        this.weight = aWeight;
        this.space = aSpace;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public int getSpace() {
        return space;
    }
}
