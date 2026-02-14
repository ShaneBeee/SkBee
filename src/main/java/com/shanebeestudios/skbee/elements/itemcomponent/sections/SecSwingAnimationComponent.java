package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.ItemComponentUtils;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SwingAnimation;
import io.papermc.paper.datacomponent.item.SwingAnimation.Animation;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

public class SecSwingAnimationComponent extends Section {

    private static EntryValidator VALIDATOR;

    public static void register(Registration reg) {
        if (Util.IS_RUNNING_MC_1_21_11) {
            VALIDATOR = SimpleEntryValidator.builder()
                .addOptionalEntry("type", String.class)
                .addOptionalEntry("duration", Timespan.class)
                .build();

            reg.newSection(SecSwingAnimationComponent.class,
                    "apply swing animation component to %itemstacks/itemtypes/slots%")
                .name("ItemComponent - Swing Animation Component Apply")
                .description("Specifies the swing animation to play when attacking or interacting using this item.",
                    "See [**Swing Animation Component**](https://minecraft.wiki/w/Data_component_format#swing_animation) on McWiki for more details.",
                    "Requires Minecraft 1.21.11+",
                    "",
                    "**ENTRIES**:",
                    "All entries are optional and will use their defaults when omitted.",
                    "- `type` = String, The type of swinging animation. Can be \"none\", \"whack\", \"stab\". Defaults to \"whack\".",
                    "- `duration` = A timespan that determines the animation's duration. Defaults to 6 ticks.")
                .examples("set {_i} to 1 of diamond sword",
                    "apply swing animation component to {_i}:",
                    "\ttype: \"stab\"",
                    "\tduration: 60 ticks",
                    "",
                    "give player 1 of {_i}")
                .since("3.16.0")
                .register();
        }
    }

    private Expression<?> items;
    private Expression<String> type;
    private Expression<Timespan> duration;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (sectionNode == null) return false;
        EntryContainer validate = VALIDATOR.validate(sectionNode);
        if (validate == null) {
            return false;
        }

        this.type = (Expression<String>) validate.getOptional("type", false);
        this.duration = (Expression<Timespan>) validate.getOptional("duration", false);
        this.items = exprs[0];

        return true;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        SwingAnimation.Builder builder = SwingAnimation.swingAnimation();

        if (this.type != null) {
            String typeString = this.type.getSingle(event);
            if (typeString != null) {
                Animation type = switch (typeString) {
                    case "whack" -> Animation.WHACK;
                    case "stab" -> Animation.STAB;
                    default -> Animation.NONE;
                };
                builder.type(type);
            }
        }
        if (this.duration != null) {
            Timespan duration = this.duration.getSingle(event);
            if (duration != null) builder.duration((int) duration.getAs(TimePeriod.TICK));
        }

        ItemComponentUtils.modifyComponent(this.items.getArray(event), ChangeMode.SET,
            DataComponentTypes.SWING_ANIMATION, builder.build());

        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "apply swing animation component to " + this.items.toString(e, d);
    }

}
