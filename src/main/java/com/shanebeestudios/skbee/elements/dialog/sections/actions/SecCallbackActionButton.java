package com.shanebeestudios.skbee.elements.dialog.sections.actions;

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
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.event.dialog.DialogCallbackEvent;
import com.shanebeestudios.skbee.api.event.dialog.DialogRegisterEvent;
import com.shanebeestudios.skbee.api.nbt.NBTApi;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.EntryValidator.EntryValidatorBuilder;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
@Name("Dialog - Callback Action Button")
@Description({"Add a dynamic action button to a dialog.",
    "This action includes a callback section to run code when the action button is clicked.",
    "See [**Custom Dynmaic Action**](https://minecraft.wiki/w/Dialog#dynamic/custom) on McWiki for more specific info.",
    "**Entries**:",
    "- `label` = The name on your button, accepts a string or text component/mini message.",
    "- `tooltip` = The hover message, accepts a string or text component/mini message.",
    "- `width` = The width of the button. Value between 1 and 1024 — Defaults to 150.",
    "- `trigger` = This section will run code when the button is clicked.",
    "",
    "**Callback Section Event-Values**:",
    "- `event-nbt` = Returns NBT from the event (not sure yet what this is used for).",
    "- `event-audience` = The audience represented in this event.",
    "- `event-player` = The player represented in this event (Might be null if the player isn't available yet, such as in the async config event)."})
@Examples({"add callback action button:",
    "\tlabel: \"Spawn\"",
    "\ttooltip: \"Teleport yoursel to spawn!\"",
    "\ttrigger:",
    "\t\tteleport event-player to spawn of world \"world\""})
@Since("INSERT VERSION")
public class SecCallbackActionButton extends Section {

    private static final EntryValidatorBuilder VALIDATOR = EntryValidator.builder();

    static {
        EventValues.registerEventValue(DialogCallbackEvent.class, NBTCompound.class, DialogCallbackEvent::getNbtCompound);
        EventValues.registerEventValue(DialogCallbackEvent.class, Audience.class, DialogCallbackEvent::getAudience);
        EventValues.registerEventValue(DialogCallbackEvent.class, Player.class, from -> {
            if (from.getAudience() instanceof Player player) return player;
            return null;
        });

        @SuppressWarnings("unchecked")
        Class<Object>[] compClasses = new Class[]{String.class, ComponentWrapper.class};
        VALIDATOR.addEntryData(new ExpressionEntryData<>("label", null, false, compClasses));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("tooltip", null, true, compClasses));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("width", new SimpleLiteral<>(150, true), true, Integer.class));

        // DYNAMIC
        VALIDATOR.addSection("trigger", false);

        Skript.registerSection(SecCallbackActionButton.class, "add callback action button");
    }

    private boolean exitAction;
    private Expression<?> label;
    private Expression<?> tooltip;
    private Expression<Integer> width;
    private Trigger callback;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (!getParser().isCurrentEvent(DialogRegisterEvent.class)) {
            Skript.error("A callback action button can only be used in an 'actions' section.");
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

        SectionNode callbackNode = (SectionNode) container.getOptional("trigger", false);
        if (callbackNode != null) {
            this.callback = loadCode(callbackNode, "callback trigger", DialogCallbackEvent.class);
        } else {
            return false;
        }

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

            Object variables = Variables.copyLocalVariables(event);
            actionButtonBuilder.action(DialogAction.customClick((response, audience) -> {
                BinaryTagHolder payload = response.payload();
                NBTCompound nbtCompound = payload != null ? NBTApi.validateNBT(payload.toString()) : null;
                DialogCallbackEvent dialogCallbackEvent = new DialogCallbackEvent(nbtCompound, audience);

                Variables.setLocalVariables(dialogCallbackEvent, variables);
                Trigger.walk(this.callback, dialogCallbackEvent);
                Variables.removeLocals(dialogCallbackEvent);

            }, ClickCallback.Options.builder().build()));

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
    public String toString(@Nullable Event event, boolean debug) {
        return "add callback action button";
    }

}
