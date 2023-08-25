package com.shanebeestudios.skbee.elements.worldcreator.objects;

import com.shanebeestudios.skbee.SkBee;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
    Optional<Boolean> loadOnStart = Optional.empty();
    boolean isLoaded;
    long seed = -1;

    private World world;
    private boolean clone;
    private boolean saveClone;

    public BeeWorldCreator(String worldName) {
        this.worldName = worldName;
        this.genStructures = Optional.empty();
        this.hardcore = Optional.empty();
        this.keepSpawnLoaded = Optional.empty();
    }

    @SuppressWarnings("deprecation")
    public BeeWorldCreator(@NotNull World world, String name, boolean clone) {
        this.worldName = name;
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

    public boolean isLoadOnStart() {
        return loadOnStart.orElse(true);
    }

    public void setLoadOnStart(boolean loadOnStart) {
        this.loadOnStart = Optional.of(loadOnStart);
    }

    public boolean isSaveClone() {
        return saveClone;
    }

    public void setSaveClone(boolean saveClone) {
        this.saveClone = saveClone;
    }

    @SuppressWarnings("deprecation")
    public CompletableFuture<World> loadWorld() {
        CompletableFuture<WorldCreator> worldCreatorCompletableFuture = new CompletableFuture<>();
        CompletableFuture<World> worldCompletableFuture = new CompletableFuture<>();
        // Copy/Clone world
        if (this.world != null) {
            worldCreatorCompletableFuture = clone ? cloneWorld() : copyWorld();
        }
        // Create new world
        else {
            worldCreatorCompletableFuture.complete(new WorldCreator(this.worldName));
        }
        worldCreatorCompletableFuture.thenAccept(worldCreator -> {
            World world;

            if (worldType != null) {
                worldCreator.type(worldType);
            }
            if (environment != null) {
                worldCreator.environment(environment);
            }
            if (seed != -1) {
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
                    worldType = world.getWorldType();
                }
                if (environment == null) {
                    environment = world.getEnvironment();
                }
                if (seed == -1) {
                    seed = world.getSeed();
                }
                if (genStructures.isEmpty()) {
                    genStructures = Optional.of(world.canGenerateStructures());
                }
                if (hardcore.isEmpty()) {
                    hardcore = Optional.of(world.isHardcore());
                }

                // Let's update the world with some other values
                keepSpawnLoaded.ifPresent(world::setKeepSpawnInMemory);
            }

            SkBee.getPlugin().getBeeWorldConfig().saveWorldToFile(this);
            worldCompletableFuture.complete(world);
        });
        return worldCompletableFuture;
    }

    private CompletableFuture<WorldCreator> copyWorld() {
        CompletableFuture<WorldCreator> worldCreatorCompletableFuture = new CompletableFuture<>();
        WorldCreator worldCreator = new WorldCreator(this.worldName);
        worldCreator.copy(this.world);
        worldCreatorCompletableFuture.complete(worldCreator);
        return worldCreatorCompletableFuture;
    }

    private CompletableFuture<WorldCreator> cloneWorld() {
        File worldContainer = Bukkit.getWorldContainer();
        File worldDirectorToClone = this.world.getWorldFolder();
        String cloneName = this.worldName;

        // Saving causes a bit of lag, we may want to disable this
        if (isSaveClone()) this.world.save();

        // Let's clone files on another thread
        CompletableFuture<WorldCreator> worldCompletableFuture = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(SkBee.getPlugin(), () -> {
            File cloneDirectory = new File(worldContainer, cloneName);
            if (worldDirectorToClone.exists()) {
                try {
                    for (File file : Objects.requireNonNull(worldDirectorToClone.listFiles())) {
                        String fileName = file.getName();
                        if (file.isDirectory()) {
                            FileUtils.copyDirectory(file, new File(cloneDirectory, fileName));
                        } else if (!fileName.contains("session") && !fileName.contains("uid.dat")) {
                            FileUtils.copyFile(file, new File(cloneDirectory, fileName));
                        }
                    }
                    WorldCreator creator = new WorldCreator(cloneName);
                    Bukkit.getScheduler().runTaskLater(SkBee.getPlugin(), () -> {
                        // Let's head back to the main thread
                        worldCompletableFuture.complete(creator);
                    }, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return worldCompletableFuture;
    }

    @SuppressWarnings("StringBufferReplaceableByString")
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
