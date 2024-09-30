package lab.database;

import lab.bouquets.Bouquet;
import lab.bouquets.BouquetAccessory;
import lab.database.interfaces.IBouquetDatabase;
import lab.flowers.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import io.github.cdimascio.dotenv.Dotenv;

public class BouquetDatabase implements IBouquetDatabase {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    public boolean insertBouquet(Bouquet bouquet, String bouquetName) {
        Connection conn = null;
        boolean success = false;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(false);

            if (isBouquetNameUnique(conn, bouquetName)) {
                int bouquetId = insertBouquetAndGetId(conn, bouquetName);
                insertBouquetFlowers(conn, bouquetId, bouquet.getFlowers());
                insertBouquetAccessories(conn, bouquetId, bouquet.getAccessories());
                conn.commit();
                success = true;
            } else {
                System.out.println("A bouquet with the name '" + bouquetName + "' already exists. Please choose a different name.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error inserting bouquet: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Error closing database connection: " + e.getMessage());
                }
            }
        }
        return success;
    }

    public void removeBouquet(String name) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);

            // First, get the bouquet ID
            int bouquetId;
            String selectSql = "SELECT id FROM bouquets WHERE name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
                pstmt.setString(1, name);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        bouquetId = rs.getInt("id");
                    } else {
                        throw new RuntimeException("Bouquet not found: " + name);
                    }
                }
            }

            // Now delete from related tables using the bouquet ID
            String[] sqls = {
                    "DELETE FROM bouquet_flowers WHERE bouquet_id = ?",
                    "DELETE FROM bouquet_accessories WHERE bouquet_id = ?",
                    "DELETE FROM bouquets WHERE id = ?"
            };

            for (String sql : sqls) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, bouquetId);
                    pstmt.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing bouquet", e);
        }
    }

    private boolean isBouquetNameUnique(Connection conn, String bouquetName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bouquets WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bouquetName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        }
        return true;
    }

    private int insertBouquetAndGetId(Connection conn, String bouquetName) throws SQLException {
        String sql = "INSERT INTO bouquets (name) VALUES (?) RETURNING id";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bouquetName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Failed to retrieve bouquet ID after insertion");
            }
        }
    }

    private void insertBouquetFlowers(Connection conn, int bouquetId, List<Flower> flowers) throws SQLException {
        String sql = "INSERT INTO bouquet_flowers (bouquet_id, flower_id, quantity) " +
                "VALUES (?, ?, 1) " +
                "ON CONFLICT (bouquet_id, flower_id) DO UPDATE SET quantity = bouquet_flowers.quantity + 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Flower flower : flowers) {
                pstmt.setInt(1, bouquetId);
                pstmt.setInt(2, FlowerDatabase.getFlowerId(flower));
                pstmt.executeUpdate();
            }
        }
    }

    private void insertBouquetAccessories(Connection conn, int bouquetId, List<BouquetAccessory> accessories) throws SQLException {
        String sql = "INSERT INTO bouquet_accessories (bouquet_id, accessory_id, quantity) " +
                "VALUES (?, ?, 1) " +
                "ON CONFLICT (bouquet_id, accessory_id) DO UPDATE SET quantity = bouquet_accessories.quantity + 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (BouquetAccessory accessory : accessories) {
                pstmt.setInt(1, bouquetId);
                pstmt.setInt(2, AccessoryDatabase.getAccessoryId(accessory));
                pstmt.executeUpdate();
            }
        }
    }

    private void loadBouquetFlowers(Bouquet bouquet, int bouquetId) {
        String sql = "SELECT f.*, bf.quantity FROM flowers f " +
                "JOIN bouquet_flowers bf ON f.id = bf.flower_id " +
                "WHERE bf.bouquet_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bouquetId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Flower flower = createFlowerFromResultSet(rs);
                    int quantity = rs.getInt("quantity");
                    for (int i = 0; i < quantity; i++) {
                        bouquet.addFlower(flower);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading bouquet flowers", e);
        }
    }

    private void loadBouquetAccessories(Bouquet bouquet, int bouquetId) {
        String sql = "SELECT a.*, ba.quantity FROM accessories a " +
                "JOIN bouquet_accessories ba ON a.id = ba.accessory_id " +
                "WHERE ba.bouquet_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bouquetId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    double cost = rs.getDouble("cost");
                    int quantity = rs.getInt("quantity");
                    BouquetAccessory accessory = new BouquetAccessory(name, cost);
                    for (int i = 0; i < quantity; i++) {
                        bouquet.addAccessory(accessory);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading bouquet accessories", e);
        }
    }

    public int getBouquetId(Bouquet bouquet) {
        String sql = "SELECT id FROM bouquets WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bouquet.getName());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new RuntimeException("Bouquet not found: " + bouquet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting bouquet ID", e);
        }
    }

    public Bouquet getBouquetByName(String name) {
        List<Bouquet> bouquets = getAllBouquets();
        for (Bouquet bouquet : bouquets) {
            if (bouquet.getName().equalsIgnoreCase(name)) {
                return bouquet;
            }
        }
        return null;
    }

    public List<Bouquet> getAllBouquets() {
        List<Bouquet> bouquets = new ArrayList<>();
        String sql = "SELECT * FROM bouquets";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                Bouquet bouquet = new Bouquet();
                bouquet.setName(name);
                loadBouquetFlowers(bouquet, id);
                loadBouquetAccessories(bouquet, id);
                bouquets.add(bouquet);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving bouquets", e);
        }
        return bouquets;
    }

    private Flower createFlowerFromResultSet(ResultSet rs) throws SQLException {
        String type = rs.getString("type");
        String name = rs.getString("name");
        int stemLength = rs.getInt("stem_length");
        int freshnessLevel = rs.getInt("freshness_level");

        switch (type) {
            case "Lily":
                int petalCount = rs.getInt("petal_count");
                boolean fragrant = rs.getBoolean("fragrant");
                return new Lily(stemLength, freshnessLevel, petalCount, fragrant);
            case "Rose":
                boolean thorny = rs.getBoolean("thorny");
                return new Rose(stemLength, freshnessLevel, thorny);
            case "Tulip":
                String bloomType = rs.getString("bloom_type");
                boolean stiffStem = rs.getBoolean("stiff_stem");
                return new Tulip(stemLength, freshnessLevel, bloomType, stiffStem);
            default:
                return new Flower(name, stemLength, freshnessLevel);
        }
    }
}
