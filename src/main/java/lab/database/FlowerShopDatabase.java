package lab.database;

import lab.bouquets.Bouquet;
import lab.bouquets.BouquetAccessory;
import lab.database.interfaces.IAccessoryDatabase;
import lab.database.interfaces.IBouquetDatabase;
import lab.database.interfaces.IFlowerDatabase;
import lab.flowers.Flower;

import java.sql.*;
import java.util.List;
import io.github.cdimascio.dotenv.Dotenv;

public class FlowerShopDatabase implements IFlowerDatabase, IBouquetDatabase, IAccessoryDatabase {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    FlowerDatabase flowerDatabase;
    BouquetDatabase bouquetDatabase;
    AccessoryDatabase accessoryDatabase;

    public FlowerShopDatabase() {
        try {
            Class.forName("org.postgresql.Driver");
            this.flowerDatabase = new FlowerDatabase();
            this.bouquetDatabase = new BouquetDatabase();
            this.accessoryDatabase = new AccessoryDatabase();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC Driver not found", e);
        }
    }

    public void createTables() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS flowers (" +
                    "id SERIAL PRIMARY KEY," +
                    "type VARCHAR(50) NOT NULL," +
                    "name VARCHAR(100) NOT NULL," +
                    "stem_length INTEGER NOT NULL," +
                    "freshness_level INTEGER NOT NULL," +
                    "petal_count INTEGER," +
                    "fragrant BOOLEAN," +
                    "thorny BOOLEAN," +
                    "bloom_type VARCHAR(50)," +
                    "stiff_stem BOOLEAN)");

            stmt.execute("CREATE TABLE IF NOT EXISTS bouquets (" +
                    "id SERIAL PRIMARY KEY," +
                    "name VARCHAR(100) NOT NULL UNIQUE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS bouquet_flowers (" +
                    "bouquet_id INTEGER REFERENCES bouquets(id)," +
                    "flower_id INTEGER REFERENCES flowers(id)," +
                    "quantity INTEGER NOT NULL DEFAULT 1," +
                    "PRIMARY KEY (bouquet_id, flower_id))");

            stmt.execute("CREATE TABLE IF NOT EXISTS accessories (" +
                    "id SERIAL PRIMARY KEY," +
                    "name VARCHAR(100) NOT NULL," +
                    "cost DECIMAL(10, 2) NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS bouquet_accessories (" +
                    "bouquet_id INTEGER REFERENCES bouquets(id)," +
                    "accessory_id INTEGER REFERENCES accessories(id)," +
                    "quantity INTEGER NOT NULL DEFAULT 1," +
                    "PRIMARY KEY (bouquet_id, accessory_id))");

        } catch (SQLException e) {
            throw new RuntimeException("Error creating tables", e);
        }
    }

    public void insertFlower(Flower flower) {
        flowerDatabase.insertFlower(flower);
    }

    public void removeFlower(int flowerId) {
        flowerDatabase.removeFlower(flowerId);
    }

    public int getFlowerId(Flower flower) {
        return flowerDatabase.getFlowerId(flower);
    }

    public List<Flower> getAllFlowers() {
        return flowerDatabase.getAllFlowers();
    }

    public boolean insertBouquet(Bouquet bouquet, String bouquetName) {
        return bouquetDatabase.insertBouquet(bouquet, bouquetName);
    }

    public int getBouquetId(Bouquet bouquet) {
        return bouquetDatabase.getBouquetId(bouquet);
    }

    public List<Bouquet> getAllBouquets() {
        return bouquetDatabase.getAllBouquets();
    }

    public void removeBouquet(String name) {
        bouquetDatabase.removeBouquet(name);
    }

    public void insertAccessory(BouquetAccessory accessory){
        accessoryDatabase.insertAccessory(accessory);
    }

    public int getAccessoryId(BouquetAccessory accessory) {
        return accessoryDatabase.getAccessoryId(accessory);
    }

    public BouquetAccessory getAccessoryByName(String name) {
        return accessoryDatabase.getAccessoryByName(name);
    }

    public List<BouquetAccessory> getAllAccessories() {
        return accessoryDatabase.getAllAccessories();
    }
}