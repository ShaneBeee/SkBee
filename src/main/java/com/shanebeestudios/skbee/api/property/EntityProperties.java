package com.shanebeestudios.skbee.api.property;

import com.shanebeestudios.skbee.api.property.Property.Adder;
import com.shanebeestudios.skbee.api.property.Property.Getter;
import com.shanebeestudios.skbee.api.property.Property.Remover;
import com.shanebeestudios.skbee.api.property.Property.Setter;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Bee;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;

public class EntityProperties {

    static void init() {
        // Armor Stands
        Properties.registerProperty(ArmorStand.class, Boolean.class, "arms")
                .description("Whether the armor stand's arms will show.")
                .getter(new Getter<>() {
                    @Override
                    public Boolean get(ArmorStand armorStand) {
                        return armorStand.hasArms();
                    }
                }).setter(new Setter<>() {
                    @Override
                    public void set(ArmorStand armorStand, Boolean value) {
                        armorStand.setArms(value);
                    }
                });

        Properties.registerProperty(ArmorStand.class, Boolean.class, "baseplate")
                .description("Whether the armor stand's baseplate will show.")
                .getter(new Getter<>() {
                    @Override
                    public Boolean get(ArmorStand armorStand) {
                        return armorStand.hasBasePlate();
                    }
                }).setter(new Setter<>() {
                    @Override
                    public void set(ArmorStand armorStand, Boolean value) {
                        armorStand.setBasePlate(value);
                    }
                });

        Properties.registerProperty(ArmorStand.class, Boolean.class, "marker")
                .description("Whether the armor stand is a marker.")
                .getter(new Getter<>() {
                    @Override
                    public Boolean get(ArmorStand armorStand) {
                        return armorStand.isMarker();
                    }
                }).setter(new Setter<>() {
                    @Override
                    public void set(ArmorStand armorStand, Boolean value) {
                        armorStand.setMarker(value);
                    }
                });

        Properties.registerProperty(ArmorStand.class, Boolean.class, "small")
                .getter(new Getter<>() {
                    @Override
                    public Boolean get(ArmorStand armorStand) {
                        return armorStand.isSmall();
                    }
                }).setter(new Setter<>() {
                    @Override
                    public void set(ArmorStand armorStand, Boolean value) {
                        armorStand.setSmall(value);
                    }
                });

        // Bee
        Properties.registerProperty(Bee.class, Location.class, "hive location")
                .description("Location of a bee's hive.")
                .getter(new Getter<>() {
                    @Override
                    public Location get(Bee bee) {
                        return bee.getHive();
                    }
                }).setter(new Setter<>() {
                    @Override
                    public void set(Bee bee, Location value) {
                        bee.setHive(value);
                    }
                });

        // LivingEntity
        Properties.registerProperty(LivingEntity.class, Boolean.class, "collidable")
                .description("Whether this entity will be subject to collisions with other entities.")
                .getter(new Getter<>() {
                    @Override
                    public Boolean get(LivingEntity livingEntity) {
                        return livingEntity.isCollidable();
                    }
                }).setter(new Setter<>() {
                    @Override
                    public void set(LivingEntity livingEntity, Boolean value) {
                        livingEntity.setCollidable(value);
                    }
                });

        // Zombie
        Properties.registerProperty(Zombie.class, Number.class, "conversion time")
                .getter(new Getter<>() {
                    @Override
                    public Number get(Zombie zombie) {
                        return zombie.getConversionTime();
                    }
                }).setter(new Setter<>() {
                    @Override
                    public void set(Zombie zombie, Number value) {
                        zombie.setConversionTime(value.intValue());
                    }
                }).adder(new Adder<>() {
                    @Override
                    public void add(Zombie zombie, Number value) {
                        zombie.setConversionTime(zombie.getConversionTime() + value.intValue());
                    }
                }).remover(new Remover<>() {
                    @Override
                    public void remove(Zombie zombie, Number value) {
                        zombie.setConversionTime(zombie.getConversionTime() - value.intValue());
                    }
                });
    }

}
