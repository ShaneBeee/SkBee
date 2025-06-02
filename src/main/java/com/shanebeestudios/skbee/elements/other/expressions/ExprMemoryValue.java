package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Name("Entity Memory")
@Description("Get/set memories of entities.")
@Examples({"set {_home} to home memory of {_villager}",
        "set home memory of last spawned villager to location of player"})
@Since("3.4.0")
public class ExprMemoryValue extends SimplePropertyExpression<LivingEntity, Object> {

    static {
        register(ExprMemoryValue.class, Object.class, "%*memory% memory", "livingentities");
    }

    private Literal<MemoryKey<?>> memory;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.memory = (Literal<MemoryKey<?>>) exprs[matchedPattern];
        setExpr((Expression<? extends LivingEntity>) exprs[matchedPattern == 0 ? 1 : 0]);
        return true;
    }

    @Override
    public @Nullable Object convert(LivingEntity entity) {
        try {
            Object memory = entity.getMemory(this.memory.getSingle());
            if (memory instanceof UUID uuid) {
                return uuid.toString();
            }
            return memory;
        } catch (IllegalStateException ignore) {
            // Minecraft throws this... reported to PaperMC
            // https://github.com/PaperMC/Paper/issues/12618
            return null;
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(getSkriptClass(this.memory.getSingle()));
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue", "unchecked", "rawtypes"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (mode == ChangeMode.SET && delta != null && delta[0] != null) {
            MemoryKey memory = this.memory.getSingle();
            Object value = delta[0];
            Class<?> skriptClass = getSkriptClass(memory);
            if (value.getClass().isAssignableFrom(skriptClass)) {
                if (memory.getMemoryClass() == UUID.class && value instanceof String string) {
                    try {
                        value = UUID.fromString(string);
                    } catch (IllegalArgumentException ig) {
                        return;
                    }
                }
                for (LivingEntity livingEntity : getExpr().getArray(event)) {
                    livingEntity.setMemory(memory, value);
                }
            }
        }
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return getSkriptClass(this.memory.getSingle());
    }

    @Override
    protected @NotNull String getPropertyName() {
        return this.memory.toString() + " memory";
    }

    private Class<?> getSkriptClass(MemoryKey<?> memoryKey) {
        Class<?> memoryClass = memoryKey.getMemoryClass();
        if (memoryClass == UUID.class) return String.class;
        return memoryClass;
    }

}
