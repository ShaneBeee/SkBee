package com.shanebeestudios.skbee.elements.generator.structure;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.util.SimpleEvent;
import com.shanebeestudios.skbee.api.generator.BiomeGenerator;
import com.shanebeestudios.skbee.api.generator.ChunkGen;
import com.shanebeestudios.skbee.api.generator.ChunkGenManager;
import com.shanebeestudios.skbee.api.generator.ChunkGenerator;
import com.shanebeestudios.skbee.api.generator.event.BiomeGenEvent;
import com.shanebeestudios.skbee.api.generator.event.BlockPopulateEvent;
import com.shanebeestudios.skbee.api.generator.event.ChunkGenEvent;
import com.shanebeestudios.skbee.api.generator.event.HeightGenEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.LiteralEntryData;
import org.skriptlang.skript.lang.script.Script;
import org.skriptlang.skript.lang.structure.Structure;

@Name("ChunkGenerator - Register Generator")
@Description({"Register a chunk generator to manipulate the world layout to your liking.",
    "ENTRIES:",
    "(These are all optional, and will default to false)",
    "`vanilla decor` = Whether Minecraft will decorate the surface based on biomes.",
    "`vanilla caves` = Whether Minecraft will carve caves.",
    "`vanilla structures` = Whether Minecraft will generate structures based on biomes.",
    "`vanilla mobs` = Whether Minecraft will spawn mobs based on biomes.",
    "SECTIONS:",
    "(These are all optional, but some do rely on others. `height gen` and `block pop` require `chunk gen`)",
    "`biome gen` = Generate the biomes to be placed in the world.",
    "`chunk gen` = Generate your surface layer of your world.",
    "`height gen` = Tell Minecraft where the highest block in a chunk is for generating structures.",
    "`block pop` = Used to decorate after initial surface is generated (Structures can be placed during this stage).",
    "NOTES:",
    "- `world-creator` needs to be enabled in the config",
    "- Please see the [**Chunk Generator**](https://github.com/ShaneBeee/SkBee/wiki/Chunk-Generator) wiki for further details."})
@Examples({"register chunk generator with id \"mars\":",
    "\tvanilla decor: false",
    "\tvanilla caves: false",
    "\tvanilla structures: false",
    "\tvanilla mobs: false",
    "\tchunk gen:",
    "\t\tloop 16 times:",
    "\t\t\tloop 16 times:",
    "\t\t\t\tset {_x} to (loop-number-1) - 1",
    "\t\t\t\tset {_z} to (loop-number-2) - 1",
    "",
    "\t\t\t\t# This is just an expression I created with reflect to give you an idea how it can work",
    "\t\t\t\tset {_y} to biome noise at vector({_x} + (chunkdata chunk x * 16), 1, {_z} + (chunkdata chunk z * 16))",
    "\t\t\t\t# Fill blocks from 0 to y level with concrete",
    "\t\t\t\tset chunkdata blocks within vector({_x}, 0, {_z}) and vector({_x}, {_y}, {_z}) to red_concrete[]",
    "\t\t\t\t# Set the surface layer to concrete powder",
    "\t\t\t\tset chunkdata block at vector({_x}, {_y}, {_z}) to red_concrete_powder[]",
    "",
    "\tbiome gen:",
    "\t\t# Set our biome to something mars like",
    "\t\tset chunkdata biome to crimson forest"})
@Since("3.5.0")
public class StructChunkGen extends Structure {

    private static final Priority PRIORITY = new Priority(450);

    static {
        EntryValidator validator = EntryValidator.builder()
            .addEntryData(new LiteralEntryData<>("vanilla decor", false, true, Boolean.class))
            .addEntryData(new LiteralEntryData<>("vanilla caves", false, true, Boolean.class))
            .addEntryData(new LiteralEntryData<>("vanilla structures", false, true, Boolean.class))
            .addEntryData(new LiteralEntryData<>("vanilla mobs", false, true, Boolean.class))
            .addSection("chunk gen", true)
            .addSection("biome gen", true)
            .addSection("height gen", true)
            .addSection("block pop", true)
            .build();
        Skript.registerStructure(StructChunkGen.class, validator, "register chunk generator with id %string%");
    }

    private Literal<String> id;
    private EntryContainer entryContainer;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult, EntryContainer entryContainer) {
        this.id = (Literal<String>) args[0];
        this.entryContainer = entryContainer;
        return true;
    }

    @Override
    public boolean load() {
        SectionNode chunkNode = this.entryContainer.getOptional("chunk gen", SectionNode.class, false);
        SectionNode biomeNode = this.entryContainer.getOptional("biome gen", SectionNode.class, false);
        SectionNode heightNode = this.entryContainer.getOptional("height gen", SectionNode.class, false);
        SectionNode blockNode = this.entryContainer.getOptional("block pop", SectionNode.class, false);

        Script currentScript = getParser().getCurrentScript();
        ChunkGen chunkGen = ChunkGenManager.registerOrGetGenerator(this.id.getSingle(), chunkNode != null, biomeNode != null);

        if (chunkNode != null) {
            ChunkGenerator chunkGenerator = chunkGen.getChunkGenerator();
            if (chunkGenerator != null) {
                boolean vanillaDecor = Boolean.TRUE.equals(this.entryContainer.getOptional("vanilla decor", Boolean.class, true));
                chunkGenerator.setVanillaDecor(vanillaDecor);
                boolean vanillaCaves = Boolean.TRUE.equals(this.entryContainer.getOptional("vanilla caves", Boolean.class, true));
                chunkGenerator.setVanillaCaves(vanillaCaves);
                boolean vanillaStructures = Boolean.TRUE.equals(this.entryContainer.getOptional("vanilla structures", Boolean.class, true));
                chunkGenerator.setVanillaStructures(vanillaStructures);
                boolean vanillaMobs = Boolean.TRUE.equals(this.entryContainer.getOptional("vanilla mobs", Boolean.class, true));
                chunkGenerator.setVanillaMobs(vanillaMobs);

                getParser().setCurrentEvent("ChunkGenSection", ChunkGenEvent.class);
                Trigger chunkTrigger = new Trigger(currentScript, "chunk gen", new SimpleEvent(), ScriptLoader.loadItems(chunkNode));
                chunkTrigger.setLineNumber(chunkNode.getLine());
                chunkGenerator.setChunkGenTrigger(chunkTrigger);

                if (blockNode != null) {
                    getParser().setCurrentEvent("BlockPopulateSection", BlockPopulateEvent.class);
                    Trigger blockTrigger = new Trigger(currentScript, "block pop", new SimpleEvent(), ScriptLoader.loadItems(blockNode));
                    blockTrigger.setLineNumber(blockNode.getLine());
                    chunkGenerator.setBlockPopTrigger(blockTrigger);
                }

                if (heightNode != null) {
                    getParser().setCurrentEvent("HeightGenSection", HeightGenEvent.class);
                    Trigger heightTrigger = new Trigger(currentScript, "height gen", new SimpleEvent(), ScriptLoader.loadItems(heightNode));
                    heightTrigger.setLineNumber(heightTrigger.getLineNumber());
                    chunkGenerator.setHeightGenTrigger(heightTrigger);
                }
            }
        }

        if (biomeNode != null) {
            BiomeGenerator biomeGenerator = chunkGen.getBiomeGenerator();
            if (biomeGenerator != null) {
                getParser().setCurrentEvent("BiomeGenSection", BiomeGenEvent.class);
                Trigger biomeTrigger = new Trigger(currentScript, "biome gen", new SimpleEvent(), ScriptLoader.loadItems(biomeNode));
                biomeTrigger.setLineNumber(biomeTrigger.getLineNumber());
                biomeGenerator.setTrigger(biomeTrigger);
            }
        }
        return true;
    }

    @Override
    public Priority getPriority() {
        //<editor-fold desc="Note on priorities" defaultstate="collapsed">
        // NOTE FOR SELF:
        // Load after all Skript stuff so it can be used in this structure
        // But BEFORE events (so the world creator can be loaded after this is created)
        // 15 - StructUsing
        // 100 - StructOptions
        // 150 - StructImport
        // 200 - StructAliases
        // 300 - StructVariables
        // 350 - StructCustomCondition
        // 350 - StructCustomConstant
        // 350 - StructCustomEffect
        // 350 - StructCustomEvent
        // 350 - StructCustomExpression
        // 400 - StructFunction
        // 450 - StructChunkGen [this]
        // 500 - StructCommand
        // 600 - StructEvent
        //</editor-fold>
        return PRIORITY;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "register chunk generator with id " + this.id.toString(e, d);
    }

}
