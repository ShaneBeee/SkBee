package com.shanebeestudios.skbee.elements.worldgen.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.github.shanebeee.skr.skript.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.worldgen.BeeWorldCreator;
import com.shanebeestudios.skbee.api.worldgen.ChunkGen;
import com.shanebeestudios.skbee.api.worldgen.ChunkGenManager;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

public class ExprWorldCreatorSection extends SectionExpression<BeeWorldCreator> {

    private static EntryValidator VALIDATOR;

    @SuppressWarnings("unchecked")
    public static void register(Registration reg) {
        VALIDATOR = SimpleEntryValidator.builder()
            .addRequiredEntry("key", new Class[]{String.class, NamespacedKey.class})
            .addOptionalEntry("seed", Number.class)
            .addOptionalEntry("world_type", WorldType.class)
            .addOptionalEntry("environment", World.Environment.class)
            .addOptionalEntry("generator_settings", String.class)
            .addOptionalEntry("structures", Boolean.class)
            .addOptionalEntry("hardcore", Boolean.class)
            .addOptionalEntry("load_on_start", Boolean.class)
            .addOptionalEntry("spawn_location", Location.class)
            .addOptionalEntry("chunk_generator", String.class)
            .addOptionalEntry("copy_world", World.class)
            .addOptionalEntry("clone_world", World.class)
            .build();

        reg.newSimpleExpression(ExprWorldCreatorSection.class, BeeWorldCreator.class,
                "[[a ]new] world creator")
            .validator(VALIDATOR)
            .name("World Creator - Create Section")
            .description("Create a new world creator with several options to customize.",
                "**Entires**:",
                " - `key` = Represents the NamespacedKey of your world (required, accepts NamespacedKey/String).",
                " - `seed` = Represents the seed if your world [optional, defaults to random].",
                " - `world_type` = The WorldType of your world [optional, defaults to normal].",
                " - `environment` = The Environment of your world [optional, defaults to normal].",
                " - `generator_settings` = The generator settings of your world used for flat worlds [optional String].",
                " - `structures` = Whether to generate vanilla structures in the world [optional Boolean, defaults to true].",
                " - `hardcore` = Whether to enable hardcore mode in the world [optional Boolean, defaults to false].",
                " - `load_on_start` = Whether to load the world automatically on server start [optional Boolean, defaults to false].",
                " - `spawn_location` = The spawn location of the world [optional Location, Minecraft will try to find one for you].",
                " - `chunk_generator` = A custom chunk generator [optional String, ID of a custom chunk generator].",
                " - `copy_world` = A World to copy settings from [optional World].",
                " - `clone_world` = A World to fully clone [optional World].")
            .examples("set {_w} to new world creator:",
                "\tkey: \"my_worlds:cool_world\"",
                "\tworld_type: large_biomes",
                "\tenvironment: nether",
                "\tstructures: false",
                "\thardcore: false",
                "\tseed: 12345",
                "\tclone_world: world(\"world\")",
                "",
                "set {_w} to new world creator:",
                "\tkey: \"my_worlds:fancy_world\"",
                "\tworld_type: normal",
                "\tenvironment: normal",
                "\tstructures: true",
                "\tseed: 9999",
                "\tchunk_generator: \"my_custom_generator\"")
            .since("3.22.0")
            .register();
    }

    private Expression<?> key;
    private Expression<Number> seed;
    private Expression<WorldType> worldType;
    private Expression<World.Environment> environment;
    private Expression<String> generatorSettings;
    private Expression<Boolean> structures;
    private Expression<Boolean> hardcore;
    private Expression<Boolean> loadOnStart;
    private Expression<Location> spawnLocation;
    private Expression<World> copyWorld;
    private Expression<World> cloneWorld;
    private Expression<String> chunkGenerator;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int pattern, Kleenean delayed, ParseResult result,
                        @Nullable SectionNode node, @Nullable List<TriggerItem> triggerItems) {
        EntryContainer validate = VALIDATOR.validate(node);
        if (validate == null) {
            Skript.error("Invalid world creator section. Please check your syntax and try again.");
            return false;
        }

        this.key = (Expression<?>) validate.getOptional("key", false);
        this.seed = (Expression<Number>) validate.getOptional("seed", false);
        this.worldType = (Expression<WorldType>) validate.getOptional("world_type", false);
        this.environment = (Expression<World.Environment>) validate.getOptional("environment", false);
        this.generatorSettings = (Expression<String>) validate.getOptional("generator_settings", false);
        this.structures = (Expression<Boolean>) validate.getOptional("structures", false);
        this.hardcore = (Expression<Boolean>) validate.getOptional("hardcore", false);
        this.loadOnStart = (Expression<Boolean>) validate.getOptional("load_on_start", false);
        this.spawnLocation = (Expression<Location>) validate.getOptional("spawn_location", false);
        this.copyWorld = (Expression<World>) validate.getOptional("copy_world", false);
        this.cloneWorld = (Expression<World>) validate.getOptional("clone_world", false);
        if (this.cloneWorld != null && this.copyWorld != null) {
            Skript.error("You cannot use copy and clone together.");
            return false;
        }
        this.chunkGenerator = (Expression<String>) validate.getOptional("chunk_generator", false);
        return true;
    }

    @Override
    protected BeeWorldCreator @Nullable [] get(Event event) {
        NamespacedKey key = Util.getNamespacedKey(this.key.getSingle(event), true);

        World world = null;
        boolean clone = false;
        if (this.copyWorld != null) {
            world = this.copyWorld.getSingle(event);
        } else if (this.cloneWorld != null) {
            world = this.cloneWorld.getSingle(event);
            clone = true;
        }

        BeeWorldCreator creator = new BeeWorldCreator(world, null, key, clone);
        if (this.seed != null) {
            Number seedNum = this.seed.getSingle(event);
            if (seedNum != null) {
                creator.setSeed(seedNum.longValue());
            }
        }
        if (this.worldType != null) {
            WorldType worldType = this.worldType.getSingle(event);
            creator.setWorldType(worldType);
        }
        if (this.environment != null) {
            World.Environment environment = this.environment.getSingle(event);
            creator.setEnvironment(environment);
        }
        if (this.generatorSettings != null) {
            String settings = this.generatorSettings.getSingle(event);
            creator.setGeneratorSettings(settings);
        }
        if (this.structures != null) {
            Boolean structures = this.structures.getSingle(event);
            if (structures != null) {
                creator.setGenStructures(structures);
            }
        }
        if (this.hardcore != null) {
            Boolean hardcore = this.hardcore.getSingle(event);
            if (hardcore != null) {
                creator.setHardcore(hardcore);
            }
        }
        if (this.loadOnStart != null) {
            Boolean loadOnStart = this.loadOnStart.getSingle(event);
            if (loadOnStart != null) {
                creator.setLoadOnStart(loadOnStart);
            }
        }
        if (this.spawnLocation != null) {
            Location spawnLocation = this.spawnLocation.getSingle(event);
            if (spawnLocation != null) {
                creator.setFixedSpawnLocation(spawnLocation);
            }
        }

        if (this.chunkGenerator != null) {
            String genKey = this.chunkGenerator.getSingle(event);
            ChunkGen generator = ChunkGenManager.getByID(genKey);
            if (generator != null) {
                creator.setChunkGenerator(generator.getChunkGenerator());
                creator.setBiomeProvider(generator.getBiomeGenerator());
            }
        }

        return new BeeWorldCreator[]{creator};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public boolean isSectionOnly() {
        return true;
    }

    @Override
    public Class<? extends BeeWorldCreator> getReturnType() {
        return BeeWorldCreator.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "new world creator";
    }

}
