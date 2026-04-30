package com.shanebeestudios.skbee.elements.scoreboard.expressions;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.SkriptColor;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.scoreboard.TeamUtils;
import com.github.shanebeee.skr.skript.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

public class ExprTeamCreate extends SectionExpression<Team> {

    private static EntryValidator VALIDATOR;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void register(Registration reg) {
        Class[] stringClasses = new Class[]{String.class, Component.class, ComponentWrapper.class};
        SimpleEntryValidator builder = SimpleEntryValidator.builder();
        builder.addRequiredEntry("id", String.class);
        builder.addOptionalEntry("display_name", stringClasses);
        builder.addOptionalEntry("color", Color.class);
        builder.addOptionalEntry("collision_rule", Team.OptionStatus.class);
        builder.addOptionalEntry("death_message_visibility", Team.OptionStatus.class);
        builder.addOptionalEntry("nametag_visibility", Team.OptionStatus.class);
        builder.addOptionalEntry("friendly_fire", Boolean.class);
        builder.addOptionalEntry("see_friendly_invisibles", Boolean.class);
        builder.addOptionalEntry("prefix", stringClasses);
        builder.addOptionalEntry("suffix", stringClasses);

        VALIDATOR = builder.build();
        reg.newCombinedExpression(ExprTeamCreate.class, Team.class,
                "create [a] new team [for %-scoreboard%]")
            .validator(VALIDATOR)
            .name("Scoreboard - Team Create")
            .description("Create a new team with a bunch of options.",
                "If a Scoreboard is not provided the default server Scoreboard will be used.",
                "For more info see [**Teams**](https://minecraft.wiki/w/Scoreboard#Teams) on McWiki.",
                "",
                "**Entries**:",
                " - `id` = The ID/name of this team (required String).",
                " - `display_name` = The display name of this team [optional String/TextComponent/TextComp].",
                " - `color` = The color of this team [optional Color].",
                " - `collision_rule` = The collision rule of this team [optional OptionStatus].",
                " - `death_message_visibility` = The death message visibility of this team [optional OptionStatus].",
                " - `nametag_visibility` = The nametag visibility of this team [optional OptionStatus].",
                " - `friendly_fire` = Whether friendly fire is enabled for this team [optional Boolean].",
                " - `see_friendly_invisibles` = Whether friendly invisibles are visible for this team [optional Boolean].",
                " - `prefix` = The prefix of this team [optional String/TextComponent/TextComp].",
                " - `suffix` = The suffix of this team [optional String/TextComponent/TextComp].")
            .examples("set {-team::blue} to create new team:",
                "\tid: \"blue_team\"",
                "\tdisplay_name: colored \"<aqua>Blue Team\"",
                "\tcolor: aqua",
                "\tcollision_rule: for_other_teams",
                "\tdeath_message_visibility: always",
                "\tnametag_visibility: for_own_team",
                "\tfriendly_fire: false",
                "\tsee_friendly_invisibles: true",
                "\tprefix: colored \"<grey>[<aqua>Blue Team<grey>] \"",
                "add all players to {-team::blue}")
            .since("3.21.0")
            .register();
    }

    private Expression<Scoreboard> scoreboard;
    private Expression<String> id;
    private Expression<?> displayName;
    private Expression<Color> color;
    private Expression<Team.OptionStatus> collisionRule;
    private Expression<Team.OptionStatus> deathMessageVisibility;
    private Expression<Team.OptionStatus> nametagVisibility;
    private Expression<Boolean> friendlyFire;
    private Expression<Boolean> seeFriendlyInvisibles;
    private Expression<?> prefix;
    private Expression<?> suffix;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int pattern, Kleenean delayed, ParseResult result,
                        @Nullable SectionNode node, @Nullable List<TriggerItem> triggerItems) {

        EntryContainer validate = VALIDATOR.validate(node);
        if (validate == null) return false;

        this.scoreboard = (Expression<Scoreboard>) expressions[0];

        this.id = (Expression<String>) validate.getOptional("id", false);
        this.displayName = (Expression<?>) validate.getOptional("display_name", false);
        this.color = (Expression<Color>) validate.getOptional("color", false);
        this.collisionRule = (Expression<Team.OptionStatus>) validate.getOptional("collision_rule", false);
        this.deathMessageVisibility = (Expression<Team.OptionStatus>) validate.getOptional("death_message_visibility", false);
        this.nametagVisibility = (Expression<Team.OptionStatus>) validate.getOptional("nametag_visibility", false);
        this.friendlyFire = (Expression<Boolean>) validate.getOptional("friendly_fire", false);
        this.seeFriendlyInvisibles = (Expression<Boolean>) validate.getOptional("see_friendly_invisibles", false);
        this.prefix = (Expression<?>) validate.getOptional("prefix", false);
        this.suffix = (Expression<?>) validate.getOptional("suffix", false);
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Team @Nullable [] get(Event event) {
        Scoreboard scoreboard;
        if (this.scoreboard != null) {
            scoreboard = this.scoreboard.getSingle(event);
        } else {
            scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        }
        if (scoreboard == null) return null;

        String id = this.id.getSingle(event);
        if (id == null) return null;

        Team team = TeamUtils.getTeam(id, scoreboard);

        if (this.displayName != null) {
            Object o = this.displayName.getSingle(event);
            if (o instanceof String s) {
                team.displayName(Component.text(s));
            } else if (o instanceof TextComponent tc) {
                team.displayName(tc);
            } else if (o instanceof ComponentWrapper cw) {
                team.displayName(cw.getComponent());
            }
        }

        if (this.color != null) {
            Color color = this.color.getSingle(event);
            if (color instanceof SkriptColor skriptColor) {
                team.setColor(skriptColor.asChatColor());
            }
        }

        if (this.collisionRule != null) {
            Team.OptionStatus value = this.collisionRule.getSingle(event);
            if (value != null) {
                team.setOption(Team.Option.COLLISION_RULE, value);
            }
        }

        if (this.deathMessageVisibility != null) {
            Team.OptionStatus value = this.deathMessageVisibility.getSingle(event);
            if (value != null) {
                team.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, value);
            }
        }

        if (this.nametagVisibility != null) {
            Team.OptionStatus value = this.nametagVisibility.getSingle(event);
            if (value != null) {
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, value);
            }
        }

        if (this.friendlyFire != null) {
            Boolean value = this.friendlyFire.getSingle(event);
            if (value != null) {
                team.setAllowFriendlyFire(value);
            }
        }

        if (this.seeFriendlyInvisibles != null) {
            Boolean value = this.seeFriendlyInvisibles.getSingle(event);
            if (value != null) {
                team.setCanSeeFriendlyInvisibles(value);
            }
        }

        if (this.prefix != null) {
            Object o = this.prefix.getSingle(event);
            if (o instanceof String s) {
                team.prefix(Component.text(s));
            } else if (o instanceof TextComponent tc) {
                team.prefix(tc);
            } else if (o instanceof ComponentWrapper cw) {
                team.prefix(cw.getComponent());
            }
        }

        if (this.suffix != null) {
            Object o = this.suffix.getSingle(event);
            if (o instanceof String s) {
                team.suffix(Component.text(s));
            } else if (o instanceof TextComponent tc) {
                team.suffix(tc);
            } else if (o instanceof ComponentWrapper cw) {
                team.suffix(cw.getComponent());
            }
        }

        return new Team[]{team};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public boolean isSectionOnly() {
        return true;
    }

    @Override
    public Class<? extends Team> getReturnType() {
        return Team.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String score = this.scoreboard != null ? " for " + this.scoreboard.toString(event, true) : "";
        return "create a new team" + score;
    }

}
