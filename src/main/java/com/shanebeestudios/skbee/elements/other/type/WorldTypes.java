package com.shanebeestudios.skbee.elements.other.type;

import ch.njol.skript.registrations.Classes;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Chunk;
import org.bukkit.HeightMap;
import org.bukkit.TreeType;
import org.bukkit.entity.SpawnCategory;

public class WorldTypes {

    public static void register(Registration reg) {
        if (Classes.getExactClassInfo(Chunk.LoadLevel.class) == null) {
            reg.newEnumType(Chunk.LoadLevel.class, "chunkloadlevel", null, "level")
                .user("chunk ?load ?levels?")
                .name("Chunk Load Level")
                .description("Represents the types of load levels of a chunk.",
                    "- `border_level` = Most game logic is not processed, including entities and redstone.",
                    "- `entity_ticking_level` = All game logic is processed.",
                    "- `inaccessible_level` = No game logic is processed, world generation may still occur.",
                    "- `ticking_level` = All game logic except entities is processed.",
                    "- `unloaded_level` = This chunk is not loaded.")
                .since("2.17.0")
                .register();
        }

        reg.newEnumType(HeightMap.class, "heightmap")
            .user("height ?maps?")
            .name("HeightMap")
            .description("The height map used during chunk generation.",
                "See [**HeightMap**](https://minecraft.wiki/w/Heightmap) on McWiki for more info.", Util.AUTO_GEN_NOTE)
            .since("3.22.0")
            .register();

        reg.newEnumType(SpawnCategory.class, "spawncategory")
            .user("spawn ?categor(y|ies)")
            .name("Spawn Category")
            .description("Represents groups of entities with shared spawn behaviors and mob caps.")
            .since("3.21.0")
            .register();

        if (Classes.getExactClassInfo(TreeType.class) == null) {
            reg.newEnumType(TreeType.class, "bukkittreetype", null, "tree")
                .user("bukkit ?tree ?types?")
                .name("Bukkit Tree Type")
                .description("Represents the different types of trees.", Util.AUTO_GEN_NOTE)
                .after("structuretype")
                .since("3.5.3")
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'tree' already.");
            Util.logLoading("You may have to use their TreeType in SkBee's syntaxes.");
        }
    }

}
