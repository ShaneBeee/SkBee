package com.shanebeestudios.skbee.elements.nbt.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.nbt.NBTApi;
import com.shanebeestudios.skbee.api.registration.Registration;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class EffTagDelete extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffTagDelete.class,
                "delete tag[s] %strings% of %nbtcompound%")
            .name("NBT - Tag Delete")
            .description("Delete an NBT tag without having to specify a tag type.")
            .examples(
                "delete tag \"minecraft:enchantments\" of nbt of player's tool",
                "delete tag \"custom;level\" of nbt of player")
            .since("3.5.0")
            .register();
    }

    private Expression<String> tags;
    private Expression<NBTCompound> nbt;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.tags = (Expression<String>) exprs[0];
        this.nbt = (Expression<NBTCompound>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        NBTCompound nbt = this.nbt.getSingle(event);
        if (nbt == null) return;

        for (String tag : this.tags.getArray(event)) {
            NBTApi.deleteTag(tag, nbt);
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "delete tag[s] " + this.tags.toString(e, d) + " of " + this.nbt.toString(e, d);
    }

}
