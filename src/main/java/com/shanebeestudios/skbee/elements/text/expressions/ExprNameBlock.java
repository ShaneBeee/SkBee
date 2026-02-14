package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Skull;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprNameBlock extends SimplePropertyExpression<Block, ComponentWrapper> {

    private static final boolean HAS_SKULL_NAME = Skript.methodExists(Skull.class, "customName");

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprNameBlock.class, ComponentWrapper.class,
                "component [custom] block name", "block")
            .name("TextComponent - Block Name")
            .description("Get/set/delete the custom name of a block. This will work on container blocks and skulls.",
                "Even though the custom name of any skull can be set, only a player head will retain its name when broken (Skeleton/creeper/etc skulls will not).")
            .examples("set {_name} to component block name of target block",
                "set component block name of target block to mini message from \"<rainbow>Mr Potato Head!\"",
                "delete component block name of target block")
            .since("3.10.1")
            .register();
    }

    @Override
    public @Nullable ComponentWrapper convert(Block block) {
        BlockState state = block.getState();
        Component component = null;
        if (state instanceof Container container) {
            component = container.customName();
        } else if (state instanceof Skull skull && HAS_SKULL_NAME) {
            component = skull.customName();
        }
        if (component != null) return ComponentWrapper.fromComponent(component);
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(ComponentWrapper.class);
        else if (mode == ChangeMode.DELETE) return CollectionUtils.array();
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        Component name = null;
        if (delta != null && delta[0] instanceof ComponentWrapper cw) {
            name = cw.getComponent();
        }
        for (Block block : getExpr().getArray(event)) {
            setName(block, name);
        }
    }

    private void setName(Block block, Component name) {
        BlockState state = block.getState(false);
        if (state instanceof Container container) {
            container.customName(name);
        } else if (state instanceof Skull skull && HAS_SKULL_NAME) {
            skull.customName(name);
        }
    }

    @Override
    protected String getPropertyName() {
        return "component block custom name";
    }

    @Override
    public Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

}
