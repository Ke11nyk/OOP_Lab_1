package lab.database;

import lab.bouquets.BouquetAccessory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import io.github.cdimascio.dotenv.Dotenv;
import lab.database.interfaces.IAccessoryDatabase;

public class AccessoryDatabase implements IAccessoryDatabase {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    public void insertAccessory(BouquetAccessory accessory) {
        String sql = "INSERT INTO accessories (name, cost) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accessory.getName());
            pstmt.setDouble(2, accessory.getCost());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting accessory", e);
        }
    }

    public static int getAccessoryId(BouquetAccessory accessory) {
        String sql = "SELECT id FROM accessories WHERE name = ? AND cost = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accessory.getName());
            pstmt.setDouble(2, accessory.getCost());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new RuntimeException("Accessory not found in database");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting accessory ID", e);
        }
    }

    public BouquetAccessory getAccessoryByName(String name) {
        String sql = "SELECT * FROM accessories WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String accessoryName = rs.getString("name");
                    double cost = rs.getDouble("cost");
                    return new BouquetAccessory(accessoryName, cost);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving accessory: " + e.getMessage(), e);
        }
        return null;
    }

    public List<BouquetAccessory> getAllAccessories() {
        List<BouquetAccessory> accessories = new ArrayList<>();
        String sql = "SELECT * FROM accessories";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("name");
                double cost = rs.getDouble("cost");
                accessories.add(new BouquetAccessory(name, cost));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving accessories", e);
        }
        return accessories;
    }
}