package lab.flowers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

class FlowerTests {

    @Nested
    class FlowerTest {
        @Test
        void testFlowerConstructor() {
            Flower flower = new Flower("Rose", 30, 8);
            assertEquals("Rose", flower.getName());
            assertEquals(30, flower.getStemLength());
            assertEquals(8, flower.getFreshnessLevel());
        }

        @Test
        void testSetName() {
            Flower flower = new Flower("Rose", 30, 8);
            flower.setName("Sunflower");
            assertEquals("Sunflower", flower.getName());
        }

        @Test
        void testSetValidStemLength() {
            Flower flower = new Flower("Rose", 30, 8);
            flower.setStemLength(1);
            assertEquals(1, flower.getStemLength());
            flower.setStemLength(50);
            assertEquals(50, flower.getStemLength());
            flower.setStemLength(100);
            assertEquals(100, flower.getStemLength());
        }

        @Test
        void testSetInvalidStemLength() {
            Flower flower = new Flower("Rose", 30, 8);
            assertThrows(IllegalArgumentException.class, () -> flower.setStemLength(0));
            assertThrows(IllegalArgumentException.class, () -> flower.setStemLength(-1));
        }

        @Test
        void testSetValidFreshnessLevel() {
            Flower flower = new Flower("Rose", 30, 8);
            flower.setFreshnessLevel(1);
            assertEquals(1, flower.getFreshnessLevel());
            flower.setFreshnessLevel(5);
            assertEquals(5, flower.getFreshnessLevel());
            flower.setFreshnessLevel(10);
            assertEquals(10, flower.getFreshnessLevel());
        }

        @Test
        void testSetInvalidFreshnessLevel() {
            Flower flower = new Flower("Rose", 30, 8);
            assertThrows(IllegalArgumentException.class, () -> flower.setFreshnessLevel(0));
            assertThrows(IllegalArgumentException.class, () -> flower.setFreshnessLevel(11));
            assertThrows(IllegalArgumentException.class, () -> flower.setFreshnessLevel(-1));
        }

        @Test
        void testGetCost() {
            Flower flower = new Flower("Rose", 30, 8);
            assertEquals(240, flower.getCost());
        }
    }

    @Nested
    class LilyTest {
        @Test
        void testLilyConstructor() {
            Lily lily = new Lily(25, 7, 6, true);
            assertEquals("Lily", lily.getName());
            assertEquals(25, lily.getStemLength());
            assertEquals(7, lily.getFreshnessLevel());
            assertEquals(6, lily.getPetalCount());
            assertTrue(lily.isFragrant());
        }

        @Test
        void testGetCostFragrant() {
            Lily lily = new Lily(25, 7, 6, true);
            assertEquals(192.5, lily.getCost(), 0.01);
        }

        @Test
        void testGetCostNotFragrant() {
            Lily lily = new Lily(25, 7, 6, false);
            assertEquals(175, lily.getCost(), 0.01);
        }
    }

    @Nested
    class RoseTest {
        @Test
        void testRoseConstructor() {
            Rose rose = new Rose(28, 9, true);
            assertEquals("Rose", rose.getName());
            assertEquals(28, rose.getStemLength());
            assertEquals(9, rose.getFreshnessLevel());
            assertTrue(rose.isThorny());
        }

        @Test
        void testGetCostThorny() {
            Rose rose = new Rose(28, 9, true);
            assertEquals(302.4, rose.getCost(), 0.01);
        }

        @Test
        void testGetCostNotThorny() {
            Rose rose = new Rose(28, 9, false);
            assertEquals(252, rose.getCost(), 0.01);
        }
    }

    @Nested
    class TulipTest {
        @Test
        void testTulipConstructor() {
            Tulip tulip = new Tulip(22, 6, "Single", true);
            assertEquals("Tulip", tulip.getName());
            assertEquals(22, tulip.getStemLength());
            assertEquals(6, tulip.getFreshnessLevel());
            assertEquals("Single", tulip.getBloomType());
            assertTrue(tulip.hasStiffStem());
        }

        @Test
        void testGetCostStiffStem() {
            Tulip tulip = new Tulip(22, 6, "Single", true);
            assertEquals(151.8, tulip.getCost(), 0.01);
        }

        @Test
        void testGetCostNotStiffStem() {
            Tulip tulip = new Tulip(22, 6, "Single", false);
            assertEquals(132, tulip.getCost(), 0.01);
        }
    }
}