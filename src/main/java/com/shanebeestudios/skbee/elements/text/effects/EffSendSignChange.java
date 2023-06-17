package com.shanebeestudios.skbee.elements.text.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.SkriptColor;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Text Component - Sign Change")
@Description({"Sends a sign change to the player. You CAN send a block change first.",
        "\nColor = the color of the text on the sign.",
        "\nGlowing = make the text glow."})
@Examples({"set {_m::1} to mini message from \"<rainbow>OOOOOOOO\"",
        "set {_m::2} to text component from \"Le-Text\"",
        "make player see sign lines of target block as {_m::*} with color blue"})
@Since("2.6.0")
public class EffSendSignChange extends Effect {

    static {
        Skript.registerEffect(EffSendSignChange.class,
                "make %players% see sign lines (at|of) %blocks% as %textcomponents% " +
                        "[with color %-color%] [glowing:and glowing]");
    }

    private Expression<Player> players;
    private Expression<Block> blocks;
    private Expression<ComponentWrapper> components;
    private Expression<SkriptColor> color;
    private boolean glowing;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.players = (Expression<Player>) exprs[0];
        this.blocks = (Expression<Block>) exprs[1];
        this.components = (Expression<ComponentWrapper>) exprs[2];
        this.color = (Expression<SkriptColor>) exprs[3];
        this.glowing = parseResult.hasTag("glowing");
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        ComponentWrapper[] components = this.components.getArray(event);
        DyeColor dyeColor = null;
        if (this.color != null) {
            SkriptColor skriptColor = this.color.getSingle(event);
            if (skriptColor != null) {
                dyeColor = skriptColor.asDyeColor();
            }
        }

        for (Block block : this.blocks.getArray(event)) {
            for (Player player : this.players.getArray(event)) {
                ComponentWrapper.sendSignChange(player, block.getLocation(), components, dyeColor, this.glowing);
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String color = this.color != null ? this.color.toString(e,d) : "";
        String glow = this.glowing ? " and glowing" : "";
        return "make " + this.players.toString(e,d) + " see sign lines of " + this.blocks.toString(e,d)
                + " as " + this.components.toString(e,d) + color + glow;
    }

}
