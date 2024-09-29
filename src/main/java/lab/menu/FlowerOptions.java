package lab.menu;

import lab.database.FlowerShopDatabase;
import lab.flowers.*;
import lab.bouquets.Bouquet;

import java.util.List;
import java.util.Scanner;

public class FlowerOptions {
    private static final FlowerShopDatabase database = new FlowerShopDatabase();
    private static final Scanner scanner = new Scanner(System.in);

    public static void addFlower() {
        System.out.println("Select flower type:");
        System.out.println("1. Lily");
        System.out.println("2. Rose");
        System.out.println("3. Tulip");
        System.out.println("4. Generic Flower");
        int flowerType = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter stem length: ");
        int stemLength = scanner.nextInt();

        System.out.print("Enter freshness level (1-10): ");
        int freshnessLevel = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Flower flower;
        switch (flowerType) {
            case 1:
                System.out.print("Enter petal count: ");
                int petalCount = scanner.nextInt();
                System.out.print("Is it fragrant? (true/false): ");
                boolean fragrant = scanner.nextBoolean();
                flower = new Lily(stemLength, freshnessLevel, petalCount, fragrant);
                break;
            case 2:
                System.out.print("Is it thorny? (true/false): ");
                boolean thorny = scanner.nextBoolean();
                flower = new Rose(stemLength, freshnessLevel, thorny);
                break;
            case 3:
                System.out.print("Enter bloom type: ");
                String bloomType = scanner.nextLine();
                System.out.print("Does it have a stiff stem? (true/false): ");
                boolean stiffStem = scanner.nextBoolean();
                flower = new Tulip(stemLength, freshnessLevel, bloomType, stiffStem);
                break;
            case 4:
                System.out.print("Enter flower name: ");
                String name = scanner.nextLine();
                flower = new Flower(name, stemLength, freshnessLevel);
                break;
            default:
                System.out.println("Invalid flower type. Creating a generic flower.");
                System.out.print("Enter flower name: ");
                name = scanner.nextLine();
                flower = new Flower(name, stemLength, freshnessLevel);
        }

        try {
            database.insertFlower(flower);
            System.out.println("Flower added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void removeFlower() {
        displayAllFlowers();
        System.out.print("Enter the ID of the flower to remove: ");
        int flowerId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        database.removeFlower(flowerId);
        System.out.println("Flower removed successfully.");
    }

    public static void addFlowerToBouquet(Bouquet bouquet) {
        List<Flower> flowers = database.getAllFlowers();
        for (int i = 0; i < flowers.size(); i++) {
            System.out.println((i + 1) + ". " + flowers.get(i).getName());
        }
        System.out.print("Choose a flower to add: ");
        int flowerChoice = scanner.nextInt() - 1;
        if (flowerChoice >= 0 && flowerChoice < flowers.size()) {
            bouquet.addFlower(flowers.get(flowerChoice));
            System.out.println("Flower added to bouquet.");
        } else {
            System.out.println("Invalid choice.");
        }
    }

    public static void displayAllFlowers() {
        List<Flower> flowers = database.getAllFlowers();
        System.out.println("\nAll Flowers:");
        for (Flower flower : flowers) {
            System.out.println(database.getFlowerId(flower) + ": " + flower.getName() +
                    " - Stem Length: " + flower.getStemLength() +
                    ", Freshness: " + flower.getFreshnessLevel() +
                    ", Cost: $" + flower.getCost());
            if (flower instanceof Lily) {
                Lily lily = (Lily) flower;
                System.out.println("  Petal Count: " + lily.getPetalCount() + ", Fragrant: " + lily.isFragrant());
            } else if (flower instanceof Rose) {
                Rose rose = (Rose) flower;
                System.out.println("  Thorny: " + rose.isThorny());
            } else if (flower instanceof Tulip) {
                Tulip tulip = (Tulip) flower;
                System.out.println("  Bloom Type: " + tulip.getBloomType() + ", Stiff Stem: " + tulip.hasStiffStem());
            }
        }
    }

    public static void findFlowersByStemLength() {
        BouquetOptions.displayAllBouquets();
        System.out.print("Enter the name of the bouquet to search: ");
        String bouquetName = scanner.nextLine();

        System.out.print("Enter minimum stem length: ");
        int minLength = scanner.nextInt();
        System.out.print("Enter maximum stem length: ");
        int maxLength = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        List<Bouquet> bouquets = database.getAllBouquets();
        Bouquet selectedBouquet = null;

        for (Bouquet bouquet : bouquets) {
            if (bouquet.getName().equalsIgnoreCase(bouquetName)) {
                selectedBouquet = bouquet;
                break;
            }
        }

        if (selectedBouquet != null) {
            List<Flower> foundFlowers = selectedBouquet.getFlowers().stream()
                    .filter(f -> f.getStemLength() >= minLength && f.getStemLength() <= maxLength)
                    .toList();

            if (!foundFlowers.isEmpty()) {
                System.out.println("Flowers found in '" + selectedBouquet.getName() + "' within the stem length range (" + minLength + " - " + maxLength + "):");
                for (Flower flower : foundFlowers) {
                    System.out.println("  " + flower.getName() + " - Stem Length: " + flower.getStemLength());
                }
            } else {
                System.out.println("No flowers found in '" + selectedBouquet.getName() + "' within the stem length range (" + minLength + " - " + maxLength + ").");
            }
        } else {
            System.out.println("Bouquet not found: " + bouquetName);
        }
    }

    private static String getBouquetNameForFlower(List<Bouquet> bouquets, Flower flower) {
        for (Bouquet bouquet : bouquets) {
            if (bouquet.getFlowers().contains(flower)) {
                return bouquet.getName();
            }
        }
        return "Unknown";
    }
}