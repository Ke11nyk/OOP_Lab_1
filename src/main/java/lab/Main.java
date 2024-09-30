package lab;

import lab.database.FlowerShopDatabase;
import lab.menu.*;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static final FlowerShopDatabase database = new FlowerShopDatabase();
    private static final Scanner scanner = new Scanner(System.in);

    private static final InputReader inputReader = new InputReader() {
        @Override
        public int readInt(String prompt) {
            System.out.print(prompt + " ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Consume invalid input
            }
            return scanner.nextInt();
        }

        @Override
        public double readDouble(String prompt) {
            System.out.print(prompt + " ");
            while (!scanner.hasNextDouble()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Consume invalid input
            }
            return scanner.nextDouble();
        }


        @Override
        public boolean readBoolean(String prompt) {
            System.out.print(prompt + " ");
            return scanner.nextBoolean();
        }

        @Override
        public String readString(String prompt) {
            System.out.print(prompt + " ");
            return scanner.nextLine();
        }
    };

    private static final FlowerOptions flowerOptions = new FlowerOptions(database, inputReader);
    private static final AccessoryOptions accessoryOptions = new AccessoryOptions(database, inputReader);
    private static final BouquetOptions bouquetOptions = new BouquetOptions(database, inputReader, flowerOptions, accessoryOptions);

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
            System.out.println("8. Remove a accessory");
            System.out.println("9. Remove a bouquet");
            System.out.println("10. Sort flowers in a bouquet");
            System.out.println("11. Find flowers in a bouquet by stem length");
            System.out.println("12. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    flowerOptions.addFlower();
                    break;
                case 2:
                    accessoryOptions.addAccessory();
                    break;
                case 3:
                    bouquetOptions.createBouquet();
                    break;
                case 4:
                    flowerOptions.displayAllFlowers();
                    break;
                case 5:
                    accessoryOptions.displayAllAccessories();
                    break;
                case 6:
                    bouquetOptions.displayAllBouquets();
                    break;
                case 7:
                    flowerOptions.removeFlower();
                    break;
                case 8:
                    accessoryOptions.removeAccessory();
                    break;
                case 9:
                    bouquetOptions.removeBouquet();
                    break;
                case 10:
                    bouquetOptions.sortBouquetFlowers();
                    break;
                case 11:
                    flowerOptions.findFlowersByStemLength();
                    break;
                case 12:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}