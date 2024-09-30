package lab.bouquets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

import lab.flowers.Flower;
import lab.flowers.Rose;
import lab.flowers.Lily;

class BouquetTests {

    @Nested
    class BouquetTest {
        private Bouquet bouquet;
        private Flower rose;
        private Flower lily;
        private BouquetAccessory ribbon;

        @BeforeEach
        void setUp() {
            bouquet = new Bouquet();
            rose = new Rose(30, 8, true);
            lily = new Lily(25, 7, 6, true);
            ribbon = new BouquetAccessory("Ribbon", 2.5);
        }

        @Test
        void testAddFlower() {
            bouquet.addFlower(rose);
            assertEquals(1, bouquet.getFlowers().size());
            assertTrue(bouquet.getFlowers().contains(rose));
        }

        @Test
        void testRemoveFlower() {
            bouquet.addFlower(rose);
            bouquet.removeFlower(rose);
            assertEquals(0, bouquet.getFlowers().size());
        }

        @Test
        void testAddAccessory() {
            bouquet.addAccessory(ribbon);
            assertEquals(1, bouquet.getAccessories().size());
            assertTrue(bouquet.getAccessories().contains(ribbon));
        }

        @Test
        void testRemoveAccessory() {
            bouquet.addAccessory(ribbon);
            bouquet.removeAccessory(ribbon);
            assertEquals(0, bouquet.getAccessories().size());
        }

        @Test
        void testGetTotalCost() {
            bouquet.addFlower(rose);
            bouquet.addFlower(lily);
            bouquet.addAccessory(ribbon);
            double expectedCost = rose.getCost() + lily.getCost() + ribbon.getCost();
            assertEquals(expectedCost, bouquet.getTotalCost(), 0.01);
        }

        @Test
        void testFindFlowerByStemLength() {
            bouquet.addFlower(rose);
            bouquet.addFlower(lily);
            Flower found = bouquet.findFlowerByStemLength(20, 28);
            assertNotNull(found);
            assertEquals(lily, found);
        }

        @Test
        void testFindFlowerByStemLengthNoMatch() {
            bouquet.addFlower(rose);
            bouquet.addFlower(lily);
            Flower found = bouquet.findFlowerByStemLength(40, 50);
            assertNull(found);
        }

        @Test
        void testSetAndGetName() {
            bouquet.setName("Spring Bouquet");
            assertEquals("Spring Bouquet", bouquet.getName());
        }
    }

    @Nested
    class BouquetAccessoryTest {
        @Test
        void testConstructorAndGetters() {
            BouquetAccessory accessory = new BouquetAccessory("Ribbon", 2.5);
            assertEquals("Ribbon", accessory.getName());
            assertEquals(2.5, accessory.getCost(), 0.01);
        }
    }

    @Nested
    class BouquetSorterTest {
        @Test
        void testSortByFreshness() {
            Bouquet bouquet = new Bouquet();
            Flower rose1 = new Rose(30, 8, true);
            Flower rose2 = new Rose(28, 6, false);
            Flower lily = new Lily(25, 9, 6, true);

            bouquet.addFlower(rose1);
            bouquet.addFlower(rose2);
            bouquet.addFlower(lily);

            BouquetSorter.sortByFreshness(bouquet);

            List<Flower> sortedFlowers = bouquet.getFlowers();
            assertEquals(lily, sortedFlowers.get(0));
            assertEquals(rose1, sortedFlowers.get(1));
            assertEquals(rose2, sortedFlowers.get(2));
        }
    }
}