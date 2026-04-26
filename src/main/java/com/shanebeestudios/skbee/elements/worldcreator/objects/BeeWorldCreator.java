package com.shanebeestudios.skbee.elements.worldcreator.objects;

import ch.njol.skript.Skript;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.region.TaskUtils;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.util.legacy.LegacyUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "CallToPrintStackTrace"})
public class BeeWorldCreator implements Keyed {

    private String worldName;
    private NamespacedKey key;
    private WorldType worldType;
    private Environment environment;
    private String generatorSettings;
    private String generator;
    private ChunkGenerator chunkGenerator;
    private BiomeProvider biomeProvider;
    Optional<Boolean> genStructures = Optional.empty();
    Optional<Boolean> hardcore = Optional.empty();
    Optional<Boolean> loadOnStart = Optional.empty();
    private Location fixedSpawnLocation;
    boolean isLoaded;
    long seed = -1;

    private World world;
    private final boolean clone;
    private boolean saveClone;

    @SuppressWarnings("deprecation")
    public BeeWorldCreator(@Nullable World world, @Nullable String name, @Nullable NamespacedKey key, boolean clone) {
        this.worldName = name;
        this.key = key;
        if (world != null) {
            this.worldType = world.getWorldType();
            this.environment = world.getEnvironment();
            this.genStructures = Optional.of(world.canGenerateStructures());
            this.hardcore = Optional.of(world.isHardcore());
            this.seed = world.getSeed();
            this.world = world;
        }
        this.clone = clone;
    }

    public @NotNull NamespacedKey getKey() {
        if (this.key == null) {
            String key = this.worldName.toLowerCase(Locale.ROOT).replace(" ", "_");
            this.key = NamespacedKey.minecraft(key);
        }
        return this.key;
    }

    public String getWorldName() {
        if (this.worldName == null) {
            if (this.world == null) {
                return getKey().toString();
            }
            this.worldName = this.world.getName();
        }
        return this.worldName;
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

    public void setChunkGenerator(ChunkGenerator chunkGenerator) {
        this.chunkGenerator = chunkGenerator;
    }

    public void setBiomeProvider(BiomeProvider biomeProvider) {
        this.biomeProvider = biomeProvider;
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

    public void setFixedSpawnLocation(Location fixedSpawnLocation) {
        this.fixedSpawnLocation = fixedSpawnLocation;
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

    @SuppressWarnings({"deprecation", "CallToPrintStackTrace"})
    public CompletableFuture<World> loadWorld() {
        CompletableFuture<WorldCreator> creatorFuture = new CompletableFuture<>();
        CompletableFuture<World> worldFuture = new CompletableFuture<>();

        if (this.world != null) {
            // Copy/Clone world
            creatorFuture = this.clone ? cloneWorld() : copyWorld();
        } else {
            // Create new world
            creatorFuture.complete(getWorldCreator(this.worldName, this.key));
        }
        creatorFuture.thenAccept(worldCreator -> {
            World world = null;

            if (this.worldType != null) {
                worldCreator.type(this.worldType);
            }

            if (this.environment != null) {
                worldCreator.environment(this.environment);
            }

            if (this.seed != -1) {
                worldCreator.seed(this.seed);
            }

            if (this.generatorSettings != null) {
                worldCreator.generatorSettings(this.generatorSettings);
            }

            if (this.generator != null) {
                worldCreator.generator(this.generator);
            }

            if (this.fixedSpawnLocation != null) {
                if (this.chunkGenerator != null) {
                    if (this.chunkGenerator instanceof com.shanebeestudios.skbee.api.generator.ChunkGenerator customChunkGenerator) {
                        customChunkGenerator.setFixedSpawnLocation(this.fixedSpawnLocation);
                    }
                } else {
                    this.chunkGenerator = getDummyGenerator(this.fixedSpawnLocation);
                }
            }

            if (this.chunkGenerator != null) {
                worldCreator.generator(this.chunkGenerator);
            }

            if (this.biomeProvider != null) {
                worldCreator.biomeProvider(this.biomeProvider);
            }

            this.genStructures.ifPresent(worldCreator::generateStructures);
            this.hardcore.ifPresent(worldCreator::hardcore);

            try {
                world = worldCreator.createWorld();
            } catch (Exception ex) {
                ex.printStackTrace();
                Util.errorForAdmins("Failed to load world '%s' see console for more details.", this.worldName);
            }

            if (world != null) {
                // Let's pull some values from the world and update our creator if need be
                if (this.worldType == null) {
                    this.worldType = world.getWorldType();
                }
                if (this.environment == null) {
                    this.environment = world.getEnvironment();
                }
                if (this.seed == -1) {
                    this.seed = world.getSeed();
                }
                if (this.genStructures.isEmpty()) {
                    this.genStructures = Optional.of(world.canGenerateStructures());
                }
                if (this.hardcore.isEmpty()) {
                    this.hardcore = Optional.of(world.isHardcore());
                }
                if (this.key == null || LegacyUtils.IS_RUNNING_MC_26_1_1) {
                    this.key = world.getKey();
                }
            }

            SkBee.getPlugin().getBeeWorldConfig().saveWorldToFile(this);
            worldFuture.complete(world);
        }).exceptionally(t -> {
            throw Skript.exception(t);
        });
        return worldFuture;
    }

    private CompletableFuture<WorldCreator> copyWorld() {
        CompletableFuture<WorldCreator> worldCreatorCompletableFuture = new CompletableFuture<>();
        WorldCreator worldCreator = getWorldCreator(this.worldName, this.key);
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
        TaskUtils.getGlobalScheduler().runTaskAsync(() -> {
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
                    WorldCreator creator = getWorldCreator(cloneName, this.key);
                    TaskUtils.getGlobalScheduler().runTaskLater(() -> {
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

    private static WorldCreator getWorldCreator(@Nullable String name, @Nullable NamespacedKey key) {
        if (name != null) {
            return new WorldCreator(name);
        } else if (key != null) {
            return new WorldCreator(key);
        } else {
            throw new IllegalArgumentException("Either name or key must be specified");
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WorldCreator{");
        sb.append("worldName='").append(worldName).append('\'');
        if (this.key != null) sb.append(", key='").append(key).append('\'');
        sb.append(", worldType=").append(worldType);
        sb.append(", environment=").append(environment);
        sb.append(", generatorSettings='").append(generatorSettings).append('\'');
        sb.append(", generator='").append(generator).append('\'');
        sb.append(", genStructures=").append(genStructures);
        sb.append(", hardcore=").append(hardcore);
        sb.append(", seed=").append(seed);
        sb.append(", world=").append(world);
        sb.append(", clone=").append(clone);
        sb.append('}');
        return sb.toString();
    }

    /**
     * This is used to set the fixed spawn location of a WorldCreator
     * Currently Bukkit/Paper APIs don't actually have any other method for this.
     * Setting this will GREATLY speed up world creation
     *
     * @param fixedSpawnLocation Spawn location
     * @return New dummy chunk generator
     */
    private static ChunkGenerator getDummyGenerator(Location fixedSpawnLocation) {
        return new ChunkGenerator() {
            @Override
            public Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
                Location clone = fixedSpawnLocation.clone();
                clone.setWorld(world);
                return clone;
            }

            @Override
            public boolean shouldGenerateNoise() {
                return true;
            }

            @Override
            public boolean shouldGenerateSurface() {
                return true;
            }

            @Override
            public boolean shouldGenerateCaves() {
                return true;
            }

            @Override
            public boolean shouldGenerateMobs() {
                return true;
            }

            @Override
            public boolean shouldGenerateDecorations() {
                return true;
            }

            @Override
            public boolean shouldGenerateStructures() {
                return true;
            }
        };
    }

}
