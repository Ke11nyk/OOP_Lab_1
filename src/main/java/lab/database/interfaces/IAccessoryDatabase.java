package lab.database.interfaces;

import lab.bouquets.BouquetAccessory;

import java.util.List;

public interface IAccessoryDatabase {
    void insertAccessory(BouquetAccessory accessory);

    static int getAccessoryId(BouquetAccessory accessory) {
        return 0;
    }

    BouquetAccessory getAccessoryByName(String name);

    List<BouquetAccessory> getAllAccessories();
}