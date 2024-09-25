package lab.flowers;

public class Lily extends Flower {
    private final int petalCount;
    private final boolean fragrant;

    public Lily(int stemLength, int freshnessLevel, int petalCount, boolean fragrant) {
        super("Lily", stemLength, freshnessLevel);
        this.petalCount = petalCount;
        this.fragrant = fragrant;
    }

    public int getPetalCount() {
        return petalCount;
    }

    public boolean isFragrant() {
        return fragrant;
    }

    @Override
    public double getCost() {
        double baseCost = super.getCost();
        if (fragrant) {
            return baseCost * 1.1; // Increase value for fragrant lilies
        }
        return baseCost;
    }
}