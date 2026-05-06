package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import com.shanebeestudios.skbee.api.util.ItemComponentUtils;
import com.shanebeestudios.skbee.config.SkBeeMetrics;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class ExprContainerComponent extends SimpleExpression<ItemType> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprContainerComponent.class, ItemType.class,
                "container component [items] of %itemstacks/itemtypes/slots%")
            .name("ItemComponent - Container")
            .description("Represents the container component of an item, providing access to its contents.",
                "You can add items, remove items, delete the component, go nuts.",
                "See [**Container Component**](https://minecraft.wiki/w/Data_component_format#container) on McWiki for more details.",
                "",
                "**Changers**:",
                " - SET = Sets the container component to a specific set of items.",
                " - ADD = Adds items to the container component.",
                " - REMOVE = Removes items from the container component.",
                " - DELETE = Deletes the container component from the item.",
                " - RESET = Resets the container component to its default state.")
            .register();
    }

    private Expression<?> items;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        SkBeeMetrics.Features.ITEM_COMPONENTS.used();
        this.items = expressions[0];
        return true;
    }

    @Override
    protected ItemType @Nullable [] get(Event event) {
        List<ItemType> components = new ArrayList<>();

        ItemComponentUtils.takeAPeakAtComponent(this.items.getArray(event), DataComponentTypes.CONTAINER, icc -> {
            for (ItemStack content : icc.contents()) {
                components.add(new ItemType(content));
            }
        });

        return components.toArray(new ItemType[0]);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, ADD, REMOVE, DELETE, RESET -> CollectionUtils.array(ItemType.class);
            default -> null;
        };
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        List<ItemType> changedItems = new ArrayList<>();
        if (delta != null) {
            for (Object o : delta) {
                if (o instanceof ItemType it) {
                    changedItems.add(it);
                }
            }
        }

        ItemComponentUtils.modifyComponent(this.items.getArray(event), DataComponentTypes.CONTAINER,
            (icc, itemstack) -> {
                ItemContainerContents.Builder builder = ItemContainerContents.containerContents();

                if (mode == ChangeMode.ADD) {
                    for (ItemType changedItem : changedItems) {
                        ItemStack random = changedItem.getRandom();
                        if (random != null) {
                            builder.add(random);
                        }
                    }
                    if (icc != null) {
                        builder.addAll(icc.contents());
                    }
                    itemstack.setData(DataComponentTypes.CONTAINER, builder.build());
                } else if (mode == ChangeMode.SET) {
                    for (ItemType changedItem : changedItems) {
                        ItemStack random = changedItem.getRandom();
                        if (random != null) {
                            builder.add(random);
                        }
                    }
                    itemstack.setData(DataComponentTypes.CONTAINER, builder.build());
                } else if (mode == ChangeMode.REMOVE) {
                    List<ItemStack> newItemSet = new ArrayList<>();
                    List<ItemStack> oldItemSet = new ArrayList<>();
                    if (icc != null) {
                        oldItemSet.addAll(icc.contents());
                    }
                    for (ItemStack content : oldItemSet) {
                        for (ItemType changedItem : changedItems) {
                            if (content.getType() != changedItem.getItem().getMaterial()) {
                                newItemSet.add(content);
                            }
                        }
                    }
                    if (newItemSet.isEmpty()) {
                        itemstack.resetData(DataComponentTypes.CONTAINER);
                    } else {
                        builder.addAll(newItemSet);
                        itemstack.setData(DataComponentTypes.CONTAINER, builder.build());
                    }
                } else if (mode == ChangeMode.DELETE) {
                    itemstack.unsetData(DataComponentTypes.CONTAINER);
                } else if (mode == ChangeMode.RESET) {
                    itemstack.resetData(DataComponentTypes.CONTAINER);
                }

            });
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "container component of " + this.items.toString(event, debug);
    }

}
