package com.shanebeestudios.skbee.elements.dialog.sections.inputs;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import com.shanebeestudios.skbee.api.event.dialog.OptionsEvent;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput.OptionEntry;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@Name("Dialog - Single Option Input - Option Entry")
@Description({"An option to be used in an options section of a single option input.",
    "See [**Single Option Input on SkBee wiki**](https://github.com/ShaneBeee/SkBee/wiki/Dialogs#single-option-input) for more info.",
    "**Entries**:",
    "- `display` = A string/text component for what is displayed as the option.",
    "- `initial` = Only one option can have this set to true. " +
        "If true, the option chosen will be the initial one. Defaults to the first option being true, and all others false."})
@Examples({"add single option input:",
    "\tkey: \"le_key\"",
    "\tlabel: \"Choose favorite animal\"",
    "\toptions:",
    "\t\tadd options entry:",
    "\t\t\tdisplay: \"cat\"",
    "\t\tadd options entry:",
    "\t\t\tdisplay: \"dog\"",
    "\t\tadd options entry:",
    "\t\t\tdisplay: \"turtle\"",
    "\t\tadd options entry:",
    "\t\t\tdisplay: \"spider\""})
@Since("3.16.0")
public class SecSingleOptionInputOptions extends Section {

    private static final EntryValidator.EntryValidatorBuilder VALIDATOR = EntryValidator.builder();

    static {
        VALIDATOR.addEntryData(new ExpressionEntryData<>("id", null, false, String.class));
        @SuppressWarnings("unchecked")
        Class<Object>[] compClasses = new Class[]{String.class, ComponentWrapper.class};
        VALIDATOR.addEntryData(new ExpressionEntryData<>("display", null, false, compClasses));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("initial", null, true, Boolean.class));

        Skript.registerSection(SecSingleOptionInputOptions.class, "add options entry");
    }

    private Expression<String> id;
    private Expression<?> display;
    private Expression<Boolean> initial;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (!getParser().isCurrentEvent(OptionsEvent.class)) {
            Skript.error("An options entry can only be used in an 'options' section of a single option input.");
            return false;
        }
        EntryContainer container = VALIDATOR.build().validate(sectionNode);
        if (container == null) return false;

        this.id = (Expression<String>) container.getOptional("id", false);
        this.display = (Expression<?>) container.getOptional("display", false);
        this.initial = (Expression<Boolean>) container.getOptional("initial", false);

        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        TriggerItem next = getNext();

        String id = this.id.getSingle(event);
        if (id == null) {
            error("Invalid ID: " + this.id.toString(event, true));
            return next;
        }


        Component display;
        if (this.display == null) {
            error("Missing Display");
            return next;
        } else {
            Object displayObject = this.display.getSingle(event);
            if (displayObject instanceof ComponentWrapper cw) {
                display = cw.getComponent();
            } else if (displayObject instanceof String string) {
                display = ComponentWrapper.fromText(string).getComponent();
            } else {
                error("Display is invalid, no dialog created: " + this.display.toString(event, true));
                return next;
            }
            if (display == null) {
                error("Display is invalid, no dialog created: " + this.display.toString(event, true));
                return next;
            }
        }

        boolean initial = false;
        if (this.initial != null) {
            Boolean initialSingle = this.initial.getSingle(event);
            if (initialSingle != null) initial = initialSingle;
        }

        if (event instanceof OptionsEvent optionsEvent) {
            OptionEntry optionEntry = OptionEntry.create(id, display, initial);
            optionsEvent.addEntry(optionEntry);
        }

        return next;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "add options entry";
    }

}
