package com.shanebeestudios.skbee.elements.text.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.EffectSection;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;

@Name("TextComponent - Click Event Callback")
@Description({"Create a click event, that when clicked will run the code in this section.",
        "\nNOTE: Internally this just makes the player run a special command",
        "so you will see console messages where a player runs the command \"/paper callback\" in your console.",
        "\nNOTE: Paper didn't make this command avaiable by default",
        "so you'll have to give your players the permission `bukkit.command.paper.callback`.",
        "\n`uses` = The amount of times the player can click this. Defaults to 1. Use `-1` for unlimited uses.",
        "\n`lifetime` = How long the player has til they can't click it. Defaults to 12 hours."})
@Examples({"set {_t} to mini message from \"JOIN US AT SPAWN FOR A SPECIAL EVENT\"",
        "create callback for {_t}:",
        "\tteleport player to spawn of world \"world\"",
        "send component {_t} to all players",
        "",
        "set {_t} to text component from \"&cDONT CLICK ME\"",
        "create callback for {_t}:",
        "\tkill player",
        "\tbroadcast \"&b%player% &eclicked it &cAND DIED&7!!!\"",
        "send component {_t} to all players"})
@Since("INSERT VERSION")
public class SecClickEventCallback extends EffectSection {

    static class ComponentCallbackEvent extends PlayerEvent {

        public ComponentCallbackEvent(Player player) {
            super(player);
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            throw new IllegalStateException("This event should not be called!!!");
        }

    }

    static {
        Skript.registerSection(SecClickEventCallback.class,
                "create [a] [new] [click event] callback for %textcomponent% " +
                        "[with %-number% use[s]] [[and] with lifetime of %-timespan%]");
    }

    private Expression<ComponentWrapper> component;
    private Expression<Number> uses;
    private Expression<Timespan> timeLimit;
    private Trigger trigger;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult,
                        @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
        this.component = (Expression<ComponentWrapper>) exprs[0];
        this.uses = (Expression<Number>) exprs[1];
        this.timeLimit = (Expression<Timespan>) exprs[2];
        if (sectionNode != null) {
            this.trigger = loadCode(sectionNode, "callback", ComponentCallbackEvent.class);
        } else {
            Skript.error("`click event callback` can only be used as a section.");
            return false;
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        ComponentWrapper component = this.component.getSingle(event);
        if (this.trigger != null && component != null) {
            Object localVars = Variables.copyLocalVariables(event);

            int uses = 1;
            if (this.uses != null) {
                Number usesNum = this.uses.getSingle(event);
                if (usesNum != null) uses = usesNum.intValue();
            }

            Duration timeLimit = ClickCallback.DEFAULT_LIFETIME;
            if (this.timeLimit != null) {
                Timespan timespan = this.timeLimit.getSingle(event);
                if (timespan != null) timeLimit = Duration.ofMillis(timespan.getTicks_i() * 50);
            }

            component.setClickEvent(ClickEvent.callback(audience -> {
                Player player = audience instanceof Player p ? p : null;
                ComponentCallbackEvent callbackEvent = new ComponentCallbackEvent(player);
                Variables.setLocalVariables(callbackEvent, localVars);
                TriggerItem.walk(this.trigger, callbackEvent);
                Variables.setLocalVariables(event, Variables.copyLocalVariables(callbackEvent));
                Variables.removeLocals(callbackEvent);
            }, ClickCallback.Options.builder().uses(uses).lifetime(timeLimit).build()));
        }
        return super.walk(event, false);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String uses = this.uses != null ? (" with " + this.uses.toString(e, d) + " uses") : "";
        String time = this.timeLimit != null ? (" with lifetime of " + this.timeLimit.toString(e, d)) : "";
        return "create click event callback for " + this.component.toString(e, d) + uses + time;
    }

}
