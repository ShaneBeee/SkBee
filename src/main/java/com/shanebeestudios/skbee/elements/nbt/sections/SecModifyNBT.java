package com.shanebeestudios.skbee.elements.nbt.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.slot.Slot;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.nbt.NBTCustom;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SecModifyNBT extends Section {

    public static class NBTEditEvent extends Event {

        private final NBTCompound compound;

        public NBTEditEvent(NBTCompound compound) {
            this.compound = compound;
        }

        public NBTCompound getCompound() {
            return this.compound;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            throw new IllegalStateException("This event should not be called!");
        }
    }

    public static void register(Registration reg) {
        reg.newSection(SecModifyNBT.class,
                "modify [:custom] nbt of %entity/block/itemstack/itemtype/slot%")
            .name("NBT - Modify NBT")
            .description("Modify NBT of an entity/block(TileEntity)/item.",
                "This method is faster than the old NBT expressions, as the changes aren't applied to the object until the section is done.",
                "This section creates its own internal event, which means previous event-values will not work.",
                "**OPTIONS**:",
                "`entity` = Will modify the base nbt of an entity/player.",
                "`block` = Will modify the base nbt of a block, must be a tile entity block.",
                "`item` = Will modify the base nbt of an item (the components section of an item).",
                "`custom` = Will modify the custom nbt of an item (the \"minecraft:custom_data\" component), entity or block.")
            .examples("# Item",
                "modify nbt of player's tool:",
                "\tset int tag \"test\" of nbt to 1",
                "# Item - Custom",
                "modify custom nbt of player's tool:",
                "\tset double tag \"points\" of nbt to 10.5",
                "",
                "# Entity",
                "modify nbt of player:",
                "\tset int tag \"SomeRealNbtTag\" of nbt to 5",
                "# Entity - Custom",
                "modify custom nbt of player:",
                "\tset string tag \"blah\" of nbt to \"ooo a string\"",
                "",
                "# Block",
                "modify nbt of target block:",
                "\tset short tag \"cooking_time_spent\" of nbt to 25",
                "# Block - Custom",
                "modify custom nbt of target block:",
                "\tset string tag \"owner\" of nbt to {_nameOfPlayer}")
            .since("3.11.0")
            .register();
        EventValues.registerEventValue(NBTEditEvent.class, NBTCompound.class, NBTEditEvent::getCompound);
    }

    private boolean custom;
    private Expression<?> object;
    private Trigger trigger;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult,
                        SectionNode sectionNode, List<TriggerItem> triggerItems) {
        this.custom = parseResult.hasTag("custom");
        this.object = exprs[0];
        if (sectionNode == null) return false;

        AtomicBoolean delayed = new AtomicBoolean(false);
        Runnable afterLoading = () -> delayed.set(!getParser().getHasDelayBefore().isFalse());
        this.trigger = loadCode(sectionNode, "nbt modify", null, afterLoading, NBTEditEvent.class);
        if (delayed.get()) {
            Skript.error("Delays can't be used within an NBT Modify Section");
            return false;
        }
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Object object = this.object.getSingle(event);
        if (this.custom) {
            if (object instanceof Entity entity) {
                NBT.modifyPersistentData(entity, readWriteNBT -> {
                    runTrigger(event, readWriteNBT.getOrCreateCompound(NBTCustom.KEY));
                });
            } else if (object instanceof Block block && block.getState() instanceof TileState tileState) {
                NBT.modifyPersistentData(tileState, readWriteNBT -> {
                    ReadWriteNBT customCompound = readWriteNBT.getOrCreateCompound(NBTCustom.KEY);
                    if (readWriteNBT.hasTag("__nbtapi")) {
                        // Remove placeholder tag
                        readWriteNBT.removeKey("__nbtapi");
                    }
                    runTrigger(event, customCompound);
                });
            } else if (object instanceof ItemStack || object instanceof ItemType || object instanceof Slot) {
                ItemUtils.modifyItems(object, itemStack ->
                    NBT.modify(itemStack, readWriteNBT -> {
                        runTrigger(event, readWriteNBT);
                    }));
            }
        } else {
            if (object instanceof Entity entity) {
                NBT.modify(entity, readWriteNBT -> {
                    runTrigger(event, readWriteNBT);
                });
            } else if (object instanceof Block block && block.getState() instanceof TileState tileState) {
                NBT.modify(tileState, readWriteNBT -> {
                    runTrigger(event, readWriteNBT);
                });
            } else if (object instanceof ItemStack || object instanceof ItemType || object instanceof Slot) {
                ItemUtils.modifyItems(object, itemStack ->
                    NBT.modifyComponents(itemStack, readWriteNBT -> {
                        runTrigger(event, readWriteNBT);
                    }));
            }
        }
        return super.walk(event, false);
    }

    private void runTrigger(Event event, ReadWriteNBT nbt) {
        NBTEditEvent nbtEditEvent = new NBTEditEvent((NBTCompound) nbt);
        Variables.withLocalVariables(event, nbtEditEvent, () -> TriggerItem.walk(this.trigger, nbtEditEvent));
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "modify nbt of " + this.object.toString(e, d);
    }

}
