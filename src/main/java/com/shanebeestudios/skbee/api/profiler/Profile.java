package com.shanebeestudios.skbee.api.profiler;

public class Profile {

    private final String name;
    private final String type;
    private long start;
    private long total;
    private int timesCounted;

    Profile(String name, String type) {
        this.name = name;
        this.type = type;
    }

    void start() {
        this.start = System.currentTimeMillis();
    }

    void stop() {
        long finish = System.currentTimeMillis() - this.start;
        this.total += finish;
        this.timesCounted++;
    }

    private int getAverage() {
        return (int) (this.total / this.timesCounted);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        int average = getAverage();
        String avg = average > 50 ? "&c" : average > 25 ? "&e" : "&a";
        // Format = "[type-name](count) average ms"
        return String.format("&7[&b%s-%s&7]&7(&b%s&7) %s%s&7ms",
                this.type, this.name, this.timesCounted, avg, average);
    }

}
