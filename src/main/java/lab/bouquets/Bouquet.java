package lab.bouquets;

import lab.flowers.Flower;
import java.util.ArrayList;
import java.util.List;

public class Bouquet {
    private final List<Flower> flowers;
    private final List<BouquetAccessory> accessories;

    public Bouquet() {
        this.flowers = new ArrayList<>();
        this.accessories = new ArrayList<>();
    }

    public void addFlower(Flower flower) {
        flowers.add(flower);
    }

    public void removeFlower(Flower flower) {
        flowers.remove(flower);
    }

    public void addAccessory(BouquetAccessory accessory) {
        accessories.add(accessory);
    }

    public void removeAccessory(BouquetAccessory accessory) {
        accessories.remove(accessory);
    }

    public double getTotalCost() {
        double flowerCost = flowers.stream().mapToDouble(Flower::getCost).sum();
        double accessoryCost = accessories.stream().mapToDouble(BouquetAccessory::getCost).sum();
        return flowerCost + accessoryCost;
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

    public List<BouquetAccessory> getAccessories() {
        return accessories;
    }
}