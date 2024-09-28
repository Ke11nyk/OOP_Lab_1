package lab.bouquets;

public class BouquetAccessory {
    private String name;
    private final double cost;

    public BouquetAccessory(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }
}