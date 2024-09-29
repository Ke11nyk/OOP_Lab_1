package lab.database;

import lab.database.interfaces.IFlowerDatabase;
import lab.flowers.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import io.github.cdimascio.dotenv.Dotenv;

public class FlowerDatabase implements IFlowerDatabase {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

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

    public static int getFlowerId(Flower flower) {
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
}
