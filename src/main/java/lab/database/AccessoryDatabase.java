package lab.database;

import lab.bouquets.BouquetAccessory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import io.github.cdimascio.dotenv.Dotenv;
import lab.database.interfaces.IAccessoryDatabase;

public class AccessoryDatabase implements IAccessoryDatabase {
    private static DatabaseConnection dbConnection;

    public AccessoryDatabase() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public void insertAccessory(BouquetAccessory accessory) {
        String sql = "INSERT INTO accessories (name, cost) VALUES (?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accessory.getName());
            pstmt.setDouble(2, accessory.getCost());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting accessory", e);
        }
    }

    public void removeAccessory(String accessoryName) {
        String sql = "DELETE FROM accessories WHERE name = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accessoryName);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting accessory failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error removing accessory", e);
        }
    }

    public static int getAccessoryId(BouquetAccessory accessory) {
        String sql = "SELECT id FROM accessories WHERE name = ? AND cost = ?";
        try (Connection conn = dbConnection.getConnection();
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
        try (Connection conn = dbConnection.getConnection();
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
        try (Connection conn = dbConnection.getConnection();
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