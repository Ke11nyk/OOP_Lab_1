package lab.bouquets;

public class BouquetAccessory {
    private final String name;
    private final double cost;

    public BouquetAccessory(String name, double cost, String description) {
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