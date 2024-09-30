package lab.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import lab.bouquets.Bouquet;
import lab.bouquets.BouquetAccessory;
import lab.flowers.Flower;
import lab.flowers.Rose;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlowerShopDatabaseTest {

    @Mock
    private FlowerDatabase mockFlowerDatabase;

    @Mock
    private BouquetDatabase mockBouquetDatabase;

    @Mock
    private AccessoryDatabase mockAccessoryDatabase;

    private FlowerShopDatabase flowerShopDatabase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        flowerShopDatabase = new FlowerShopDatabase();
        flowerShopDatabase.flowerDatabase = mockFlowerDatabase;
        flowerShopDatabase.bouquetDatabase = mockBouquetDatabase;
        flowerShopDatabase.accessoryDatabase = mockAccessoryDatabase;
    }

    @Test
    void testInsertFlower() {
        Flower flower = new Rose(30, 5, true);
        flowerShopDatabase.insertFlower(flower);
        verify(mockFlowerDatabase).insertFlower(flower);
    }

    @Test
    void testRemoveFlower() {
        int flowerId = 1;
        flowerShopDatabase.removeFlower(flowerId);
        verify(mockFlowerDatabase).removeFlower(flowerId);
    }

    @Test
    void testGetAllFlowers() {
        List<Flower> expectedFlowers = Arrays.asList(new Rose(30, 5, true), new Rose(25, 4, false));
        when(mockFlowerDatabase.getAllFlowers()).thenReturn(expectedFlowers);
        List<Flower> result = flowerShopDatabase.getAllFlowers();
        assertEquals(expectedFlowers, result);
    }

    @Test
    void testInsertBouquet() {
        Bouquet bouquet = new Bouquet();
        String bouquetName = "Test Bouquet";
        when(mockBouquetDatabase.insertBouquet(bouquet, bouquetName)).thenReturn(true);
        boolean result = flowerShopDatabase.insertBouquet(bouquet, bouquetName);
        assertTrue(result);
    }

    @Test
    void testGetBouquetId() {
        Bouquet bouquet = new Bouquet();
        when(mockBouquetDatabase.getBouquetId(bouquet)).thenReturn(1);
        int result = flowerShopDatabase.getBouquetId(bouquet);
        assertEquals(1, result);
    }

    @Test
    void testGetAllBouquets() {
        List<Bouquet> expectedBouquets = Arrays.asList(new Bouquet(), new Bouquet());
        when(mockBouquetDatabase.getAllBouquets()).thenReturn(expectedBouquets);
        List<Bouquet> result = flowerShopDatabase.getAllBouquets();
        assertEquals(expectedBouquets, result);
    }

    @Test
    void testRemoveBouquet() {
        String bouquetName = "Test Bouquet";
        flowerShopDatabase.removeBouquet(bouquetName);
        verify(mockBouquetDatabase).removeBouquet(bouquetName);
    }

    @Test
    void testInsertAccessory() {
        BouquetAccessory accessory = new BouquetAccessory("Ribbon", 2.5);
        flowerShopDatabase.insertAccessory(accessory);
        verify(mockAccessoryDatabase).insertAccessory(accessory);
    }

    @Test
    void testGetAccessoryByName() {
        String accessoryName = "Ribbon";
        BouquetAccessory expectedAccessory = new BouquetAccessory(accessoryName, 2.5);
        when(mockAccessoryDatabase.getAccessoryByName(accessoryName)).thenReturn(expectedAccessory);
        BouquetAccessory result = flowerShopDatabase.getAccessoryByName(accessoryName);
        assertEquals(expectedAccessory, result);
    }

    @Test
    void testGetAllAccessories() {
        List<BouquetAccessory> expectedAccessories = Arrays.asList(
                new BouquetAccessory("Ribbon", 2.5),
                new BouquetAccessory("Wrapping Paper", 1.5)
        );
        when(mockAccessoryDatabase.getAllAccessories()).thenReturn(expectedAccessories);
        List<BouquetAccessory> result = flowerShopDatabase.getAllAccessories();
        assertEquals(expectedAccessories, result);
    }
}