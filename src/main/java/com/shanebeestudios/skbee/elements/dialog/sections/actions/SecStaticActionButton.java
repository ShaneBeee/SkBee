package com.shanebeestudios.skbee.elements.dialog.sections.actions;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.event.dialog.DialogRegisterEvent;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.EntryValidator.EntryValidatorBuilder;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class SecStaticActionButton extends Section {

    private static final EntryValidatorBuilder VALIDATOR = EntryValidator.builder();

    static {
        @SuppressWarnings("unchecked")
        Class<Object>[] compClasses = new Class[]{String.class, ComponentWrapper.class};
        VALIDATOR.addEntryData(new ExpressionEntryData<>("label", null, false, compClasses));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("tooltip", null, true, compClasses));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("width", new SimpleLiteral<>(150, true), true, Integer.class));

        // STATIC
        VALIDATOR.addEntryData(new ExpressionEntryData<>("action", null, true, ClickEvent.class));
    }

    public static void register(Registration reg) {
        reg.newSection(SecStaticActionButton.class, "add static action button")
            .name("Dialog - Static Action Button")
            .description("Add a static action button to a dialog.",
                "See [**Static Action**](https://minecraft.wiki/w/Dialog#Static_action_types) on McWiki for more detailed info.",
                "**Entries**:",
                "- `label` = The name on your button, accepts a string or text component/mini message.",
                "- `tooltip` = The hover message, accepts a string or text component/mini message.",
                "- `width` = The width of the button. Value between 1 and 1024 — Defaults to 150.",
                "- `action` = A click event, also called a [**Static Action**](https://minecraft.wiki/w/Dialog#Static_action_types). " +
                    "This is what happens when the player clicks the button.")
            .examples("add static action button:",
                "\tlabel: mini message from \"Creative Gamemode\"",
                "\ttooltip: mini message from \"Switch to creative gamemode\"",
                "\twidth: 200",
                "\taction: click event to run command \"gamemode creative\"")
            .since("3.16.0")
            .register();
    }

    private boolean exitAction;
    private Expression<?> label;
    private Expression<?> tooltip;
    private Expression<Integer> width;
    private Expression<ClickEvent> action;


    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (!getParser().isCurrentEvent(DialogRegisterEvent.class)) {
            Skript.error("A static action button can only be used in an 'actions' section.");
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
        this.action = (Expression<ClickEvent>) container.getOptional("action", false);
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
            if (this.action != null) {
                ClickEvent action = this.action.getSingle(event);
                actionButtonBuilder.action(DialogAction.staticAction(action));

            }
            ActionButton button = actionButtonBuilder.build();

            if (this.exitAction) {
                actionEvent.setExitActionButton(button);
            } else {
                actionEvent.addActionButton(button);
            }
        }

        return next;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "add static action button";
    }

}
