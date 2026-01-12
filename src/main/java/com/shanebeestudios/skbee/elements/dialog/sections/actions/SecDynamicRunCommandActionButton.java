package com.shanebeestudios.skbee.elements.dialog.sections.actions;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.event.dialog.DialogRegisterEvent;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.EntryValidator.EntryValidatorBuilder;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
@Name("Dialog - Dynamic Run Command Action Button")
@Description({"Add a dynamic run command action button to a dialog.",
    "See [**Dynamic Run Command Action**](https://minecraft.wiki/w/Dialog#dynamic/run_command) on McWiki for more specific info.",
    "This action will run a command using your command template and provided macros.",
    "The template format looks for `$(dialog_input_key)` to substitute macros (Must include at least ONE macro).",
    "The `input_key` is the key you used for your inputs.",
    "**Entries**:",
    "- `label` = The name on your button, accepts a string or text component/mini message.",
    "- `tooltip` = The hover message, accepts a string or text component/mini message.",
    "- `width` = The width of the button. Value between 1 and 1024 — Defaults to 150.",
    "- `template` = The command template including macros that will run when clicked."})
@Examples({"command /message:",
    "\trigger:",
    "\t\topen multi action dialog to player:",
    "\t\t\ttitle: \"Send a message:\"",
    "\t\t\tinputs:",
    "\t\t\t\tadd text input:",
    "\t\t\t\t\tkey: \"name_input\"",
    "\t\t\t\t\tlabel: \"Player Name\"",
    "\t\t\t\tadd text input:",
    "\t\t\t\t\tkey: \"text_input\"",
    "\t\t\t\t\tlabel: \"Message to send\"",
    "\t\t\tactions:",
    "\t\t\t\tadd dynamic run command action button:",
    "\t\t\t\t\tlabel: \"Send Message\"",
    "\t\t\t\t\twidth: 100",
    "\t\t\t\t\ttemplate: \"/msg $(name_input) $(text_input)\""})
public class SecDynamicRunCommandActionButton extends Section {

    private static final EntryValidatorBuilder VALIDATOR = EntryValidator.builder();

    static {
        @SuppressWarnings("unchecked")
        Class<Object>[] compClasses = new Class[]{String.class, ComponentWrapper.class};
        VALIDATOR.addEntryData(new ExpressionEntryData<>("label", null, false, compClasses));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("tooltip", null, true, compClasses));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("width", new SimpleLiteral<>(150, true), true, Integer.class));

        // DYNAMIC
        VALIDATOR.addEntryData(new ExpressionEntryData<>("template", null, false, String.class));

        Skript.registerSection(SecDynamicRunCommandActionButton.class, "add dynamic run command action button");
    }

    private boolean exitAction;
    private Expression<?> label;
    private Expression<?> tooltip;
    private Expression<Integer> width;
    private Expression<String> commandTemplate;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (!getParser().isCurrentEvent(DialogRegisterEvent.class)) {
            Skript.error("A dynamic action button can only be used in an 'actions' section.");
            return false;
        }
        EntryContainer container = VALIDATOR.build().validate(sectionNode);
        if (container == null) return false;

        // Action button type
        String currentEventName = getParser().getCurrentEventName();
        this.exitAction = currentEventName != null && currentEventName.equalsIgnoreCase("exit_action");

        this.label = (Expression<?>) container.getOptional("label", false);
        this.tooltip = (Expression<?>) container.getOptional("tooltip", false);
        this.width = (Expression<Integer>) container.getOptional("width", true);
        this.commandTemplate = (Expression<String>) container.getOptional("template", false);

        return true;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        TriggerItem next = getNext();

        Component label;

        Object labelObject = this.label.getSingle(event);
        if (labelObject instanceof ComponentWrapper cw) {
            label = cw.getComponent();
        } else if (labelObject instanceof String string) {
            label = ComponentWrapper.fromText(string).getComponent();
        } else {
            error("Unknown label object: " + Classes.toString(labelObject));
            return next;
        }

        Optional<Component> tooltip = Optional.empty();
        if (this.tooltip != null) {
            Object tooltipSingle = this.tooltip.getSingle(event);
            if (tooltipSingle instanceof ComponentWrapper cw) {
                tooltip = Optional.of(cw.getComponent());
            } else if (tooltipSingle instanceof String string) {
                tooltip = Optional.of(ComponentWrapper.fromText(string).getComponent());
            }
        }

        ActionButton.Builder actionButtonBuilder = ActionButton.builder(label);
        tooltip.ifPresent(actionButtonBuilder::tooltip);
        if (this.width != null) {
            actionButtonBuilder.width(this.width.getSingle(event));
        }


        if (event instanceof DialogRegisterEvent actionEvent) {
            String command = this.commandTemplate.getSingle(event);
            if (command == null) return next;

            if (!command.contains("$(")) {
                error("Command template must contain at least one macro, but found: '" + command + "'");
                return next;
            }

            try {
                actionButtonBuilder.action(DialogAction.commandTemplate(command));
            } catch (IllegalArgumentException exception) {
                error("Error with command template: " + exception.getMessage());
                return next;
            }

            ActionButton actionButton = actionButtonBuilder.build();
            if (this.exitAction) {
                actionEvent.setExitActionButton(actionButton);
            } else {
                actionEvent.addActionButton(actionButton);
            }
        }

        return next;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "add dynamic run command action button";
    }

}
