package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Anvil Rename Text")
@Description("Represents the text the player enters into an anvil. This can not be set.")
@Examples({"on anvil prepare:",
        "\tif slot 0 of event-inventory is chicken:",
        "\t\tif slot 1 of event-inventory is an enchanted book:",
        "\t\t\tif stored enchants of slot 1 of event-inventory contains sharpness 5:",
        "\t\t\t\tset {_i} to cooked chicken",
        "\t\t\t\tset name of {_i} to colored anvil rename text of event-inventory",
        "\t\t\t\tenchant {_i} with sharpness 6",
        "\t\t\t\tset event-slot to {_i}",
        "\t\t\t\twait 1 tick #seems to be needed to set repair cost",
        "\t\t\t\tset repair cost of event-inventory to 30"})
public class ExprAnvilRenameText extends SimplePropertyExpression<Inventory, String> {

    static {
        register(ExprAnvilRenameText.class, String.class, "[anvil] (rename text|repair name)", "inventories");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        Util.skript27Warning("anvil text input", "expression");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Nullable
    @Override
    public String convert(Inventory inv) {
        if (inv instanceof AnvilInventory anvilInventory) {
            return anvilInventory.getRenameText();
        }
        return null;
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "anvil rename text";
    }

}
