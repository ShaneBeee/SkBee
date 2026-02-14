package com.shanebeestudios.skbee.elements.nbt.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.nbt.NBTApi;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprItemWithNBT extends SimpleExpression<ItemType> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprItemWithNBT.class, ItemType.class,
                "%itemtype% with [:custom] [[item( |-)]nbt] %nbtcompound%")
            .name("NBT - Item with NBT")
            .description("Get an item with nbt.",
                "NOTE: The NBT in the examples represents NBT for Minecraft 1.20.5+",
                "NOTE: The optional `custom` syntax will place nbt in the `\"minecraft:custom_data\"` component (1.20.5+ only).")
            .examples("give player diamond sword with nbt from \"{\"\"minecraft:food\"\":{nutrition:10,saturation:2.0f}}\"",
                "set {_n} to nbt from \"{custom_data:{points:10}}\"",
                "set {_i} to netherite axe with nbt {_n}",
                "give player diamond pickaxe with nbt from \"{\"\"minecraft:damage\"\":500}\"",
                "give player 30 apples with nbt from \"{\"\"minecraft:max_stack_size\"\":10}\"",
                "",
                "#These two have the same outcome, just showing the use of `custom` and how it places in the custom_data component.",
                "give player diamond sword with nbt from \"{\"\"minecraft:custom_data\"\":{points:10}}\"",
                "give player diamond sword with custom nbt from \"{points:10}\"")
            .since("1.0.0")
            .register();
    }

    private Expression<ItemType> item;
    private Expression<NBTCompound> nbt;
    private boolean custom;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.item = (Expression<ItemType>) exprs[0];
        this.nbt = (Expression<NBTCompound>) exprs[1];
        this.custom = parseResult.hasTag("custom");
        return true;
    }

    @Override
    protected ItemType @Nullable [] get(Event event) {
        ItemType item = this.item.getSingle(event);
        NBTCompound nbt = this.nbt.getSingle(event);
        if (item == null || nbt == null) {
            return null;
        }
        Material material = item.getMaterial();
        if (!material.isItem()) {
            warning("Cannot add nbt to a non-item (block only) itemtype: " + Classes.toString(item));
            return null;
        }
        if (material == Material.AIR || item.getAmount() < 1) {
            warning("Cannot add nbt to an empty/air item: " + Classes.toString(item));
            return null;
        }

        return new ItemType[]{NBTApi.getItemTypeWithNBT(item, nbt, this.custom)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String custom = this.custom ? " custom " : " ";
        return this.item.toString(e, d) + " with" + custom + "nbt " + this.nbt.toString(e, d);
    }

}
