package lab.flowers;

public class Tulip extends Flower {
    private final String bloomType;
    private final boolean stiffStem;

    public Tulip(int stemLength, int freshnessLevel, String bloomType, boolean stiffStem) {
        super("Tulip", stemLength, freshnessLevel);
        this.bloomType = bloomType;
        this.stiffStem = stiffStem;
    }

    public String getBloomType() {
        return bloomType;
    }

    public boolean hasStiffStem() {
        return stiffStem;
    }

    @Override
    public double getCost() {
        double baseCost = super.getCost();
        if (stiffStem) {
            return baseCost * 1.15; // Increase value for tulips with stiff stems
        }
        return baseCost;
    }
}