package com.shanebeestudios.skbee.api.property;

import com.shanebeestudios.skbee.api.property.Property.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class BlockProperties {

    static void init() {

        Properties.registerProperty(List.of(Block.class, Entity.class), Location.class,"location")
                        .getter(new Getter<>() {
                            @Override
                            public @Nullable Location get(Object object) {
                                if (object instanceof Block block) return block.getLocation();
                                else if (object instanceof Entity entity) return entity.getLocation();
                                return null;
                            }
                        });
        // Block Sounds
        Properties.registerProperty(Block.class, String.class, "break sound")
                .description("Get the break sound of a block.")
                .getter(new Getter<>() {
                    @Override
                    public String get(Block block) {
                        return block.getBlockData().getSoundGroup().getBreakSound().toString().toLowerCase(Locale.ROOT);
                    }
                });

        Properties.registerProperty(Block.class, String.class, "fall sound")
                .description("Get the fall sound of a block.")
                .getter(new Getter<>() {
                    @Override
                    public String get(Block block) {
                        return block.getBlockData().getSoundGroup().getFallSound().toString().toLowerCase(Locale.ROOT);
                    }
                });

        Properties.registerProperty(Block.class, String.class, "hit sound")
                .description("Get the hit sound of a block.")
                .getter(new Getter<>() {
                    @Override
                    public String get(Block block) {
                        return block.getBlockData().getSoundGroup().getHitSound().toString().toLowerCase(Locale.ROOT);
                    }
                });

        Properties.registerProperty(Block.class, String.class, "place sound")
                .description("Get the place sound of a block.")
                .getter(new Getter<>() {
                    @Override
                    public String get(Block block) {
                        return block.getBlockData().getSoundGroup().getPlaceSound().toString().toLowerCase(Locale.ROOT);
                    }
                });

        Properties.registerProperty(Block.class, String.class, "step sound")
                .description("Get the step sound of a block.")
                .getter(new Getter<>() {
                    @Override
                    public String get(Block block) {
                        return block.getBlockData().getSoundGroup().getStepSound().toString().toLowerCase(Locale.ROOT);
                    }
                });
    }

}
