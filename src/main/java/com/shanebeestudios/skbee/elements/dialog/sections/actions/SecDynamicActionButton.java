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
import com.shanebeestudios.skbee.api.event.dialog.DialogRegisterEvent;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
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
@Name("Dialog - Dynamic Action Button")
@Description({"Add a dynamic action button to a dialog.",
    "See [**Custom Dynmaic Action**](https://minecraft.wiki/w/Dialog#dynamic/custom) on McWiki for more specific info.",
    "This action will fire the 'Player Custom Click' event along with the id and additions.",
    "**Entries**:",
    "- `label` = The name on your button, accepts a string or text component/mini message.",
    "- `tooltip` = The hover message, accepts a string or text component/mini message.",
    "- `width` = The width of the button. Value between 1 and 1024 — Defaults to 150.",
    "- `id` = The id of the action.",
    "- `additions` = An additional NBT compound to go along with your custom dynamic action."})
@Examples({"add dynamic action button:",
    "\tlabel: \"Spawn\"",
    "\ttooltip: \"Teleport yoursel to spawn!\"",
    "\tid: \"custom:teleport_to_spawn\"",
    "\tadditions: nbt from \"{some_tag:\"\"some extra info\"\"}\""})
@Since("3.16.0")
public class SecDynamicActionButton extends Section {

    private static final EntryValidatorBuilder VALIDATOR = EntryValidator.builder();

    static {
        @SuppressWarnings("unchecked")
        Class<Object>[] compClasses = new Class[]{String.class, ComponentWrapper.class};
        VALIDATOR.addEntryData(new ExpressionEntryData<>("label", null, false, compClasses));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("tooltip", null, true, compClasses));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("width", new SimpleLiteral<>(150, true), true, Integer.class));

        // DYNAMIC
        @SuppressWarnings("unchecked")
        Class<Object>[] idClasses = new Class[]{String.class, NamespacedKey.class};
        VALIDATOR.addEntryData(new ExpressionEntryData<>("id", null, true, idClasses));
        VALIDATOR.addEntryData(new ExpressionEntryData<>("additions", null, true, NBTCompound.class));

        Skript.registerSection(SecDynamicActionButton.class, "add dynamic action button");
    }

    private boolean exitAction;
    private Expression<?> label;
    private Expression<?> tooltip;
    private Expression<Integer> width;
    private Expression<?> id;
    private Expression<NBTCompound> additions;


    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
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

        this.id = (Expression<String>) container.getOptional("id", false);
        this.additions = (Expression<NBTCompound>) container.getOptional("additions", false);

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

        return next;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "add dynamic action button";
    }

}
