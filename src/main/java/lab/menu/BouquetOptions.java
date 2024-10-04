package lab.menu;

import lab.database.FlowerShopDatabase;
import lab.bouquets.*;
import lab.flowers.Flower;
import lab.flowers.FlowerComparator;

import java.util.List;

public class BouquetOptions {
    private final FlowerShopDatabase database;
    private final InputReader inputReader;
    private final FlowerOptions flowerOptions;
    private final AccessoryOptions accessoryOptions;

    public BouquetOptions(FlowerShopDatabase database, InputReader inputReader, FlowerOptions flowerOptions, AccessoryOptions accessoryOptions) {
        this.database = database;
        this.inputReader = inputReader;
        this.flowerOptions = flowerOptions;
        this.accessoryOptions = accessoryOptions;
    }

    public void createBouquet() {
        Bouquet bouquet = new Bouquet();
        String bouquetName = inputReader.readString("Enter bouquet name: ");

        while (true) {
            System.out.println("\n1. Add a flower to the bouquet");
            System.out.println("2. Add an accessory to the bouquet");
            System.out.println("3. Finish bouquet");
            int choice = inputReader.readInt("Choose an option: ");

            switch (choice) {
                case 1:
                    flowerOptions.addFlowerToBouquet(bouquet);
                    break;
                case 2:
                    accessoryOptions.addAccessoryToBouquet(bouquet);
                    break;
                case 3:
                    finalizeBouquet(bouquet, bouquetName);
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public void removeBouquet() {
        displayAllBouquets();
        String name = inputReader.readString("Enter the name of the bouquet to remove: ");
        database.removeBouquet(name);
        System.out.println("Bouquet removed successfully.");
    }

    private void finalizeBouquet(Bouquet bouquet, String bouquetName) {
        database.insertBouquet(bouquet, bouquetName);
        System.out.println("Bouquet created successfully.");
        System.out.println("Total cost: $" + bouquet.getTotalCost());
    }

    public void displayAllBouquets() {
        List<Bouquet> bouquets = database.getAllBouquets();
        System.out.println("\nAll Bouquets:");
        for (Bouquet bouquet : bouquets) {
            System.out.println(bouquet.getName() + ": ");
            System.out.println("Flowers:");
            for (Flower flower : bouquet.getFlowers()) {
                System.out.println("  " + flower.getName() + " - Stem Length: " + flower.getStemLength() + ", Freshness: " + flower.getFreshnessLevel());
            }
            System.out.println("Accessories:");
            for (BouquetAccessory accessory : bouquet.getAccessories()) {
                System.out.println("  " + accessory.getName() + " - Cost: $" + accessory.getCost());
            }
            System.out.println("Total Cost: $" + bouquet.getTotalCost());
            System.out.println();
        }
    }

    public void sortBouquetFlowers() {
        displayAllBouquets();
        String bouquetName = inputReader.readString("Enter the name of the bouquet to sort: ");
        Bouquet selectedBouquet = database.getBouquetByName(bouquetName);

        if (selectedBouquet != null) {
            System.out.println("Sort order:");
            System.out.println("1. Ascending (least fresh to freshest)");
            System.out.println("2. Descending (freshest to least fresh)");
            int choice = inputReader.readInt("Choose sorting order (1 or 2): ");

            boolean ascending = choice == 1;
            selectedBouquet.sortFlowers(ascending);

            System.out.println("Bouquet flowers sorted by freshness (" + (ascending ? "ascending" : "descending") + "):");
            for (Flower flower : selectedBouquet.getFlowers()) {
                System.out.println("  " + flower.getName() + " - Freshness: " + flower.getFreshnessLevel());
            }
        } else {
            System.out.println("Bouquet not found: " + bouquetName);
        }
    }
}