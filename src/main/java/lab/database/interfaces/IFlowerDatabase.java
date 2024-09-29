package lab.database.interfaces;

import lab.flowers.Flower;

import java.util.List;

public interface IFlowerDatabase {
    void insertFlower(Flower flower);

    void removeFlower(int flowerId);

    static int getFlowerId(Flower flower) {
        return 0;
    }

    List<Flower> getAllFlowers();
}