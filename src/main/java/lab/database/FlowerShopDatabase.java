package lab.database;

import lab.flowers.*;
import lab.bouquets.Bouquet;
import lab.bouquets.BouquetAccessory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import io.github.cdimascio.dotenv.Dotenv;

public class FlowerShopDatabase {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    public FlowerShopDatabase() {
        try {
            Class.forName("org.postgresql.Driver");
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
        String sql = "INSERT INTO flowers (type, name, stem_length, freshness_level, petal_count, fragrant, thorny, bloom_type, stiff_stem) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, flower.getClass().getSimpleName());
            pstmt.setString(2, flower.getName());
            pstmt.setInt(3, flower.getStemLength());
            pstmt.setInt(4, flower.getFreshnessLevel());

            if (flower instanceof Lily) {
                Lily lily = (Lily) flower;
                pstmt.setInt(5, lily.getPetalCount());
                pstmt.setBoolean(6, lily.isFragrant());
                pstmt.setNull(7, Types.BOOLEAN);
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setNull(9, Types.BOOLEAN);
            } else if (flower instanceof Rose) {
                Rose rose = (Rose) flower;
                pstmt.setNull(5, Types.INTEGER);
                pstmt.setNull(6, Types.BOOLEAN);
                pstmt.setBoolean(7, rose.isThorny());
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setNull(9, Types.BOOLEAN);
            } else if (flower instanceof Tulip) {
                Tulip tulip = (Tulip) flower;
                pstmt.setNull(5, Types.INTEGER);
                pstmt.setNull(6, Types.BOOLEAN);
                pstmt.setNull(7, Types.BOOLEAN);
                pstmt.setString(8, tulip.getBloomType());
                pstmt.setBoolean(9, tulip.hasStiffStem());
            } else {
                pstmt.setNull(5, Types.INTEGER);
                pstmt.setNull(6, Types.BOOLEAN);
                pstmt.setNull(7, Types.BOOLEAN);
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setNull(9, Types.BOOLEAN);
            }

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting flower", e);
        }
    }

    public List<Flower> getAllFlowers() {
        List<Flower> flowers = new ArrayList<>();
        String sql = "SELECT * FROM flowers";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String type = rs.getString("type");
                String name = rs.getString("name");
                int stemLength = rs.getInt("stem_length");
                int freshnessLevel = rs.getInt("freshness_level");

                Flower flower;
                switch (type) {
                    case "Lily":
                        int petalCount = rs.getInt("petal_count");
                        boolean fragrant = rs.getBoolean("fragrant");
                        flower = new Lily(stemLength, freshnessLevel, petalCount, fragrant);
                        break;
                    case "Rose":
                        boolean thorny = rs.getBoolean("thorny");
                        flower = new Rose(stemLength, freshnessLevel, thorny);
                        break;
                    case "Tulip":
                        String bloomType = rs.getString("bloom_type");
                        boolean stiffStem = rs.getBoolean("stiff_stem");
                        flower = new Tulip(stemLength, freshnessLevel, bloomType, stiffStem);
                        break;
                    default:
                        flower = new Flower(name, stemLength, freshnessLevel);
                }
                flowers.add(flower);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving flowers", e);
        }
        return flowers;
    }

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
                pstmt.setInt(2, getFlowerId(flower));
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
                pstmt.setInt(2, getAccessoryId(accessory));
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

    public int getFlowerId(Flower flower) {
        String sql = "SELECT id FROM flowers WHERE name = ? AND stem_length = ? AND freshness_level = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, flower.getName());
            pstmt.setInt(2, flower.getStemLength());
            pstmt.setInt(3, flower.getFreshnessLevel());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new RuntimeException("Flower not found in database");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting flower ID", e);
        }
    }

    private int getAccessoryId(BouquetAccessory accessory) {
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

    public void removeFlower(int flowerId) {
        String sql = "DELETE FROM flowers WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, flowerId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error removing flower", e);
        }
    }

    public void removeBouquet(int bouquetId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);

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