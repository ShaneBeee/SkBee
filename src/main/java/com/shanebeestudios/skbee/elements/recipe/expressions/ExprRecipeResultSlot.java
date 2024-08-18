package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.slot.Slot;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Recipe - Result Slot")
@Description({"Represents the result slot of a crafting event.",
    "This can be changed."})
@Examples({"on crafter craft:",
    "\tif event-string = \"minecraft:diamond_sword\":",
    "\t\tset name of recipe result to \"Señor Sword\"",
    "\telse:",
    "\t\tset recipe result to a stick named \"&cNice Try\"",
    "",
    "on preparing craft:",
    "\tset {_e} to event-string",
    "\tif {_e} = \"minecraft:diamond_shovel\":",
    "\t\tset name of recipe result to \"&cMr Shovel\""})
@Since("3.6.1")
public class ExprRecipeResultSlot extends SimpleExpression<Slot> {

    public static final boolean HAS_CRAFTER_RECIPE = Skript.classExists("org.bukkit.event.block.CrafterCraftEvent");

    static {
        Skript.registerExpression(ExprRecipeResultSlot.class, Slot.class, ExpressionType.SIMPLE,
            "recipe result [slot]");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        ParserInstance parser = getParser();
        if (HAS_CRAFTER_RECIPE && parser.isCurrentEvent(CrafterCraftEvent.class)) {
            return true;
        } else if (parser.isCurrentEvent(CraftItemEvent.class, PrepareItemCraftEvent.class)) {
            return true;
        }
        Skript.error("'" + parser.getCurrentEventName() + "' does not have a recipe result.");
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Slot @Nullable [] get(Event event) {
        Slot slot = null;
        if (HAS_CRAFTER_RECIPE && event instanceof CrafterCraftEvent craftEvent) {
            slot = new Slot() {
                @Override
                public @NotNull ItemStack getItem() {
                    return craftEvent.getResult();
                }

                @Override
                public void setItem(@Nullable ItemStack item) {
                    if (item == null) return;
                    craftEvent.setResult(item);
                }

                @Override
                public int getAmount() {
                    return getItem().getAmount();
                }

                @Override
                public void setAmount(int amount) {
                    ItemStack clone = getItem().clone();
                    clone.setAmount(amount);
                    setItem(clone);
                }

                @Override
                public boolean isSameSlot(Slot other) {
                    return other.getItem() == getItem();
                }

                @Override
                public String toString(Event event, boolean debug) {
                    return "crafter craft event result slot";
                }
            };
            //} else if (event instanceof CraftItemEvent craftItemEvent) {
        } else if (event instanceof InventoryEvent invEvent && invEvent.getInventory() instanceof CraftingInventory craftingInventory) {
            slot = new Slot() {
                @Override
                public @Nullable ItemStack getItem() {
                    return craftingInventory.getResult();
                }

                @Override
                public void setItem(@Nullable ItemStack item) {
                    craftingInventory.setResult(item);
                }

                @Override
                public int getAmount() {
                    ItemStack item = getItem();
                    if (item == null) return 0;
                    return item.getAmount();
                }

                @Override
                public void setAmount(int amount) {
                    ItemStack item = getItem();
                    if (item != null) {
                        ItemStack clone = item.clone();
                        clone.setAmount(amount);
                        setItem(clone);
                    }
                }

                @Override
                public boolean isSameSlot(Slot other) {
                    return other.getItem() == getItem();
                }

                @Override
                public String toString(Event event, boolean debug) {
                    return "crafting inventory result slot";
                }
            };
        }
        if (slot != null) return new Slot[]{slot};
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Slot> getReturnType() {
        return Slot.class;
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "recipe result slot";
    }

}
