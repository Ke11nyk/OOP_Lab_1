package lab.bouquets;

import lab.flowers.Flower;
import java.util.Comparator;
import java.util.List;

public class BouquetSorter {
    public static void sortByFreshness(Bouquet bouquet) {
        List<Flower> flowers = bouquet.getFlowers();
        flowers.sort(Comparator.comparingInt(Flower::getFreshnessLevel).reversed());
    }
}