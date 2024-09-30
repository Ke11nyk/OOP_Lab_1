package lab.menu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import lab.database.FlowerShopDatabase;
import lab.bouquets.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AccessoryOptionsTest {

    @Mock
    private FlowerShopDatabase database;

    @Mock
    private InputReader inputReader;

    private AccessoryOptions accessoryOptions;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accessoryOptions = new AccessoryOptions(database, inputReader);
    }

    @Test
    void testAddAccessory() {
        when(inputReader.readString(anyString())).thenReturn("Ribbon");
        when(inputReader.readDouble(anyString())).thenReturn(2.5);

        accessoryOptions.addAccessory();

        verify(database).insertAccessory(any(BouquetAccessory.class));
    }

    @Test
    void testRemoveAccessory() {
        when(inputReader.readString(anyString())).thenReturn("Card");

        accessoryOptions.removeAccessory();

        verify(database).removeAccessory("Card");
    }

    @Test
    void testAddAccessoryToBouquet() {
        Bouquet bouquet = new Bouquet();
        List<BouquetAccessory> accessories = Arrays.asList(
                new BouquetAccessory("Ribbon", 2.5),
                new BouquetAccessory("Card", 1.0)
        );
        when(database.getAllAccessories()).thenReturn(accessories);
        when(inputReader.readInt(anyString())).thenReturn(1);

        accessoryOptions.addAccessoryToBouquet(bouquet);

        verify(database).getAllAccessories();
        assertEquals(1, bouquet.getAccessories().size());
        assertEquals("Ribbon", bouquet.getAccessories().get(0).getName());
    }

    @Test
    void testDisplayAllAccessories() {
        List<BouquetAccessory> accessories = Arrays.asList(
                new BouquetAccessory("Ribbon", 2.5),
                new BouquetAccessory("Card", 1.0)
        );
        when(database.getAllAccessories()).thenReturn(accessories);
        when(database.getAccessoryId(any())).thenReturn(1, 2);

        accessoryOptions.displayAllAccessories();

        verify(database).getAllAccessories();
        verify(database, times(2)).getAccessoryId(any());
    }
}