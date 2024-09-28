package lab.menu;

import lab.database.*;
import lab.bouquets.*;
import lab.flowers.Flower;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class BouquetOptions {
    private static final FlowerShopDatabase database = new FlowerShopDatabase();
    private static final Scanner scanner = new Scanner(System.in);

    public static void createBouquet() throws SQLException {
        Bouquet bouquet = new Bouquet();

        while (true) {
            System.out.println("\n1. Add a flower to the bouquet");
            System.out.println("2. Add an accessory to the bouquet");
            System.out.println("3. Finish bouquet");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    FlowerOptions.addFlowerToBouquet(bouquet);
                    break;
                case 2:
                    AccessoryOptions.addAccessoryToBouquet(bouquet);
                    break;
                case 3:
                    finalizeBouquet(bouquet);
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public static void removeBouquet() {
        displayAllBouquets();
        System.out.print("Enter the ID of the bouquet to remove: ");
        int bouquetId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        database.removeBouquet(bouquetId);
        System.out.println("Bouquet removed successfully.");
    }

    public static void finalizeBouquet(Bouquet bouquet) throws SQLException {
        System.out.print("Enter bouquet name: ");
        String bouquetName = scanner.nextLine();
        database.insertBouquet(bouquet, bouquetName);
        System.out.println("Bouquet created successfully.");
        System.out.println("Total cost: $" + bouquet.getTotalCost());

        System.out.print("Enter minimum stem length: ");
        int minLength = scanner.nextInt();
        System.out.print("Enter maximum stem length: ");
        int maxLength = scanner.nextInt();

        Flower foundFlower = bouquet.findFlowerByStemLength(minLength, maxLength);
        if (foundFlower != null) {
            System.out.println("Found flower in range: " + foundFlower.getName());
        } else {
            System.out.println("No flower found in the given stem length range.");
        }
    }

    public static void displayAllBouquets() {
        List<Bouquet> bouquets = database.getAllBouquets();
        System.out.println("\nAll Bouquets:");
        for (Bouquet bouquet : bouquets) {
            System.out.println(database.getBouquetId(bouquet) + ": " + bouquet.getName());
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

    public static void sortBouquetFlowers() {
        displayAllBouquets();
        System.out.print("Enter the ID of the bouquet to sort: ");
        int bouquetId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        List<Bouquet> bouquets = database.getAllBouquets();
        Bouquet selectedBouquet = null;

        if (selectedBouquet != null) {
            BouquetSorter.sortByFreshness(selectedBouquet);
            System.out.println("Bouquet flowers sorted by freshness:");
            for (Flower flower : selectedBouquet.getFlowers()) {
                System.out.println("  " + flower.getName() + " - Freshness: " + flower.getFreshnessLevel());
            }
        } else {
            System.out.println("Bouquet not found.");
        }
    }
}
