package lab.flowers;

public class Rose extends Flower {
    private final boolean thorny;

    public Rose(int stemLength, int freshnessLevel, boolean thorny) {
        super("Rose", stemLength, freshnessLevel);
        this.thorny = thorny;
    }

    public boolean isThorny() {
        return thorny;
    }

    @Override
    public double getCost() {
        double baseCost = super.getCost();
        if (thorny) {
            return baseCost * 1.2; // Increase value for thorny roses
        }
        return baseCost;
    }
}