package lab.menu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import lab.database.FlowerShopDatabase;
import lab.flowers.*;
import lab.bouquets.Bouquet;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class FlowerOptionsTest {

    @Mock
    private FlowerShopDatabase database;

    @Mock
    private InputReader inputReader;

    private FlowerOptions flowerOptions;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        flowerOptions = new FlowerOptions(database, inputReader);
    }

    @Test
    void testAddFlower() {
        when(inputReader.readInt(anyString())).thenReturn(1, 30, 8, 6);
        when(inputReader.readBoolean(anyString())).thenReturn(true);

        flowerOptions.addFlower();

        verify(database).insertFlower(any(Lily.class));
    }

    @Test
    void testRemoveFlower() {
        when(inputReader.readInt(anyString())).thenReturn(1);

        flowerOptions.removeFlower();

        verify(database).removeFlower(1);
    }

    @Test
    void testAddFlowerToBouquet() {
        Bouquet bouquet = new Bouquet();
        List<Flower> flowers = Arrays.asList(
                new Rose(30, 8, true),
                new Lily(25, 7, 6, true)
        );
        when(database.getAllFlowers()).thenReturn(flowers);
        when(inputReader.readInt(anyString())).thenReturn(1);

        flowerOptions.addFlowerToBouquet(bouquet);

        verify(database).getAllFlowers();
        assertEquals(1, bouquet.getFlowers().size());
        assertTrue(bouquet.getFlowers().get(0) instanceof Rose);
    }

    @Test
    void testDisplayAllFlowers() {
        List<Flower> flowers = Arrays.asList(
                new Rose(30, 8, true),
                new Lily(25, 7, 6, true)
        );
        when(database.getAllFlowers()).thenReturn(flowers);
        when(database.getFlowerId(any())).thenReturn(1, 2);

        flowerOptions.displayAllFlowers();

        verify(database).getAllFlowers();
        verify(database, times(2)).getFlowerId(any());
    }
}