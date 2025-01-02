package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@SuppressWarnings({"DataFlowIssue", "UnstableApiUsage"})
@Name("ItemComponent - Food Component Apply")
@Description({"Apply a food component to any item making it an edible item. Requires Minecraft 1.20.5+",
    "See [**McWiki Food Component**](https://minecraft.wiki/w/Data_component_format#food) for more details.",
    "**NOTE**: `eat time`, `using converts to` and the `effects` section were all removed in Minecraft 1.21.2.",
    "",
    "**Entries/Sections**:",
    "- `nutrition` = The number of food points restored by this item when eaten. Must be a non-negative integer.",
    "- `saturation` = The amount of saturation restored by this item when eaten.",
    "- `can always eat` = If true, this item can be eaten even if the player is not hungry. Defaults to false. [Optional]",
    "- `eat time` = The number of seconds taken by this item to be eaten. Defaults to 1.6 seconds. [Optional] (Requires Minecraft 1.21/1.21.1)",
    "- `using converts to` = The item to replace this item with when it is eaten. [Optional] (Requires Minecraft 1.21/1.21.1)",
    "- `effects:` = A section to apply potion effects to this food item. [Optional] (Removed in Minecraft 1.21.2)"})
@Examples({"# Directly apply a food component to the player's tool",
    "apply food component to player's tool:",
    "\tnutrition: 5",
    "\tsaturation: 3",
    "",
    "# Create a new item and apply a food item to it",
    "set {_i} to 1 of book",
    "apply food component to {_i}:",
    "\tnutrition: 5",
    "\tsaturation: 3",
    "\tusing converts to: 1 of bowl",
    "\tcan always eat: true",
    "\teffects:",
    "\t\tapply potion effect of nausea without particles for 10 seconds",
    "\t\tapply potion effect of poison without particles for 5 seconds ",
    "give player 1 of {_i}"})
@Since("3.5.8")
public class SecFoodComponent extends Section {

    public static class FoodComponentApplyEvent extends Event {

        private final FoodComponent component;

        public FoodComponentApplyEvent(FoodComponent component) {
            this.component = component;
        }

        public FoodComponent getComponent() {
            return this.component;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            throw new IllegalStateException("FoodComponentApplyEvent should never be called");
        }
    }

    private static boolean HAS_CONVERT = false;
    private static boolean HAS_EAT_SECONDS = false;
    private static boolean HAS_EFFECTS = false;
    private static Method EAT_SECONDS_METHOD;
    private static Method USING_CONVERTS_TO_METHOD;
    private static final EntryValidator.EntryValidatorBuilder VALIDATIOR = EntryValidator.builder();

    static {
        if (Skript.classExists("org.bukkit.inventory.meta.components.FoodComponent")) {
            HAS_CONVERT = Skript.methodExists(FoodComponent.class, "setUsingConvertsTo", ItemStack.class);
            HAS_EAT_SECONDS = Skript.methodExists(FoodComponent.class, "setEatSeconds", float.class);
            HAS_EFFECTS = Skript.methodExists(FoodComponent.class, "addEffect", PotionEffect.class, float.class);
            VALIDATIOR.addEntryData(new ExpressionEntryData<>("nutrition", null, false, Number.class));
            VALIDATIOR.addEntryData(new ExpressionEntryData<>("saturation", null, false, Number.class));
            VALIDATIOR.addEntryData(new ExpressionEntryData<>("can always eat", null, true, Boolean.class));
            VALIDATIOR.addEntryData(new ExpressionEntryData<>("eat time", null, true, Timespan.class));
            VALIDATIOR.addEntryData(new ExpressionEntryData<>("using converts to", null, true, ItemType.class));
            VALIDATIOR.addSection("effects", true);
            Skript.registerSection(SecFoodComponent.class, "apply food component to %itemtypes%");
            if (HAS_CONVERT) {
                try {
                    USING_CONVERTS_TO_METHOD = FoodComponent.class.getDeclaredMethod("setUsingConvertsTo", ItemStack.class);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
            if (HAS_EAT_SECONDS) {
                try {
                    EAT_SECONDS_METHOD = FoodComponent.class.getDeclaredMethod("setEatSeconds", float.class);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private Expression<ItemType> items;
    private Expression<Number> nutrition;
    private Expression<Number> saturation;
    private Expression<Boolean> canAlwaysEat;
    private Expression<Timespan> eatTime;
    private Expression<ItemType> usingConverts;
    private Trigger potionEffectSection;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = VALIDATIOR.build().validate(sectionNode);
        if (container == null) return false;

        this.items = (Expression<ItemType>) exprs[0];
        this.nutrition = (Expression<Number>) container.getOptional("nutrition", false);
        this.saturation = (Expression<Number>) container.getOptional("saturation", false);
        this.canAlwaysEat = (Expression<Boolean>) container.getOptional("can always eat", false);
        this.eatTime = (Expression<Timespan>) container.getOptional("eat time", false);
        if (this.eatTime != null && !HAS_EAT_SECONDS) {
            Skript.error("'eat time' requires Minecraft 1.21 or 1.21.1");
            return false;
        }
        this.usingConverts = (Expression<ItemType>) container.getOptional("using converts to", false);
        if (this.usingConverts != null && !HAS_CONVERT) {
            Skript.error("'using converts to' requires Minecraft 1.21 or 1.21.1");
            return false;
        }
        SectionNode potionEffects = container.getOptional("effects", SectionNode.class, false);
        if (potionEffects != null) {
            if (!HAS_EFFECTS) {
                Skript.error("'effects' was removed in Minecraft 1.21.2");
                return false;
            }
            this.potionEffectSection = loadCode(potionEffects, "potion effects", FoodComponentApplyEvent.class);
        }
        return true;
    }

    @SuppressWarnings({"NullableProblems"})
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Object localVars = Variables.copyLocalVariables(event);

        Number nutritionNum = this.nutrition.getSingle(event);
        Number saturationNum = this.saturation.getSingle(event);
        if (nutritionNum == null || saturationNum == null) return super.walk(event, false);

        int nutrition = Math.max(nutritionNum.intValue(), 0);
        float saturation = saturationNum.floatValue();

        boolean canAlwaysEat = this.canAlwaysEat != null ? this.canAlwaysEat.getSingle(event) : false;
        Timespan eatTime = this.eatTime != null ? this.eatTime.getSingle(event) : null;
        ItemStack usingConvertsTo = this.usingConverts != null ? this.usingConverts.getSingle(event).getRandom() : null;

        for (ItemType itemType : this.items.getArray(event)) {
            ItemMeta itemMeta = itemType.getItemMeta();

            FoodComponent food = itemMeta.getFood();
            food.setNutrition(nutrition);
            food.setSaturation(saturation);
            food.setCanAlwaysEat(canAlwaysEat);
            if (HAS_EAT_SECONDS && eatTime != null) {
                setEatSeconds(food, (float) eatTime.getAs(Timespan.TimePeriod.TICK) / 20);
            }
            if (HAS_CONVERT && usingConvertsTo != null) {
                setUsingConverts(food, usingConvertsTo);
            }

            if (this.potionEffectSection != null) {
                FoodComponentApplyEvent foodEvent = new FoodComponentApplyEvent(food);
                Variables.setLocalVariables(foodEvent, localVars);
                TriggerItem.walk(this.potionEffectSection, foodEvent);
                Variables.setLocalVariables(event, Variables.copyLocalVariables(foodEvent));
                Variables.removeLocals(foodEvent);
            }

            itemMeta.setFood(food);
            itemType.setItemMeta(itemMeta);
        }
        return super.walk(event, false);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "apply food component to " + this.items.toString(e, d);
    }

    private static void setEatSeconds(FoodComponent food, float seconds) {
        if (EAT_SECONDS_METHOD != null) {
            try {
                EAT_SECONDS_METHOD.invoke(food, seconds);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void setUsingConverts(FoodComponent food, ItemStack usingConvertsTo) {
        if (USING_CONVERTS_TO_METHOD != null) {
            try {
                USING_CONVERTS_TO_METHOD.invoke(food, usingConvertsTo);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
