package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.bound.Bound;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Name("Bound - Owners/Members")
@Description("Represents the owners and members of a bound. Will be saved as UUIDs in the bound config.")
@Examples({"add player to bound members of bound with id \"spawn\"",
        "remove player from bound owners of bound with id \"spawn\"",
        "loop bound owners of bound with id \"beach\":",
        "\tif loop-offline player is online:",
        "\t\tteleport loop-offline player to spawn of world \"world\""})
@Since("1.15.0")
public class ExprBoundOwnerMember extends SimpleExpression<OfflinePlayer> {

    static {
        Skript.registerExpression(ExprBoundOwnerMember.class, OfflinePlayer.class, ExpressionType.PROPERTY,
                "bound (owner[s]|1¦member[s]) of %bound%");
    }

    private Expression<Bound> bound;
    private boolean owners;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        bound = (Expression<Bound>) exprs[0];
        owners = parseResult.mark == 0;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Nullable
    @Override
    protected OfflinePlayer[] get(Event e) {
        List<String> uuids = new ArrayList<>();
        Bound bound = this.bound.getSingle(e);
        if (bound == null) return null;

        if (owners) {
            bound.getOwners().forEach(owner -> uuids.add(owner.toString()));
        } else {
            bound.getMembers().forEach(member -> uuids.add(member.toString()));
        }

        List<OfflinePlayer> players = new ArrayList<>();
        uuids.forEach(uuid -> players.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid))));
        return players.toArray(new OfflinePlayer[0]);
    }

    @SuppressWarnings("NullableProblems")
    @Nullable
    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case ADD, REMOVE, SET, DELETE -> CollectionUtils.array(OfflinePlayer[].class);
            default -> null;
        };
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
        Bound bound = this.bound.getSingle(e);
        if (bound == null) return;

        switch (mode) {
            case DELETE:
                if (owners) {
                    bound.clearOwners();
                } else {
                    bound.clearMembers();
                }
                break;
            case SET:
                if (owners) {
                    bound.clearOwners();
                } else {
                    bound.clearMembers();
                }
            case ADD:
                for (Object player : delta) {
                    if (player instanceof OfflinePlayer) {
                        UUID uuid = ((OfflinePlayer) player).getUniqueId();
                        if (owners) {
                            bound.addOwner(uuid);
                        } else {
                            bound.addMember(uuid);
                        }
                    }
                }
                break;
            case REMOVE:
                for (Object player : delta) {
                    if (player instanceof OfflinePlayer) {
                        UUID uuid = ((OfflinePlayer) player).getUniqueId();
                        if (owners) {
                            bound.removeOwner(uuid);
                        } else {
                            bound.removeMember(uuid);
                        }
                    }
                }
                break;
            default:
                return;
        }
        SkBee.getPlugin().getBoundConfig().saveBound(bound);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends OfflinePlayer> getReturnType() {
        return OfflinePlayer.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "bound " + (this.owners ? "owners" : "members") + " of bound " + this.bound.toString(e, d);
    }

}
