package lab.menu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import lab.database.FlowerShopDatabase;
import lab.bouquets.*;
import lab.flowers.*;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class BouquetOptionsTest {

    @Mock
    private FlowerShopDatabase database;

    @Mock
    private InputReader inputReader;

    @Mock
    private FlowerOptions flowerOptions;

    @Mock
    private AccessoryOptions accessoryOptions;

    private BouquetOptions bouquetOptions;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bouquetOptions = new BouquetOptions(database, inputReader, flowerOptions, accessoryOptions);
    }

    @Test
    void testCreateBouquet() {
        when(inputReader.readString(anyString())).thenReturn("Spring Bouquet");
        when(inputReader.readInt(anyString())).thenReturn(1, 2, 3);

        bouquetOptions.createBouquet();

        verify(flowerOptions).addFlowerToBouquet(any(Bouquet.class));
        verify(accessoryOptions).addAccessoryToBouquet(any(Bouquet.class));
        verify(database).insertBouquet(any(Bouquet.class), eq("Spring Bouquet"));
    }

    @Test
    void testRemoveBouquet() {
        when(inputReader.readString(anyString())).thenReturn("Spring Bouquet");

        bouquetOptions.removeBouquet();

        verify(database).removeBouquet("Spring Bouquet");
    }

    @Test
    void testDisplayAllBouquets() {
        Bouquet bouquet = new Bouquet();
        bouquet.addFlower(new Rose(30, 8, true));
        bouquet.addAccessory(new BouquetAccessory("Ribbon", 2.5));
        List<Bouquet> bouquets = Arrays.asList(bouquet);

        when(database.getAllBouquets()).thenReturn(bouquets);

        bouquetOptions.displayAllBouquets();

        verify(database).getAllBouquets();
    }

    @Test
    void testSortBouquetFlowers() {
        Bouquet bouquet = new Bouquet();
        bouquet.addFlower(new Rose(30, 8, true));
        bouquet.addFlower(new Lily(25, 7, 6, true));

        when(inputReader.readString(anyString())).thenReturn("Spring Bouquet");
        when(database.getBouquetByName("Spring Bouquet")).thenReturn(bouquet);

        bouquetOptions.sortBouquetFlowers();

        verify(database).getBouquetByName("Spring Bouquet");
    }
}