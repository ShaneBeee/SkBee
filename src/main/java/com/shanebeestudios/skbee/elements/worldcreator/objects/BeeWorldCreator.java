package com.shanebeestudios.skbee.elements.worldcreator.objects;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import com.shanebeestudios.skbee.SkBee;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class BeeWorldCreator {

    private final String worldName;
    private WorldType worldType;
    private Environment environment;
    private String generatorSettings;
    private String generator;
    Optional<Boolean> genStructures;
    Optional<Boolean> hardcore;
    Optional<Boolean> keepSpawnLoaded;
    long seed = -1;

    private World world;
    private boolean clone;

    public BeeWorldCreator(String worldName) {
        this.worldName = worldName;
        this.genStructures = Optional.empty();
        this.hardcore = Optional.empty();
        this.keepSpawnLoaded = Optional.empty();
    }

    public BeeWorldCreator(World world, String name, boolean clone) {
        this.worldName = name;
        //noinspection deprecation
        this.worldType = world.getWorldType();
        this.environment = world.getEnvironment();
        this.genStructures = Optional.of(world.canGenerateStructures());
        this.hardcore = Optional.of(world.isHardcore());
        this.keepSpawnLoaded = Optional.of(world.getKeepSpawnInMemory());
        this.seed = world.getSeed();
        this.world = world;
        this.clone = clone;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldType(WorldType worldType) {
        this.worldType = worldType;
    }

    public WorldType getWorldType() {
        return worldType;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public long getSeed() {
        return seed;
    }

    public void setGeneratorSettings(String generatorSettings) {
        this.generatorSettings = generatorSettings;
    }

    public String getGeneratorSettings() {
        return generatorSettings;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public void setGenStructures(boolean genStructures) {
        this.genStructures = Optional.of(genStructures);
    }

    public boolean isGenStructures() {
        return genStructures.orElse(true);
    }

    public boolean isHardcore() {
        return this.hardcore.orElse(false);
    }

    public void setHardcore(boolean hardcore) {
        this.hardcore = Optional.of(hardcore);
    }

    public boolean isKeepSpawnLoaded() {
        return keepSpawnLoaded.orElse(false);
    }

    public void setKeepSpawnLoaded(boolean loaded) {
        this.keepSpawnLoaded = Optional.of(loaded);
    }

    public World loadWorld() {
        World world;
        WorldCreator worldCreator;
        // Copy/Clone world
        if (this.world != null) {
            worldCreator = clone ? cloneWorld() : copyWorld();
        }
        // Create new world
        else {
            worldCreator = new WorldCreator(this.worldName);
        }
        if (worldCreator == null) return null;


        if (worldType != null) {
            worldCreator.type(worldType);
        }
        if (environment != null) {
            worldCreator.environment(environment);
        }
        if (seed > -1) {
            worldCreator.seed(seed);
        }

        if (generatorSettings != null) {
            worldCreator.generatorSettings(generatorSettings);
        }
        if (generator != null) {
            worldCreator.generator(generator);
        }

        genStructures.ifPresent(worldCreator::generateStructures);
        hardcore.ifPresent(worldCreator::hardcore);

        world = worldCreator.createWorld();
        if (world != null) {
            // Let's pull some values from the world and update our creator if need be
            if (worldType == null) {
                //noinspection deprecation
                worldType = world.getWorldType();
            }
            if (environment == null) {
                environment = world.getEnvironment();
            }
            if (seed == -1) {
                seed = world.getSeed();
            }
            if (!genStructures.isPresent()) {
                genStructures = Optional.of(world.canGenerateStructures());
            }
            if (!hardcore.isPresent()) {
                hardcore = Optional.of(world.isHardcore());
            }

            // Let's update the world with some other values
            keepSpawnLoaded.ifPresent(world::setKeepSpawnInMemory);
        }

        SkBee.getPlugin().getBeeWorldConfig().saveWorldToFile(this);
        return world;
    }

    private WorldCreator copyWorld() {
        WorldCreator worldCreator = new WorldCreator(this.worldName);
        worldCreator.copy(this.world);
        return worldCreator;
    }

    private WorldCreator cloneWorld() {
        File worldSaveLocation = Bukkit.getWorldContainer();
        File worldFile = this.world.getWorldFolder();
        File newWorldFile = new File(worldSaveLocation, this.worldName);
        if (worldFile.exists()) {
            try {
                this.world.save();
                FileUtils.copyDirectory(worldFile, newWorldFile);
                File uuidFile = new File(newWorldFile, "uid.dat");
                if (uuidFile.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    uuidFile.delete();
                }
                return new WorldCreator(this.worldName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WorldCreator{");
        sb.append("worldName='").append(worldName).append('\'');
        sb.append(", worldType=").append(worldType);
        sb.append(", environment=").append(environment);
        sb.append(", generatorSettings='").append(generatorSettings).append('\'');
        sb.append(", generator='").append(generator).append('\'');
        sb.append(", genStructures=").append(genStructures);
        sb.append(", hardcore=").append(hardcore);
        sb.append(", keepSpawnLoaded=").append(keepSpawnLoaded);
        sb.append(", seed=").append(seed);
        sb.append(", world=").append(world);
        sb.append(", clone=").append(clone);
        sb.append('}');
        return sb.toString();
    }

}
