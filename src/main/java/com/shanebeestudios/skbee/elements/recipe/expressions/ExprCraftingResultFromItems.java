package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

@Name("Recipe - Crafting Result from Items")
@Description({"Get the resulting item which would be crafted with a list of items.",
    "This requires either 4 or 9 items, to follow the 2x2 and 3x3 crafting grids respectively.",
    "Use `air` to represent blank slots in the crafting grid.",
    "I have no clue what the world is for since recipes are not per world, but Bukkit offers it so here we are."})
@Examples({"set {_barrel} to crafting result of oak planks, oak slab, oak planks, oak planks, air, oak planks, oak planks, oak slab, oak planks",
    "set {_carrotOnStick} to crafting result of fishing rod, air, air, carrot"})
@Since("INSERT VERSION")
public class ExprCraftingResultFromItems extends SimpleExpression<ItemStack> {

    private static final World DEFAULT_WORLD = Bukkit.getWorlds().getFirst();
    private static final ItemStack AIR = new ItemStack(Material.AIR);

    static {
        Skript.registerExpression(ExprCraftingResultFromItems.class, ItemStack.class, ExpressionType.COMBINED,
            "crafting result (of|from) [items] %itemtypes% [in %-world%]");
    }

    private Expression<ItemType> items;
    private Expression<World> world;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.items = (Expression<ItemType>) exprs[0];
        this.world = (Expression<World>) exprs[1];
        return true;
    }

    @Override
    protected ItemStack @Nullable [] get(Event event) {
        World world = this.world != null ? this.world.getOptionalSingle(event).orElse(DEFAULT_WORLD) : DEFAULT_WORLD;
        ItemStack[] itemStacks = new ItemStack[9];

        ItemType[] itemTypes = this.items.getArray(event);
        if (itemTypes.length == 4) { // Represents the 2x2 crafting grid
            itemStacks = new ItemStack[]{
                itemTypes[0].getRandom(), itemTypes[1].getRandom(), AIR,
                itemTypes[2].getRandom(), itemTypes[3].getRandom(), AIR,
                AIR, AIR, AIR
            };
        } else if (itemTypes.length == 9) { // Represents the 3x3 crafting grid
            for (int i = 0; i < 9; i++) {
                itemStacks[i] = itemTypes[i].getRandom();
            }
        } else {
            return null;
        }
        Recipe craftingRecipe = Bukkit.getCraftingRecipe(itemStacks, world);
        if (craftingRecipe != null) {
            ItemStack itemStack = craftingRecipe.getResult();
            if (itemStack.getType() != Material.AIR) return new ItemStack[]{itemStack};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends ItemStack> getReturnType() {
        return ItemStack.class;
    }

    @Override
    public String toString(Event e, boolean d) {
        SyntaxStringBuilder builder = new SyntaxStringBuilder(e, d);
        builder.append("crafting result from", this.items);
        if (this.world != null) {
            builder.append("in world", this.world);
        }
        return builder.toString();
    }

}
