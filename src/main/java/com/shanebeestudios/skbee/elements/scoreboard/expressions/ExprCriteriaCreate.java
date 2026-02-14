package com.shanebeestudios.skbee.elements.scoreboard.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Criteria;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExprCriteriaCreate extends SimpleExpression<Criteria> {

    public static void register(Registration reg) {
        List<String> patterns = new ArrayList<>();
        patterns.add("criteria with id %string%");
        if (SkBee.getPlugin().getPluginConfig().ELEMENTS_STATISTIC) {
            patterns.add("criteria from [statistic] %statistic% [(using|with) %-itemtype/blockdata/entitydata%]");
        }

        reg.newCombinedExpression(ExprCriteriaCreate.class, Criteria.class, patterns.toArray(new String[0]))
            .name("Scoreboard - Criteria Create")
            .description("Get one of the default Minecraft scoreboard criterias.",
                "You can also get a criteria bassed off a statistic.",
                "(see [**Scoreboard Criteria**](https://minecraft.wiki/w/Scoreboard#Criteria) on McWiki) or create your own.")
            .examples("set {_c} to criteria with id \"health\"",
                "set {_c} to criteria from sprint_one_cm",
                "set {_c} to criteria from mine_block using diamond ore",
                "set {_c} to criteria from interact_with_anvil using diamond",
                "set {_c} to criteria from craft_item using diamond sword",
                "set {_c} to criteria from mob_kills using a player")
            .since("2.6.0")
            .register();
    }

    private Expression<String> id;
    private Expression<Statistic> statistic;
    private @Nullable Expression<?> object;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (matchedPattern == 0) {
            this.id = (Expression<String>) exprs[0];
        } else {
            this.statistic = (Expression<Statistic>) exprs[0];
            this.object = exprs[1];
        }
        return true;
    }

    @Override
    protected @Nullable Criteria[] get(Event event) {
        if (this.id != null) {
            String id = this.id.getSingle(event);
            if (id != null) {
                return new Criteria[]{Bukkit.getScoreboardCriteria(id)};
            }
        } else {
            Statistic statistic = this.statistic.getSingle(event);
            Object object = this.object != null ? this.object.getSingle(event) : null;
            Criteria criteria = null;
            if (statistic != null) {
                if (statistic.getType() == Statistic.Type.ENTITY) {
                    if (object instanceof EntityData<?> entityData) {
                        EntityType entityType = EntityUtils.toBukkitEntityType(entityData);
                        criteria = Criteria.statistic(statistic, entityType);
                    }
                } else if (statistic.getType() == Statistic.Type.BLOCK) {
                    Material material = getMaterial(object);
                    if (material.isBlock()) criteria = Criteria.statistic(statistic, material);
                } else if (statistic.getType() == Statistic.Type.ITEM) {
                    Material material = getMaterial(object);
                    if (material.isItem()) criteria = Criteria.statistic(statistic, material);
                } else if (statistic.getType() == Statistic.Type.UNTYPED && object == null) {
                    criteria = Criteria.statistic(statistic);
                }
            }
            if (criteria != null) return new Criteria[]{criteria};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Criteria> getReturnType() {
        return Criteria.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        if (this.id != null) {
            return "criteria with id " + this.id.toString(e, d);
        } else {
            String using = this.object != null ? " using " + this.object.toString(e, d) : "";
            return "criteria from statistic " + this.statistic.toString(e, d) + using;
        }
    }

    private static Material getMaterial(Object object) {
        if (object instanceof ItemType itemType) {
            return itemType.getMaterial();
        } else if (object instanceof BlockData blockData) {
            return blockData.getMaterial();
        }
        return Material.AIR;
    }

}
