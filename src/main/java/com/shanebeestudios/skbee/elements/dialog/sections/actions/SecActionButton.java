package com.shanebeestudios.skbee.elements.dialog.sections.actions;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import com.shanebeestudios.skbee.api.event.dialog.DialogRegisterEvent;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.EntryValidator.EntryValidatorBuilder;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
@Name("Dialog - Action Button")
@Description({"Add an action button to a dialog.",
    "Only some entries will be discussed here, for further info please see [**Action Format**](https://minecraft.wiki/w/Dialog#Action_format) on McWiki.",
    "You can use either a [**Static Action**](https://minecraft.wiki/w/Dialog#Static_action_types) with the `action` section,",
    "or you can use a [**Custom Dynmaic Action**](https://minecraft.wiki/w/Dialog#dynamic/custom) with the `id` and `additions` entries.",
    "**Entries**:",
    "- `label` = The name on your button, accepts a string or text component/mini message (from SkBee).",
    "- `tooltip` = The hover message, accepts a string or text component/mini message (from SkBee).",
    "- `action` = A click event (from SkBee), also called a [**Static Action**](https://minecraft.wiki/w/Dialog#Static_action_types). " +
        "This is what happens when the player clicks the button.",
    "- `id` = The id of a [**Custom Dynmaic Action**](https://minecraft.wiki/w/Dialog#dynamic/custom). " +
        "This will fire the 'Dynamic Action Button Click' event along with the provided data from an input.",
    "- `additions` = An additional NBT compound to go along with your custom dynamic action."})
@Examples({"add static action button:",
    "\tlabel: mini message from \"Creative Gamemode\"",
    "\ttooltip: mini message from \"Switch to creative gamemode\"",
    "\twidth: 200",
    "\taction: click event to run command \"gamemode creative\"",
    "",
    "add dynamic action button:",
    "\tlabel: \"Spawn\"",
    "\ttooltip: \"Teleport yoursel to spawn!\"",
    "\tid: \"custom:teleport_to_spawn\""})
@Since("INSERT VERSION")
public class SecActionButton extends Section {

    private static final EntryValidatorBuilder VALIDATOR = EntryValidator.builder();

    static {
        @SuppressWarnings("unchecked")
        Class<Object>[] compClasses = new Class[]{String.class, ComponentWrapper.class};
        VALIDATOR.addEntryData(new ExpressionEntryData<>("label", null, false, compClasses));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("tooltip", null, true, compClasses));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("width", new SimpleLiteral<>(150, true), true, Integer.class));

        // STATIC
        VALIDATOR.addEntryData(new ExpressionEntryData<>("action", null, true, ClickEvent.class));

        // DYNAMIC
        @SuppressWarnings("unchecked")
        Class<Object>[] idClasses = new Class[]{String.class, NamespacedKey.class};
        VALIDATOR.addEntryData(new ExpressionEntryData<>("id", null, true, idClasses));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("additions", null, true, NBTCompound.class));

        Skript.registerSection(SecActionButton.class, "add (:static|dynamic) action button");
    }

    private boolean isStatic;
    private boolean exitAction;
    private Expression<?> label;
    private Expression<?> tooltip;
    private Expression<Integer> width;
    private Expression<ClickEvent> action;
    private Expression<?> id;
    private Expression<NBTCompound> additions;


    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (!getParser().isCurrentEvent(DialogRegisterEvent.class)) {
            Skript.error("An action button can only be used in an 'actions' section.");
            return false;
        }
        EntryContainer container = VALIDATOR.build().validate(sectionNode);
        if (container == null) return false;
        this.isStatic = parseResult.hasTag("static");

        // Action button type
        String currentEventName = getParser().getCurrentEventName();
        this.exitAction = currentEventName != null && currentEventName.equalsIgnoreCase("exit_action");

        this.label = (Expression<?>) container.getOptional("label", false);
        this.tooltip = (Expression<?>) container.getOptional("tooltip", false);
        this.width = (Expression<Integer>) container.getOptional("width", true);
        if (this.isStatic) {
            this.action = (Expression<ClickEvent>) container.getOptional("action", false);
        } else {
            this.id = (Expression<String>) container.getOptional("id", false);
            this.additions = (Expression<NBTCompound>) container.getOptional("additions", false);
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
        actionButtonBuilder.width(this.width.getSingle(event));


        if (event instanceof DialogRegisterEvent actionEvent) {
            if (this.isStatic) {

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
            } else {
                NamespacedKey id;
                Object idSingle = this.id.getSingle(event);
                if (idSingle instanceof String string) id = NamespacedKey.fromString(string);
                else if (idSingle instanceof NamespacedKey nsk) id = nsk;
                else return next;


                BinaryTagHolder tag = null;
                if (this.additions != null) {
                    NBTCompound nbtCompound = this.additions.getSingle(event);
                    if (nbtCompound != null) {
                        tag = BinaryTagHolder.binaryTagHolder(nbtCompound.toString());
                    }
                }

                actionButtonBuilder.action(DialogAction.customClick(id, tag));

                ActionButton actionButton = actionButtonBuilder.build();
                if (this.exitAction) {
                    actionEvent.setExitActionButton(actionButton);
                } else {
                    actionEvent.addActionButton(actionButton);
                }
            }
        }

        return next;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String type = this.isStatic ? "static" : "dynamic";
        return "add " + type + " action button";
    }

}
