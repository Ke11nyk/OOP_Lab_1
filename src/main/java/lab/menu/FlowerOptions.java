package lab.menu;

import lab.database.FlowerShopDatabase;
import lab.flowers.*;
        import lab.bouquets.Bouquet;

import java.util.List;
import java.util.Scanner;

public class FlowerOptions {
    private final FlowerShopDatabase database;
    private final InputReader inputReader;
    private static final Scanner scanner = new Scanner(System.in);

    public FlowerOptions(FlowerShopDatabase database, InputReader inputReader) {
        this.database = database;
        this.inputReader = inputReader;
    }

    public void addFlower() {
        int flowerType = inputReader.readInt("Select flower type:\n1. Lily\n2. Rose\n3. Tulip\n4. Generic Flower\n");
        int stemLength = inputReader.readInt("Enter stem length:");
        int freshnessLevel = inputReader.readInt("Enter freshness level (1-10):");

        Flower flower;
        switch (flowerType) {
            case 1:
                int petalCount = inputReader.readInt("Enter petal count:");
                boolean fragrant = inputReader.readBoolean("Is it fragrant? (true/false):");
                flower = new Lily(stemLength, freshnessLevel, petalCount, fragrant);
                break;
            case 2:
                boolean thorny = inputReader.readBoolean("Is it thorny? (true/false):");
                flower = new Rose(stemLength, freshnessLevel, thorny);
                break;
            case 3:
                String bloomType = inputReader.readString("Enter bloom type:");
                boolean stiffStem = inputReader.readBoolean("Does it have a stiff stem? (true/false):");
                flower = new Tulip(stemLength, freshnessLevel, bloomType, stiffStem);
                break;
            default:
                String name = inputReader.readString("Enter flower name:");
                flower = new Flower(name, stemLength, freshnessLevel);
        }

        database.insertFlower(flower);
        System.out.println("Flower added successfully.");
    }

    public void removeFlower() {
        displayAllFlowers();
        int flowerId = inputReader.readInt("Enter the ID of the flower to remove:");
        database.removeFlower(flowerId);
        System.out.println("Flower removed successfully.");
    }

    public void addFlowerToBouquet(Bouquet bouquet) {
        List<Flower> flowers = database.getAllFlowers();
        for (int i = 0; i < flowers.size(); i++) {
            System.out.println((i + 1) + ". " + flowers.get(i).getName());
        }
        int flowerChoice = inputReader.readInt("Choose a flower to add:") - 1;
        if (flowerChoice >= 0 && flowerChoice < flowers.size()) {
            bouquet.addFlower(flowers.get(flowerChoice));
            System.out.println("Flower added to bouquet.");
        } else {
            System.out.println("Invalid choice.");
        }
    }

    public void displayAllFlowers() {
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

    public void findFlowersByStemLength() {
        displayAllBouquets();
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

    private void displayAllBouquets() {
        List<Bouquet> bouquets = database.getAllBouquets();
        System.out.println("\nAll Bouquets:");
        for (Bouquet bouquet : bouquets) {
            System.out.println(bouquet.getName());
        }
    }

    private String getBouquetNameForFlower(List<Bouquet> bouquets, Flower flower) {
        for (Bouquet bouquet : bouquets) {
            if (bouquet.getFlowers().contains(flower)) {
                return bouquet.getName();
            }
        }
        return "Unknown";
    }
}