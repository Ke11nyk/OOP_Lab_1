package lab.database.interfaces;

import lab.bouquets.Bouquet;

import java.util.List;

public interface IBouquetDatabase {
    boolean insertBouquet(Bouquet bouquet, String bouquetName);

    int getBouquetId(Bouquet bouquet);

    Bouquet getBouquetByName(String name);

    List<Bouquet> getAllBouquets();

    void removeBouquet(String name);
}