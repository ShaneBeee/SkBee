package com.shanebeestudios.skbee.api.beacon;

import java.util.HashMap;

public enum BeaconTier {

    TIER_0(0),
    TIER_1(1),
    TIER_2(2),
    TIER_3(3),
    TIER_4(4);

    private final int tier;
    private static final HashMap<Integer, BeaconTier> BY_INT = new HashMap<>();

    static {
        for (BeaconTier tier : values()) {
            BY_INT.put(tier.getTier(), tier);
        }
    }

    BeaconTier(int tier) {
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    public static BeaconTier getTierFromInt(int tier) {
        return BY_INT.get(tier);
    }

}
