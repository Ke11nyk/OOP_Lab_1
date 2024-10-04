package lab.flowers;

import java.util.Comparator;
import java.util.List;

public class Flower {
    private String name;
    private int stemLength;
    private int freshnessLevel;

    // Constructor
    public Flower(String name, int stemLength, int freshnessLevel) {
        this.name = name;
        setStemLength(stemLength);
        setFreshnessLevel(freshnessLevel);
    }

    /**
     * Sorts the given list of flowers using the provided comparator.
     * @param flowers The list of flowers to sort.
     * @param comparator The comparator to use for sorting.
     */
    public static void sort(List<Flower> flowers, Comparator<Flower> comparator) {
        flowers.sort(comparator);
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for stemLength
    public int getStemLength() {
        return stemLength;
    }

    // Setter for stemLength
    public void setStemLength(int stemLength) {
        if (stemLength > 0) {
            this.stemLength = stemLength;
        } else {
            throw new IllegalArgumentException("Stem length must be positive");
        }
    }

    // Getter for freshnessLevel
    public int getFreshnessLevel() {
        return freshnessLevel;
    }

    // Setter for freshnessLevel
    public void setFreshnessLevel(int freshnessLevel) {
        if (freshnessLevel >= 1 && freshnessLevel <= 10) {
            this.freshnessLevel = freshnessLevel;
        } else {
            throw new IllegalArgumentException("Freshness level must be between 1 and 10");
        }
    }

    // Method to calculate the value of the flower
    public double getCost() {
        return stemLength * freshnessLevel;
    }
}