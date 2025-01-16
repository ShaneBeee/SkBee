package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Color;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.Experiments;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@Name("ItemComponent - CustomModelData Component Apply")
@Description({"Apply a custom model data component to items.",
    "Requires Paper 1.21.4+ and `item_component` feature.",
    "See [**CustomModelData Component**](https://minecraft.wiki/w/Data_component_format#custom_model_data) on McWiki for more info.",
    "",
    "**Entries**:",
    "- `floats` = A list of numbers.",
    "- `flags` = A list of booleans (true/false).",
    "- `strings` = A list of strings.",
    "- `colors` = A list of colors."})
@Examples({"apply custom model data to player's tool:",
    "\tfloats: 1, 2, 3, 4, 5",
    "\tstrings: \"hello\", \"yippee\"",
    "\tflags: true, true, true, false",
    "\tcolors: blue, green, rgb(1,1,1)"})
@Since("3.8.0")
public class SecCustomModelDataComponent extends Section {

    private static final EntryValidator VALIDATOR;
    private static final boolean HAS_SUPPORT = Skript.isRunningMinecraft(1, 21, 4);

    static {
        VALIDATOR = SimpleEntryValidator.builder()
            .addOptionalEntry("floats", Number.class)
            .addOptionalEntry("flags", Boolean.class)
            .addOptionalEntry("strings", String.class)
            .addOptionalEntry("colors", Color.class)
            .build();

        Skript.registerSection(SecCustomModelDataComponent.class,
            "apply custom model data [component] to %itemstacks/itemtypes/slots%");
    }

    private Expression<?> items;
    private Expression<Number> floats;
    private Expression<Boolean> flags;
    private Expression<String> strings;
    private Expression<Color> colors;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (!HAS_SUPPORT) {
            Skript.error("CustomModelData with fields requires Minecraft 1.21.4+");
            return false;
        }
        if (!getParser().hasExperiment(Experiments.ITEM_COMPONENT)) {
            Skript.error("requires '" + Experiments.ITEM_COMPONENT.codeName() + "' feature.");
            return false;
        }
        EntryContainer validate = VALIDATOR.validate(sectionNode);
        if (validate == null) {
            return false;
        }
        this.items = exprs[0];
        this.floats = (Expression<Number>) validate.getOptional("floats", false);
        this.flags = (Expression<Boolean>) validate.getOptional("flags", false);
        this.strings = (Expression<String>) validate.getOptional("strings", false);
        this.colors = (Expression<Color>) validate.getOptional("colors", false);
        return true;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        CustomModelData.Builder builder = CustomModelData.customModelData();
        if (this.floats != null) {
            for (Number number : this.floats.getArray(event)) {
                builder.addFloat(number.floatValue());
            }
        }
        if (this.flags != null) {
            for (Boolean bool : this.flags.getArray(event)) {
                builder.addFlag(bool);
            }
        }
        if (this.strings != null) {
            for (String string : this.strings.getArray(event)) {
                builder.addString(string);
            }
        }
        if (this.colors != null) {
            for (Color color : this.colors.getArray(event)) {
                builder.addColor(color.asBukkitColor());
            }
        }
        CustomModelData customModelData = builder.build();
        ItemUtils.modifyItems(this.items.getArray(event), itemStack ->
            itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, customModelData));
        return super.walk(event, false);
    }

    @Override
    public String toString(Event e, boolean d) {
        return "apply custom model data component to " + this.items.toString(e, d);
    }

}
