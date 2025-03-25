package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.command.EffectCommandEvent;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.LiteralUtils;
import com.shanebeestudios.skbee.api.reflection.ReflectionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Utility class to handle Skript things
 */
public class SkriptUtils {

    private static Field LAST_SPAWNED;

    static {
        Class<?> effSpawnClass;
        try {
            if (Skript.classExists("ch.njol.skript.sections.EffSecSpawn")) {
                effSpawnClass = Class.forName("ch.njol.skript.sections.EffSecSpawn");
            } else {
                effSpawnClass = Class.forName("ch.njol.skript.effects.EffSpawn");
            }
            LAST_SPAWNED = effSpawnClass.getDeclaredField("lastSpawned");
        } catch (ClassNotFoundException | NoSuchFieldException ignore) {
        }
    }

    /**
     * Set last spawned entity
     * <p>Skript changed the name of the EffSpawn class so now we gotta use this method</p>
     *
     * @param entity Entity that was spawned last
     */
    public static void setLastSpawned(Entity entity) {
        if (LAST_SPAWNED != null) {
            try {
                LAST_SPAWNED.set(null, entity);
            } catch (IllegalAccessException ignore) {
            }
        }
    }

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

    /** Get a default instance of a Parser for ClassInfos
     * @param <T> ClassType
     * @return New instance of default parser
     */
    public static <T> Parser<T> getDefaultParser() {
        return new Parser<T>() {
            @SuppressWarnings("NullableProblems")
            @Override
            public boolean canParse(ParseContext context) {
                return false;
            }

            @SuppressWarnings("NullableProblems")
            @Override
            public String toString(T o, int flags) {
                return o.toString();
            }

            @SuppressWarnings("NullableProblems")
            @Override
            public String toVariableNameString(T o) {
                return o.toString();
            }
        };
    }

    public static Map<String,EquipmentSlotGroup> getEquipmentSlotGroups() {
        Map<String,EquipmentSlotGroup> groups = new HashMap<>();
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

    /**
     * Skript's ClassInfo for PotionEffectType has a conflicting pattern for PotionTypes
     * <p>This is just until Skript fixes the pattern</p>
     */
    @ApiStatus.Internal
    public static void hackPotionEffectTypeClassInfoPattern() {
        // Fixed in Skript 2.11
        if (Util.IS_RUNNING_SKRIPT_2_11) return;
        ClassInfo<PotionEffectType> info = Classes.getExactClassInfo(PotionEffectType.class);
        assert info != null;
        Pattern[] patterns = new Pattern[]{Pattern.compile("potion ?effect ?types?")};
        ReflectionUtils.setField("userInputPatterns", ClassInfo.class, info, patterns);
    }

}

