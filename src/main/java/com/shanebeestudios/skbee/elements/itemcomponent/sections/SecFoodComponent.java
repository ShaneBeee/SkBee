package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SecFoodComponent extends Section {

    private static EntryValidator VALIDATOR;

    public static void register(Registration reg) {
        VALIDATOR = SimpleEntryValidator.builder()
            .addRequiredEntry("nutrition", Number.class)
            .addRequiredEntry("saturation", Number.class)
            .addOptionalEntry("can_always_eat", Boolean.class)
            .build();

        reg.newSection(SecFoodComponent.class, "apply food component to %itemstacks/itemtypes/slots%")
            .name("ItemComponent - Food Component Apply")
            .description("Apply a food component to any item giving it food properties.",
                "You will also require a `consumable` component to actually make the item consumable.",
                "Requires Paper 1.21.3+",
                "See [**Food Component**](https://minecraft.wiki/w/Data_component_format#food) on McWiki for more details.",
                "",
                "**Entries/Sections**:",
                "- `nutrition` = The number of food points restored by this item when eaten. Must be a non-negative integer.",
                "- `saturation` = The amount of saturation restored by this item when eaten.",
                "- `can always eat` = If true, this item can be eaten even if the player is not hungry. Defaults to false. [Optional]")
            .examples("apply food component to player's tool:",
                "\tnutrition: 5",
                "\tsaturation: 3",
                "",
                "set {_i} to 1 of book",
                "apply food component to {_i}:",
                "\tnutrition: 5",
                "\tsaturation: 3",
                "\tcan_always_eat: true",
                "give player 1 of {_i}")
            .since("3.5.8")
            .register();
    }

    private Expression<Object> items;
    private Expression<Number> nutrition;
    private Expression<Number> saturation;
    private Expression<Boolean> canAlwaysEat;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) return false;

        this.items = (Expression<Object>) exprs[0];
        this.nutrition = (Expression<Number>) container.getOptional("nutrition", false);
        this.saturation = (Expression<Number>) container.getOptional("saturation", false);
        this.canAlwaysEat = (Expression<Boolean>) container.getOptional("can_always_eat", false);
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        FoodProperties.Builder builder = FoodProperties.food();

        if (this.nutrition != null) {
            Number nutrition = this.nutrition.getSingle(event);
            if (nutrition != null) builder.nutrition(Math.max(0, nutrition.intValue()));
        }
        if (this.saturation != null) {
            Number saturation = this.saturation.getSingle(event);
            if (saturation != null) builder.saturation(saturation.floatValue());
        }
        if (this.canAlwaysEat != null) {
            boolean canAlwaysEat = this.canAlwaysEat.getOptionalSingle(event).orElse(false);
            builder.canAlwaysEat(canAlwaysEat);
        }

        FoodProperties food = builder.build();

        ItemUtils.modifyItems(this.items.getArray(event), itemStack -> {
            itemStack.setData(DataComponentTypes.FOOD, food);
        });
        return super.walk(event, false);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "apply food component to " + this.items.toString(e, d);
    }

}
