package com.shanebeestudios.skbee.elements.generator.structure;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
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
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.lang.entry.EntryContainer;
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
    "`noise gen` = Generate the base terrain of a chunk.",
    "`surface gen` = Generate the surface above the terrain of the chunk.",
    "`chunk gen` = A combination of noise and surface gen (Cannot be used WITH noise/surface gen).",
    "`biome gen` = Generate the biomes to be placed in a chunk.",
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
        SimpleEntryValidator builder = SimpleEntryValidator.builder();
        builder.addOptionalEntry("vanilla decor", Boolean.class);
        builder.addOptionalEntry("vanilla caves", Boolean.class);
        builder.addOptionalEntry("vanilla structures", Boolean.class);
        builder.addOptionalEntry("vanilla mobs", Boolean.class);
        builder.addOptionalSection("noise gen");
        builder.addOptionalSection("surface gen");
        builder.addOptionalSection("chunk gen");
        builder.addOptionalSection("biome gen");
        builder.addOptionalSection("height gen");
        builder.addOptionalSection("block pop");
        Skript.registerStructure(StructChunkGen.class, builder.build(), "register chunk generator with id %string%");
    }

    private Literal<String> id;

    private EntryContainer entryContainer;
    private Expression<Boolean> vanillaDecor;
    private Expression<Boolean> vanillaCaves;
    private Expression<Boolean> vanillaStructures;
    private Expression<Boolean> vanillaMobs;
    private Trigger noiseGenSection;
    private Trigger surfaceGenSection;
    private Trigger chunkGenSection;
    private Trigger biomeGenSection;
    private Trigger heightGenSection;
    private Trigger blockPopSection;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult, EntryContainer entryContainer) {
        this.id = (Literal<String>) args[0];
        if (entryContainer == null) return false;
        this.entryContainer = entryContainer;
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean preLoad() {
        this.vanillaDecor = (Expression<Boolean>) this.entryContainer.getOptional("vanilla decor", false);
        this.vanillaCaves = (Expression<Boolean>) this.entryContainer.getOptional("vanilla caves", false);
        this.vanillaStructures = (Expression<Boolean>) this.entryContainer.getOptional("vanilla structures", false);
        this.vanillaMobs = (Expression<Boolean>) this.entryContainer.getOptional("vanilla mobs", false);

        Script currentScript = getParser().getCurrentScript();
        SectionNode noiseNode = this.entryContainer.getOptional("noise gen", SectionNode.class, false);
        if (noiseNode != null) {
            getParser().setCurrentEvent("noise gen section", ChunkGenEvent.class);
            this.noiseGenSection = new Trigger(currentScript, "noise gen", new SimpleEvent(), ScriptLoader.loadItems(noiseNode));
        }
        SectionNode surfaceNode = this.entryContainer.getOptional("surface gen", SectionNode.class, false);
        if (surfaceNode != null) {
            getParser().setCurrentEvent("surface gen section", ChunkGenEvent.class);
            this.surfaceGenSection = new Trigger(currentScript, "surface gen", new SimpleEvent(), ScriptLoader.loadItems(surfaceNode));
        }

        SectionNode chunkNode = this.entryContainer.getOptional("chunk gen", SectionNode.class, false);
        if (chunkNode != null) {
            if (noiseNode != null) {
                Skript.error("Cannot use a 'chunk gen' section with a 'noise gen' section");
                return false;
            }
            if (surfaceNode != null) {
                Skript.error("Cannot use a 'chunk gen' section with a 'surface gen' section");
                return false;
            }
            getParser().setCurrentEvent("chunk gen section", ChunkGenEvent.class);
            this.chunkGenSection = new Trigger(currentScript, "chunk gen", new SimpleEvent(), ScriptLoader.loadItems(chunkNode));
        }

        SectionNode biomeNode = this.entryContainer.getOptional("biome gen", SectionNode.class, false);
        if (biomeNode != null) {
            getParser().setCurrentEvent("biome gen section", BiomeGenEvent.class);
            this.biomeGenSection = new Trigger(currentScript, "biome gen", new SimpleEvent(), ScriptLoader.loadItems(biomeNode));
        }

        SectionNode heightNode = this.entryContainer.getOptional("height gen", SectionNode.class, false);
        if (heightNode != null) {
            getParser().setCurrentEvent("height gen section", HeightGenEvent.class);
            this.heightGenSection = new Trigger(currentScript, "height gen", new SimpleEvent(), ScriptLoader.loadItems(heightNode));
        }

        SectionNode blockPopNode = this.entryContainer.getOptional("block pop", SectionNode.class, false);
        if (blockPopNode != null) {
            getParser().setCurrentEvent("block pop section", BlockPopulateEvent.class);
            this.blockPopSection = new Trigger(currentScript, "block pop", new SimpleEvent(), ScriptLoader.loadItems(blockPopNode));
        }

        return true;
    }

    @Override
    public boolean load() {
        ChunkGen chunkGen = ChunkGenManager.registerOrGetGenerator(this.id.getSingle(), biomeGenSection != null);

        ChunkGenerator chunkGenerator = chunkGen.getChunkGenerator();
        if (chunkGenerator != null) {
            boolean vanillaDecor = this.vanillaDecor != null && this.vanillaDecor.getOptionalSingle(null).orElse(false);
            chunkGenerator.setVanillaDecor(vanillaDecor);
            boolean vanillaCaves = this.vanillaCaves != null && this.vanillaCaves.getOptionalSingle(null).orElse(false);
            chunkGenerator.setVanillaCaves(vanillaCaves);
            boolean vanillaStructures = this.vanillaStructures != null && this.vanillaStructures.getOptionalSingle(null).orElse(false);
            chunkGenerator.setVanillaStructures(vanillaStructures);
            boolean vanillaMobs = this.vanillaMobs != null && this.vanillaMobs.getOptionalSingle(null).orElse(false);
            chunkGenerator.setVanillaMobs(vanillaMobs);

            chunkGenerator.setNoiseGenTrigger(this.noiseGenSection);
            chunkGenerator.setSurfaceGenTrigger(this.surfaceGenSection);
            chunkGenerator.setChunkGenTrigger(this.chunkGenSection);
            chunkGenerator.setBlockPopTrigger(this.blockPopSection);
            chunkGenerator.setHeightGenTrigger(this.heightGenSection);
        }

        if (this.biomeGenSection != null) {
            BiomeGenerator biomeGenerator = chunkGen.getBiomeGenerator();
            if (biomeGenerator != null) {
                biomeGenerator.setTrigger(this.biomeGenSection);
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
