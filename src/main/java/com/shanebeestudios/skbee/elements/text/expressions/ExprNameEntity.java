package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("TextComponent - Entity Name")
@Description({"Get/set the component name of an entity.",
    "- `name` = The vanilla name of an Entity, or the player's profile name (cannot be changed).",
    "- `custom name` = The custom name of an entity, will only show when the player's crosshair is pointed at them.",
    "- `display name` = Same as custom name, but will always show."})
@Examples("set component entity name of target entity to translate component from \"entity.minecraft.llama\"")
@Since("2.4.0")
public class ExprNameEntity extends SimplePropertyExpression<Entity, ComponentWrapper> {

    static {
        register(ExprNameEntity.class, ComponentWrapper.class,
            "component entity [:custom|:display] name", "entities");
    }

    private boolean custom;
    private boolean display;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.custom = parseResult.hasTag("custom");
        this.display = parseResult.hasTag("display");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable ComponentWrapper convert(Entity entity) {
        Component component;
        if (entity instanceof Player player && (this.display || this.custom)) {
            component = player.displayName();
        } else if (this.custom || this.display) {
            component = entity.customName();
        } else {
            component = entity.name();
        }
        if (component == null) return null;
        return ComponentWrapper.fromComponent(component);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            if (!this.custom && !this.display) {
                Skript.error("'name' cannot be set, rather use custom/display name.");
                return null;
            }
            return CollectionUtils.array(ComponentWrapper.class);
        }
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            if (delta[0] instanceof ComponentWrapper component) {
                for (Entity entity : getExpr().getArray(event)) {
                    component.setEntityName(entity, this.display);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        String name = this.display ? "display name" : this.custom ? "custom name" : "name";
        return "component entity " + name;
    }

}
