package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.bukkit.event.command.UnknownCommandEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("TextComponent - Join/Quit/Kick/Death Message")
@Description("Get/set the join, quit, kick, death, unknown command messages.")
@Examples({"on join:",
    "\tset component join message to mini message from \"<hover:show_entity:player:%uuid of player%><gradient:##FAA401:##5FFA01>%player%<reset><green> joined the game\"",
    "",
    "on quit:",
    "\tset component quit message to mini message from \"<hover:show_entity:player:%uuid of player%><gradient:##FAA401:##5FFA01>%player%<reset><red> left the game\"",
    "",
    "on death of player:",
    "\tif attacker is a player:",
    "\t\tset {_t} to translate component of attacker's tool",
    "\t\tset {_m} to mini message from \"<##FA1F01>%victim% <##FAD401>was slain by <##72FA01>%attacker% <##FAD401>using <grey>[<##03FCEA><lang:%{_t}%><grey>]\"",
    "\t\tset component death message to {_m}"})
@Since("3.4.0")
public class ExprMessageComponent extends SimpleExpression<ComponentWrapper> {

    private static final String[] PATTERNS = new String[]{"component join message", "component (quit|kick) message",
        "component death message", "component unknown command message"};

    static {
        Skript.registerExpression(ExprMessageComponent.class, ComponentWrapper.class, ExpressionType.SIMPLE, PATTERNS);
    }

    private int pattern;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = matchedPattern;
        ParserInstance parserInstance = ParserInstance.get();
        String errorMessage = "'" + parseResult.expr + "' can only be used in a %s event.";
        if (matchedPattern == 0 && !parserInstance.isCurrentEvent(PlayerJoinEvent.class)) {
            Skript.error(String.format(errorMessage, "join"));
            return false;
        } else if (matchedPattern == 1 && !parserInstance.isCurrentEvent(PlayerQuitEvent.class, PlayerKickEvent.class)) {
            Skript.error(String.format(errorMessage, "quit/kick"));
            return false;
        } else if (matchedPattern == 2 && !parserInstance.isCurrentEvent(EntityDeathEvent.class)) {
            Skript.error(String.format(errorMessage, "death"));
            return false;
        } else if (matchedPattern == 3 && !parserInstance.isCurrentEvent(UnknownCommandEvent.class)) {
            Skript.error(String.format(errorMessage, "unknown command"));
            return false;
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected ComponentWrapper @Nullable [] get(Event event) {
        Component component;
        if (this.pattern == 0 && event instanceof PlayerJoinEvent joinEvent) {
            component = joinEvent.joinMessage();
        } else if (this.pattern == 1 && event instanceof PlayerQuitEvent quitEvent) {
            component = quitEvent.quitMessage();
        } else if (this.pattern == 1 && event instanceof PlayerKickEvent kickEvent) {
            component = kickEvent.leaveMessage();
        } else if (this.pattern == 2 && event instanceof PlayerDeathEvent deathEvent) {
            component = deathEvent.deathMessage();
        } else if (this.pattern == 3 && event instanceof UnknownCommandEvent unknownCommandEvent) {
            component = unknownCommandEvent.message();
        } else {
            return null;
        }
        return new ComponentWrapper[]{ComponentWrapper.fromComponent(component)};
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(ComponentWrapper.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof ComponentWrapper wrapper) {
            Component component = wrapper.getComponent();
            if (this.pattern == 0 && event instanceof PlayerJoinEvent joinEvent) {
                joinEvent.joinMessage(component);
            } else if (this.pattern == 1 && event instanceof PlayerQuitEvent quitEvent) {
                quitEvent.quitMessage(component);
            } else if (this.pattern == 1 && event instanceof PlayerKickEvent kickEvent) {
                kickEvent.leaveMessage(component);
            } else if (this.pattern == 2 && event instanceof PlayerDeathEvent deathEvent) {
                deathEvent.deathMessage(component);
            } else if (this.pattern == 3 && event instanceof UnknownCommandEvent unknownCommandEvent) {
                unknownCommandEvent.message(component);
            }
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return PATTERNS[this.pattern];
    }

}
