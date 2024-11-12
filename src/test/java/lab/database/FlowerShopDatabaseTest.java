package lab.database;

import org.junit.jupiter.api.*;
import lab.bouquets.Bouquet;
import lab.bouquets.BouquetAccessory;
import lab.flowers.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FlowerShopDatabaseTest {

    private static FlowerShopDatabase db;

    @BeforeAll
    static void setUp() {
        db = new FlowerShopDatabase();
        db.createTables();
    }

    @AfterAll
    static void tearDown() {
        db.closeConnection();
    }

    @Test
    @Order(1)
    void testInsertFlower() {
        Flower rose = new Rose(30, 8, true);
        db.insertFlower(rose);
        List<Flower> flowers = db.getAllFlowers();
        assertTrue(flowers.stream().anyMatch(f -> f instanceof Rose
                && f.getStemLength() == 30
                && f.getFreshnessLevel() == 8
                && ((Rose) f).isThorny()));
    }

    @Test
    @Order(2)
    void testGetAllFlowers() {
        List<Flower> flowers = db.getAllFlowers();
        assertFalse(flowers.isEmpty());
    }

    @Test
    @Order(3)
    void testInsertAndGetAccessory() {
        BouquetAccessory ribbon = new BouquetAccessory("Ribbon", 2.50);
        db.insertAccessory(ribbon);
        BouquetAccessory retrievedRibbon = db.getAccessoryByName("Ribbon");
        assertNotNull(retrievedRibbon);
        assertEquals(ribbon.getName(), retrievedRibbon.getName());
        assertEquals(ribbon.getCost(), retrievedRibbon.getCost());
    }

    @Test
    @Order(4)
    void testGetAllAccessories() {
        List<BouquetAccessory> accessories = db.getAllAccessories();
        assertFalse(accessories.isEmpty());
    }

    @Test
    @Order(5)
    void testInsertAndGetBouquet() {
        Bouquet bouquet = new Bouquet();
        bouquet.addFlower(new Rose(30, 8, true));
        bouquet.addAccessory(new BouquetAccessory("Ribbon", 2.50));
        boolean inserted = db.insertBouquet(bouquet, "TestBouquet");
        assertTrue(inserted);

        Bouquet retrievedBouquet = db.getBouquetByName("TestBouquet");
        assertNotNull(retrievedBouquet);
        assertEquals(1, retrievedBouquet.getFlowers().size());
        assertEquals(1, retrievedBouquet.getAccessories().size());
    }

    @Test
    @Order(6)
    void testGetAllBouquets() {
        List<Bouquet> bouquets = db.getAllBouquets();
        assertFalse(bouquets.isEmpty());
    }

    @Test
    @Order(7)
    void testRemoveFlower() {
        List<Flower> flowers = db.getAllFlowers();
        if (!flowers.isEmpty()) {
            int flowerId = db.getFlowerId(flowers.get(0));
            db.removeFlower(flowerId);
            List<Flower> updatedFlowers = db.getAllFlowers();
            assertTrue(updatedFlowers.size() < flowers.size());
        }
    }

    @Test
    @Order(8)
    void testRemoveAccessory() {
        db.removeAccessory("Ribbon");
        BouquetAccessory removedAccessory = db.getAccessoryByName("Ribbon");
        assertNull(removedAccessory);
    }

    @Test
    @Order(9)
    void testRemoveBouquet() {
        db.removeBouquet("TestBouquet");
        Bouquet removedBouquet = db.getBouquetByName("TestBouquet");
        assertNull(removedBouquet);
    }
}