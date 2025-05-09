package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
@Name("ItemComponent - Food Properties")
@Description({"Get the food properties of an item.",
    "This will only return a value if the item has a food component.",
    "See [Food Component](https://minecraft.wiki/w/Data_component_format#food) on McWiki for more details.",
    "Requires Paper 1.21.3+"})
@Examples({"set {_nutrition} to food nutrition of player's tool",
    "if food saturation of player's tool > 0:"})
@Since("INSERT VERSION")
public class ExprFoodComponentProperties extends SimplePropertyExpression<Object, Number> {

    static {
        register(ExprFoodComponentProperties.class, Number.class,
            "food (nutrition|:saturation)", "itemstacks/itemtypes/slots");
    }

    private boolean saturation;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.saturation = parseResult.hasTag("saturation");
        return super.init(expressions, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Number convert(Object from) {
        return ItemUtils.getValue(from, itemStack -> {
            if (itemStack.hasData(DataComponentTypes.FOOD)) {
                FoodProperties data = itemStack.getData(DataComponentTypes.FOOD);
                assert data != null;
                if (this.saturation) return data.saturation();
                return data.nutrition();
            }
            return null;
        });
    }

    @Override
    protected String getPropertyName() {
        return "food " + (this.saturation ? "saturation" : "nutrition");
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

}
