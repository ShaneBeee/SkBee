package com.shanebeestudios.skbee.api.util;

import java.util.List;
import java.util.Random;
import java.util.TreeMap;

/**
 * A weighted list that allows for random selection based on entry weights.
 *
 * @param <T> The type of elements in the list.
 */
public class WeightedList<T> {

    private final TreeMap<Double, T> weightMap = new TreeMap<>();
    private final Random random = new Random();
    private double total = 0;

    /**
     * Add an entry to the weighted list with a specified weight.
     *
     * @param entry  The entry to add.
     * @param weight The weight of the entry.
     * @throws IllegalArgumentException if weight is not positive.
     */
    public void add(T entry, int weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        this.total += weight;
        this.weightMap.put(this.total, entry);
    }

    /**
     * Get the next entry from the weighted list based on weights.
     *
     * @return The next entry or null if the list is empty.
     */
    public T nextEntry() {
        if (this.total == 0) {
            return null;
        }
        double randomValue = this.random.nextDouble() * this.total;
        return this.weightMap.higherEntry(randomValue).getValue();
    }

    /**
     * Get all entries in the weighted list.
     *
     * @return An unmodifiable list of entries.
     */
    public List<T> getEntries() {
        return List.copyOf(this.weightMap.values());
    }

    /**
     * Check if the weighted list is empty.
     *
     * @return True if the list is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.weightMap.isEmpty();
    }

    /**
     * Get the number of entries in the weighted list.
     *
     * @return The number of entries.
     */
    public int size() {
        return this.weightMap.size();
    }

}
