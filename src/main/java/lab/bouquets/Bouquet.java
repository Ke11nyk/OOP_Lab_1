package lab.bouquets;

import lab.flowers.Flower;
import java.util.ArrayList;
import java.util.List;

public class Bouquet {
    private final List<Flower> flowers;

    public Bouquet() {
        this.flowers = new ArrayList<>();
    }

    public void addFlower(Flower flower) {
        flowers.add(flower);
    }

    public void removeFlower(Flower flower) {
        flowers.remove(flower);
    }

    public double getTotalCost() {
        double flowerCost = flowers.stream().mapToDouble(Flower::getCost).sum();
        return flowerCost;
    }

    public Flower findFlowerByStemLength(int minLength, int maxLength) {
        return flowers.stream()
                .filter(f -> f.getStemLength() >= minLength && f.getStemLength() <= maxLength)
                .findFirst()
                .orElse(null);
    }

    public List<Flower> getFlowers() {
        return flowers;
    }
}