package com.shanebeestudios.skbee.elements.dialog.sections.inputs;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import com.shanebeestudios.skbee.api.event.dialog.DialogRegisterEvent;
import com.shanebeestudios.skbee.api.event.dialog.OptionsEvent;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.SectionEntryData;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@Name("Dialog - Single Option Input")
@Description({"A preset option selection input to be used in an `inputs` section of a dialog.",
    "See [**Input Control on SkBee wiki**](https://github.com/ShaneBeee/SkBee/wiki/Dialogs#input-control)" +
        "and [**Input Control on McWiki**](https://minecraft.wiki/w/Dialog#Input_control_format) for further info.",
    "**Entries**:",
    "- `key` = String identifier of value used when submitting data, must be a valid template argument (letters, digits and _).",
    "- `label` = A string/text component to be displayed to the left of the input.",
    "- `label_visible` = Controls if the label is visible. Defaults to true.",
    "- `width` = Integer value between 1 and 1024 — The width of the input. Defaults to 200.",
    "- `options` = A section for adding options. See [**options**](https://github.com/ShaneBeee/SkBee/wiki/Dialogs#options) for more info."})
@Examples({"add single option input:",
    "\tkey: \"le_key\"",
    "\tlabel: \"Choose favorite animal\"",
    "\toptions:",
    "\t\tadd options entity:",
    "\t\t\tdisplay: \"cat\"",
    "\t\tadd options entity:",
    "\t\t\tdisplay: \"dog\"",
    "\t\tadd options entity:",
    "\t\t\tdisplay: \"turtle\"",
    "\t\tadd options entity:",
    "\t\t\tdisplay: \"spider\""})
@Since("3.16.0")
public class SecSingleOptionInput extends Section {

    private static final EntryValidator.EntryValidatorBuilder VALIDATOR = EntryValidator.builder();

    static {
        // GENERAL INPUT
        VALIDATOR.addEntryData(new ExpressionEntryData<>("key", null, false, String.class));
        @SuppressWarnings("unchecked")
        Class<Object>[] compClasses = new Class[]{String.class, ComponentWrapper.class};
        VALIDATOR.addEntryData(new ExpressionEntryData<>("label", null, false, compClasses));

        // SINGLE OPTION INPUT
        VALIDATOR.addEntryData(new ExpressionEntryData<>("label_visible", null, true, Boolean.class));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("width", new SimpleLiteral<>(200, true), true, Integer.class));
        VALIDATOR.addEntryData(new SectionEntryData("options", null, false));

        Skript.registerSection(SecSingleOptionInput.class, "add single option input");
    }

    // GENERAL INPUT
    private Expression<String> key;
    private Expression<?> label;

    // SINGLE OPTION INPUT
    private Expression<Boolean> labelVisible;
    private Expression<Integer> width;
    private Trigger options;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (!getParser().isCurrentEvent(DialogRegisterEvent.class)) {
            Skript.error("A single option input can only be used in an 'inputs' section of a dialog.");
            return false;
        }
        EntryContainer container = VALIDATOR.build().validate(sectionNode);
        if (container == null) return false;

        this.key = (Expression<String>) container.getOptional("key", false);
        this.label = (Expression<?>) container.getOptional("label", false);
        this.labelVisible = (Expression<Boolean>) container.getOptional("label_visible", false);
        this.width = (Expression<Integer>) container.getOptional("width", false);

        SectionNode optionsNode = (SectionNode) container.getOptional("options", false);
        if (optionsNode != null) {
            this.options = loadCode(optionsNode, "options", OptionsEvent.class);
        }
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        TriggerItem next = getNext();

        // GENERAL INPUT
        String key = this.key.getSingle(event);
        if (key == null) {
            return next;
        }
        if (!Util.isValidDialogInputKey(key)) {
            error("Invalid key. Must only contain letters, numbers and underscores but found: " + key);
            return next;
        }

        Component label;
        if (this.label == null) {
            error("Missing Label");
            return next;
        } else {
            Object titleSingle = this.label.getSingle(event);
            if (titleSingle instanceof ComponentWrapper cw) {
                label = cw.getComponent();
            } else if (titleSingle instanceof String string) {
                label = ComponentWrapper.fromText(string).getComponent();
            } else {
                error("Label is invalid, no dialog created: " + this.label.toString(event, true));
                return next;
            }
            if (label == null) {
                error("Label is invalid, no dialog created: " + this.label.toString(event, true));
                return next;
            }
        }

        // SINGLE OPTION INPUT
        boolean labelVisible = true;
        if (this.labelVisible != null) {
            Boolean single = this.labelVisible.getSingle(event);
            if (single != null) labelVisible = single;
        }
        int width = 200;
        if (this.width != null) {
            Integer intSingle = this.width.getSingle(event);
            if (intSingle != null) width = intSingle;
        }

        OptionsEvent optionsEvent = new OptionsEvent();
        Variables.withLocalVariables(event, optionsEvent, () ->
            Trigger.walk(this.options, optionsEvent));

        if (event instanceof DialogRegisterEvent actionEvent) {
            SingleOptionDialogInput input = DialogInput.singleOption(key, label, optionsEvent.getEntries())
                .labelVisible(labelVisible)
                .width(width)
                .build();
            actionEvent.addInput(input);
        }

        return next;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "add single option input";
    }

}
