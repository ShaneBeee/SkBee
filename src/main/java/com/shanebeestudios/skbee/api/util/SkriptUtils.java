package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.command.EffectCommandEvent;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.util.LiteralUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class to handle Skript things
 */
public class SkriptUtils {

    /**
     * Get counts of loaded Skript elements
     * <br>
     * In order events, effects, expressions, conditions, sections
     *
     * @return Counts of loaded Skript elements
     */
    public static int[] getElementCount() {
        int[] i = new int[5];

        i[0] = Skript.getEvents().size();
        i[1] = Skript.getEffects().size();
        AtomicInteger exprs = new AtomicInteger();
        Skript.getExpressions().forEachRemaining(e -> exprs.getAndIncrement());
        i[2] = exprs.get();
        i[3] = Skript.getConditions().size();
        i[4] = Skript.getSections().size();

        return i;
    }

    /**
     * Parse a string as an effect
     *
     * @param stringEffect String to parse
     * @param sender       Who it was sent from
     * @return True if parsed correctly else false
     */
    public static boolean parseEffect(String stringEffect, CommandSender sender) {
        ParserInstance parserInstance = ParserInstance.get();
        parserInstance.setCurrentEvent("effect command", EffectCommandEvent.class);
        Effect effect = Effect.parse(stringEffect, null);
        parserInstance.deleteCurrentEvent();
        if (effect != null) {
            return TriggerItem.walk(effect, new EffectCommandEvent(sender, stringEffect));
        } else {
            return false;
        }
    }

    /**
     * Get a default instance of a Parser for ClassInfos
     *
     * @param <T> ClassType
     * @return New instance of default parser
     */
    public static <T> Parser<T> getDefaultParser() {
        return new Parser<T>() {
            @Override
            public boolean canParse(ParseContext context) {
                return false;
            }

            @Override
            public String toString(T o, int flags) {
                return o.toString();
            }

            @Override
            public String toVariableNameString(T o) {
                return o.toString();
            }
        };
    }

    public static Map<String, EquipmentSlotGroup> getEquipmentSlotGroups() {
        Map<String, EquipmentSlotGroup> groups = new HashMap<>();
        for (Field declaredField : EquipmentSlotGroup.class.getDeclaredFields()) {
            if (EquipmentSlotGroup.class.isAssignableFrom(declaredField.getType())) {
                try {
                    EquipmentSlotGroup equipmentSlotGroup = (EquipmentSlotGroup) declaredField.get(null);
                    String name = declaredField.getName().toLowerCase(Locale.ROOT) + "_slot_group";
                    groups.put(name, equipmentSlotGroup);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return groups;
    }

    /**
     * Parse an expression
     * <p>Copied from {@link ch.njol.skript.lang.InputSource} and modified</p>
     *
     * @param expr The string expression to parse
     * @return An expression if it parsed correctly
     */
    public static Expression<?> parseExpression(String expr) {
        Expression<?> mappingExpr = new SkriptParser(expr, SkriptParser.ALL_FLAGS, ParseContext.DEFAULT)
            .parseExpression(Object.class);
        if (LiteralUtils.hasUnparsedLiteral(mappingExpr)) {
            mappingExpr = LiteralUtils.defendExpression(mappingExpr);
            if (!LiteralUtils.canInitSafely(mappingExpr))
                return null;
        }
        return mappingExpr;
    }

}

