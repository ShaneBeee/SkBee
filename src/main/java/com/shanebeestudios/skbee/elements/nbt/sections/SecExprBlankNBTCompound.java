package com.shanebeestudios.skbee.elements.nbt.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.nbt.sections.SecModifyNBT.NBTEditEvent;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Name("NBT - Empty Compound")
@Description("Returns an empty/new NBT compound.")
@Examples({"set {_nbt} to blank nbt compound",
    "set tag \"points\" of {_nbt} to 10",
    "",
    "set {_data} to empty nbt compound:",
    "\tset string tag \"name\" of nbt to \"Jimmy\"",
    "\tset int tag \"points\" of nbt to 99",
    "\tset compound tag \"extra\" of nbt to empty nbt compound"})
@Since("2.8.0")
@DocumentationId("ExprBlankNBTCompound")
public class SecExprBlankNBTCompound extends SectionExpression<NBTCompound> {

    static {
        Skript.registerExpression(SecExprBlankNBTCompound.class, NBTCompound.class, ExpressionType.SIMPLE,
            "[a[n]] (blank|empty|new) nbt compound");
    }

    private @Nullable Trigger trigger;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult,
                        SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (sectionNode != null) {
            AtomicBoolean delayed = new AtomicBoolean(false);
            Runnable afterLoading = () -> delayed.set(!getParser().getHasDelayBefore().isFalse());
            //noinspection unchecked
            this.trigger = loadCode(sectionNode, "empty nbt modify", afterLoading, NBTEditEvent.class);
            if (delayed.get()) {
                Skript.error("Delays can't be used within an Empty NBT Modify Section");
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected @Nullable NBTCompound[] get(Event event) {
        NBTCompound nbtCompound = new NBTContainer();
        if (trigger != null) {
            NBTEditEvent nbtEditEvent = new NBTEditEvent(nbtCompound);
            Variables.withLocalVariables(event, nbtEditEvent, () -> TriggerItem.walk(trigger, nbtEditEvent));
        }
        return new NBTCompound[]{nbtCompound};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends NBTCompound> getReturnType() {
        return NBTCompound.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "blank nbt compound";
    }

}
