package com.shanebeestudios.skbee.elements.other.structures;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.Aliases;
import ch.njol.skript.aliases.AliasesParser;
import ch.njol.skript.aliases.ScriptAliases;
import ch.njol.skript.config.EntryNode;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.tags.TagModule;
import org.skriptlang.skript.bukkit.tags.TagType;
import org.skriptlang.skript.bukkit.tags.sources.TagOrigin;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.script.Script;
import org.skriptlang.skript.lang.structure.Structure;

import java.util.ArrayList;
import java.util.List;

@Name("Tag Aliases")
@Description({"Create item/block aliases that use Minecraft tags.",
    "Supports paper and datapack tags as well.",
    "Custom Skript tags will not work here as they're registered after this structure loads."})
@Examples({"item tag aliases:",
    "\t[any] tool[s] = minecraft:axes, minecraft:pickaxes, minecraft:shovels, minecraft:hoes, minecraft:bundles, paper:buckets",
    "\t[any] enchantable[s] = paper:enchantable",
    "\t[any] cool item = my_pack:cool_items",
    "",
    "block tag aliases:",
    "\t[any] wall[s] = minecraft:walls",
    "\t[any] leaves = minecraft:leaves",
    "\t[any] log[s] = minecraft:logs",
    "",
    "# Using the same alias in Skript's aliases structure will add to your tag alias",
    "aliases:",
    "\t[any] tool[s] = shears, brush",
    "",
    "# Aliases can be used to compare items/blocks",
    "on break:",
    "\tif player's tool is any tool:",
    "\tif player's tool is any enchantable:",
    "\tif event-block is any log:",
    "",
    "# Aliases can be used in events",
    "on right click holding any tool:",
    "on break of any wall:",
    "on break of any leaves:",
    "on break of any logs:"})
@Since("INSERT VERSION")
public class StructTagAliases extends Structure {

    public static final Priority PRIORITY = new Priority(201);

    static {
        Skript.registerStructure(StructTagAliases.class, "[(:block|item)] tag aliases");
    }

    private TagType<Material> tagType;

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult, @Nullable EntryContainer entryContainer) {
        if (entryContainer == null) return false;

        SectionNode rootNode = entryContainer.getSource();
        rootNode.convertToEntries(0, "=");
        this.tagType = parseResult.hasTag("block") ? TagType.BLOCKS : TagType.ITEMS;

        // Initialize and load script aliases
        Script script = getParser().getCurrentScript();
        ScriptAliases scriptAliases = Aliases.getScriptAliases(script);
        if (scriptAliases == null)
            scriptAliases = Aliases.createScriptAliases(script);
        for (Node node : rootNode) {
            if (node instanceof EntryNode entryNode) {
                if (!loadAlias(rootNode, scriptAliases.parser, entryNode.getKey(), entryNode.getValue(), node.getLine())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean loadAlias(SectionNode parent, AliasesParser parser, String key, String value, int lineNum) {
        SectionNode fakeSectionNode = new SectionNode("tag aliases", "", parent, lineNum);

        List<String> ids = new ArrayList<>();

        for (String s : value.split(",")) {
            String tagName = s.trim();
            NamespacedKey tagKey = Util.getNamespacedKey(tagName, false);
            if (tagKey != null) {
                Tag<Material> tag = TagModule.tagRegistry.getTag(TagOrigin.ANY, this.tagType, tagKey);
                if (tag != null) {
                    tag.getValues().forEach(material -> ids.add(material.getKey().toString()));
                } else {
                    Skript.error("Invalid " + this.tagType.toString() + " tag '" + tagName + "'");
                    return false;
                }
            } else {
                Skript.error("Invalid " + this.tagType.toString() + " tag '" + tagName + "'");
                return false;
            }
        }
        String joinedIds = String.join(", ", ids);
        EntryNode node = new EntryNode(key, joinedIds, fakeSectionNode);
        fakeSectionNode.add(node);

        parser.load(fakeSectionNode);
        return true;
    }

    @Override
    public Priority getPriority() {
        return PRIORITY;
    }

    @Override
    public boolean load() {
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return this.tagType.toString() + " tag aliases";
    }

}
