package com.shanebeestudios.skbee.elements.nbt.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.conditions.base.PropertyCondition.PropertyType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.nbt.NBTApi;
import com.shanebeestudios.skbee.api.skript.base.Condition;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("NBT - Has Tag")
@Description("Check if an NBT Compound contains a tag.")
@Examples({"if nbt compound of player has tag \"custom;points\":",
    "if nbt compound of player's tool doesn't have nbt tag \"health;score\":"})
@Since("2.3.2")
public class CondHasNBTTag extends Condition {

    static {
        PropertyCondition.register(CondHasNBTTag.class, PropertyType.HAVE,
            "[nbt] tag %string%", "nbtcompounds");
    }

    private Expression<NBTCompound> compounds;
    private Expression<String> tag;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.compounds = (Expression<NBTCompound>) exprs[0];
        this.tag = (Expression<String>) exprs[1];
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        String tag = this.tag.getSingle(event);
        if (tag == null) {
            return false;
        }
        return compounds.check(event, compound -> NBTApi.hasTag(compound, tag), isNegated());
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String has = isNegated() ? " has tag " : " doesn't have tag ";
        return this.compounds.toString(e, d) + has + this.tag.toString(e, d);
    }

}
