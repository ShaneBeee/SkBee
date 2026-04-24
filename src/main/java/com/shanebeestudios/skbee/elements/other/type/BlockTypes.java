package com.shanebeestudios.skbee.elements.other.type;

import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.registrations.Classes;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;

public class BlockTypes {

    public static void register(Registration reg) {
        if (Classes.getExactClassInfo(Action.class) == null) {
            reg.newEnumType(Action.class, "blockaction")
                .user("block ?actions?")
                .name("Block Action")
                .description("Represents different ways to interact.")
                .since("3.4.0")
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'blockaction' already.");
            Util.logLoading("You may have to use their BlockAction in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(BlockFace.class) == null) {
            reg.newEnumType(BlockFace.class, "blockface", "", "face")
                .user("blockfaces?")
                .name("BlockFace")
                .description("Represents the face of a block.", Util.AUTO_GEN_NOTE)
                .since("2.6.0")
                .defaultExpression(new SimpleLiteral<>(BlockFace.NORTH, true))
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'blockFace' already.");
            Util.logLoading("You may have to use their BlockFace in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(BlockState.class) == null) {
            reg.newType(BlockState.class, "blockstate")
                .user("blockstates?")
                .name("BlockState")
                .description("Represents a captured state of a block, which will not change automatically.",
                    "Unlike Block, which only one object can exist per coordinate, BlockState can exist multiple times for any given Block.",
                    "In a structure, this represents how the block is saved to the structure.",
                    "Requires MC 1.17.1+")
                .since("1.12.3")
                .parser(new Parser<>() {
                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(BlockState blockState, int flags) {
                        return String.format("BlockState{type=%s,location=%s}",
                            blockState.getType(), blockState.getLocation());
                    }

                    @Override
                    public @NotNull String toVariableNameString(BlockState blockState) {
                        return toString(blockState, 0);
                    }
                })
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'blockState' already.");
            Util.logLoading("You may have to use their BlockState in SkBee's syntaxes.");
        }

    }

}
