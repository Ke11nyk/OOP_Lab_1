package lab.flowers;

import java.util.Comparator;

public class FlowerComparator implements Comparator<Flower> {
    private final boolean ascending;

    public FlowerComparator(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public int compare(Flower flower1, Flower flower2) {
        int result = Integer.compare(flower1.getFreshnessLevel(), flower2.getFreshnessLevel());
        return ascending ? result : -result;
    }
}