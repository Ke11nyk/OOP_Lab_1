package lab.menu;

import lab.database.*;
import lab.bouquets.*;

import java.util.List;
import java.util.Scanner;

public class AccessoryOptions {
    private static final FlowerShopDatabase database = new FlowerShopDatabase();
    private static final Scanner scanner = new Scanner(System.in);

    public static void addAccessory() {
        System.out.print("Enter accessory name: ");
        String name = scanner.nextLine();
        System.out.print("Enter accessory cost: ");
        double cost = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        BouquetAccessory accessory = new BouquetAccessory(name, cost);
        database.insertAccessory(accessory);
        System.out.println("Accessory added successfully.");
    }

    public static void addAccessoryToBouquet(Bouquet bouquet) {
        System.out.print("Enter accessory name: ");
        String name = scanner.nextLine();

        BouquetAccessory accessory = database.getAccessoryByName(name);
        if (accessory != null) {
            bouquet.addAccessory(accessory);
            System.out.println("Accessory added to bouquet.");
        } else {
            System.out.println("Accessory not found in the database. Please add it first.");
        }
    }

    public static void displayAllAccessories() {
        List<BouquetAccessory> accessories = database.getAllAccessories();
        System.out.println("\nAll Accessories:");
        for (BouquetAccessory accessory : accessories) {
            System.out.println(accessory.getName() + " - Cost: $" + accessory.getCost());
        }
    }
}
