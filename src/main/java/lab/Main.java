package lab;

import lab.database.*;
import lab.menu.*;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static final FlowerShopDatabase database = new FlowerShopDatabase();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws SQLException {
        database.createTables();

        while (true) {
            System.out.println("\n1. Add a flower");
            System.out.println("2. Add an accessory");
            System.out.println("3. Create a bouquet");
            System.out.println("4. Display all flowers");
            System.out.println("5. Display all accessories");
            System.out.println("6. Display all bouquets");
            System.out.println("7. Remove a flower");
            System.out.println("8. Remove a bouquet");
            System.out.println("9. Sort flowers in a bouquet");
            System.out.println("10. Find flowers in a bouquet by stem length");
            System.out.println("11. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    FlowerOptions.addFlower();
                    break;
                case 2:
                    AccessoryOptions.addAccessory();
                    break;
                case 3:
                    BouquetOptions.createBouquet();
                    break;
                case 4:
                    FlowerOptions.displayAllFlowers();
                    break;
                case 5:
                    AccessoryOptions.displayAllAccessories();
                    break;
                case 6:
                    BouquetOptions.displayAllBouquets();
                    break;
                case 7:
                    FlowerOptions.removeFlower();
                    break;
                case 8:
                    BouquetOptions.removeBouquet();
                    break;
                case 9:
                    BouquetOptions.sortBouquetFlowers();
                    break;
                case 10:
                    FlowerOptions.findFlowersByStemLength();
                    break;
                case 11:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}