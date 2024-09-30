package lab.menu;

import lab.database.FlowerShopDatabase;
import lab.bouquets.*;

import java.util.List;

public class AccessoryOptions {
    private final FlowerShopDatabase database;
    private final InputReader inputReader;

    public AccessoryOptions(FlowerShopDatabase database, InputReader inputReader) {
        this.database = database;
        this.inputReader = inputReader;
    }

    public void addAccessory() {
        String name = inputReader.readString("Enter accessory name: ");
        double cost = inputReader.readDouble("Enter accessory cost: ");

        BouquetAccessory accessory = new BouquetAccessory(name, cost);
        database.insertAccessory(accessory);
        System.out.println("Accessory added successfully.");
    }

    public void removeAccessory() {
        displayAllAccessories();
        String accessoryName = inputReader.readString("Enter the name of the accessory to remove:");
        database.removeAccessory(accessoryName);
        System.out.println("Accessory removed successfully.");
    }

    public void addAccessoryToBouquet(Bouquet bouquet) {
        List<BouquetAccessory> accessories = database.getAllAccessories();
        for (int i = 0; i < accessories.size(); i++) {
            System.out.println((i + 1) + ". " + accessories.get(i).getName() + " - Cost: $" + accessories.get(i).getCost());
        }
        int accessoryChoice = inputReader.readInt("Choose an accessory to add:") - 1;
        if (accessoryChoice >= 0 && accessoryChoice < accessories.size()) {
            bouquet.addAccessory(accessories.get(accessoryChoice));
            System.out.println("Accessory added to bouquet.");
        } else {
            System.out.println("Invalid choice.");
        }
    }

    public void displayAllAccessories() {
        List<BouquetAccessory> accessories = database.getAllAccessories();
        System.out.println("\nAll Accessories:");
        for (BouquetAccessory accessory : accessories) {
            System.out.println(database.getAccessoryId(accessory) + ": " + accessory.getName() + " - Cost: $" + accessory.getCost());
        }
    }
}