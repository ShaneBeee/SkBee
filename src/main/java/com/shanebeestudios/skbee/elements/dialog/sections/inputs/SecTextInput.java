package com.shanebeestudios.skbee.elements.dialog.sections.inputs;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import com.shanebeestudios.skbee.api.event.dialog.DialogRegisterEvent;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput.MultilineOptions;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SecTextInput extends Section {

    private static final EntryValidator.EntryValidatorBuilder VALIDATOR = EntryValidator.builder();

    static {
        // GENERAL INPUT
        VALIDATOR.addEntryData(new ExpressionEntryData<>("key", null, false, String.class));
        @SuppressWarnings("unchecked")
        Class<Object>[] compClasses = new Class[]{String.class, ComponentWrapper.class};
        VALIDATOR.addEntryData(new ExpressionEntryData<>("label", null, false, compClasses));

        // TEXT INPUT
        VALIDATOR.addEntryData(new ExpressionEntryData<>("width", new SimpleLiteral<>(200, true), true, Integer.class));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("label_visible", null, true, Boolean.class));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("initial", null, true, String.class));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("max_length", null, true, Integer.class));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("multiline_max_lines", null, true, Integer.class));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("multiline_height", null, true, Integer.class));
    }

    public static void register(Registration reg) {
        reg.newSection(SecTextInput.class, "add text input")
            .name("Dialog - Text Input")
            .description("A text input to be used in an `inputs` section of a dialog.",
                "See [**Input Control on SkBee wiki**](https://github.com/ShaneBeee/SkBee/wiki/Dialogs#input-control)" +
                    "and [**Input Control on McWiki**](https://minecraft.wiki/w/Dialog#Input_control_format) for further info.",
                "**Entries**:",
                "- `key` = String identifier of value used when submitting data, must be a valid template argument (letters, digits and _).",
                "- `label` = A string/text component to be displayed to the left of the input.",
                "- `width` = Integer value between 1 and 1024 — The width of the input. Defaults to 200. [Optional]",
                "- `label_visible` = Controls if the label is visible. Defaults to true. [Optional]",
                "- `initial` = The initial string value of the text input. [Optional]",
                "- `max_length` = Maximum length of input. Defaults to 32. [Optional]",
                "- The next two represent `multiline` and if used, must be used together.",
                "If present, allows users to input multiple lines, optional object with entries:",
                "  - `multiline_max_lines` = Positive integer. If present, limits maximum lines.",
                "  - `multiline_height` = Integer value between 1 and 512 — Height of input.")
            .examples("add text input:",
                "    key: \"name_input\"",
                "    label: \"Input your name to confirm:\"",
                "    initial: \"name\"")
            .since("3.16.0")
            .register();
    }

    // GENERAL INPUT
    private Expression<String> key;
    private Expression<?> label;

    // TEXT INPUT

    private Expression<Integer> width;
    private Expression<Boolean> labelVisible;
    private Expression<String> initial;
    private Expression<Integer> maxLength;
    private Expression<Integer> multiline_max_lines;
    private Expression<Integer> multiline_height;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (!getParser().isCurrentEvent(DialogRegisterEvent.class)) {
            Skript.error("A text input can only be used in an 'inputs' section of a dialog.");
            return false;
        }
        EntryContainer container = VALIDATOR.build().validate(sectionNode);
        if (container == null) return false;

        this.key = (Expression<String>) container.getOptional("key", false);
        this.label = (Expression<?>) container.getOptional("label", false);
        this.initial = (Expression<String>) container.getOptional("initial", false);
        this.labelVisible = (Expression<Boolean>) container.getOptional("label_visible", false);
        this.maxLength = (Expression<Integer>) container.getOptional("max_length", false);
        this.width = (Expression<Integer>) container.getOptional("width", false);
        this.multiline_max_lines = (Expression<Integer>) container.getOptional("multiline_max_lines", false);
        this.multiline_height = (Expression<Integer>) container.getOptional("multiline_height", false);

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

        // TEXT INPUT
        int width = 200;
        if (this.width != null) {
            Integer intSingle = this.width.getSingle(event);
            if (intSingle != null) width = intSingle;
        }

        String initial = "";
        if (this.initial != null) {
            String s = this.initial.getSingle(event);
            if (s != null) initial = s;
        }

        boolean labelVisible = true;
        if (this.labelVisible != null) {
            Boolean single = this.labelVisible.getSingle(event);
            if (single != null) labelVisible = single;
        }

        int maxLength = 32;
        if (this.maxLength != null) {
            Integer intSingle = this.maxLength.getSingle(event);
            if (intSingle != null) maxLength = intSingle;
        }

        MultilineOptions multilineOptions = null;
        if (this.multiline_max_lines != null && this.multiline_height != null) {
            Integer maxLines = this.multiline_max_lines.getSingle(event);
            Integer height = this.multiline_height.getSingle(event);
            if (maxLines != null && height != null) {
                multilineOptions = MultilineOptions.create(maxLines, height);
            }
        }

        if (event instanceof DialogRegisterEvent actionEvent) {
            TextDialogInput text = DialogInput.text(key, width, label, labelVisible, initial,
                maxLength, multilineOptions);
            actionEvent.addInput(text);
        }

        return next;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "add text input";
    }

}
