package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mannequin;
import org.jetbrains.annotations.Nullable;

@Name("TextComponent - Mannequin Description")
@Description("Represents the description of a mannequin entity (text below name). Requires Minecraft 1.21.9+")
@Examples({"set component mannequin description of last spawned entity to mini message from \"<rainbow>OOOO Imma Mannequin\"",
    "delete component mannequin description of all mannequins",
    "set {_d} to component mannequin description of {_entity}"})
@Since("INSERT VERSION")
public class ExprMannequinDescription extends SimplePropertyExpression<Entity, ComponentWrapper> {

    static {
        if (Util.IS_RUNNING_MC_1_21_9) {
            register(ExprMannequinDescription.class, ComponentWrapper.class,
                "component mannequin description", "entities");
        }
    }

    @Override
    public @Nullable ComponentWrapper convert(Entity entity) {
        if (entity instanceof Mannequin mannequin) return ComponentWrapper.fromComponent(mannequin.getDescription());
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE)
            return CollectionUtils.array(ComponentWrapper.class);
        return null;
    }

    @Override
    public void change(Entity from, Object @Nullable [] delta, ChangeMode mode) {
        if (!(from instanceof Mannequin mannequin)) return;

        if (mode == ChangeMode.DELETE) {
            mannequin.setDescription(null);
        } else if (mode == ChangeMode.SET && delta != null && delta.length == 1 && delta[0] instanceof ComponentWrapper comp) {
            mannequin.setDescription(comp.getComponent());
        }
    }

    @Override
    protected String getPropertyName() {
        return "component mannequin description";
    }

    @Override
    public Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

}
