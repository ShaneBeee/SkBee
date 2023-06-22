package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Spawn Egg from Entity")
@Description("Gets a spawn egg from an entity/entityType. Requires Paper.")
@Examples({"set {_egg} to spawn egg of last spawned entity",
        "set {_egg} to spawn egg of (spawner type of target block)"})
@Since("INSERT VERSION")
public class ExprSpawnEggFromEntity extends SimplePropertyExpression<Object, ItemType> {

    private static final ItemFactory ITEM_FACTORY = Bukkit.getItemFactory();


    static {
        if (Skript.methodExists(ItemFactory.class, "getSpawnEgg", EntityType.class)) {
            register(ExprSpawnEggFromEntity.class, ItemType.class, "spawn egg", "entities/entitydatas");
        }
    }

    @Override
    public @Nullable ItemType convert(Object object) {
        EntityType entityType = null;
        if (object instanceof Entity entity) {
            entityType = entity.getType();
        } else if (object instanceof EntityData<?> entityData) {
            entityType = EntityUtils.getByClass(entityData.getType());
        }
        if (entityType != null) {
            ItemStack spawnEgg = ITEM_FACTORY.getSpawnEgg(entityType);
            if (spawnEgg != null) return new ItemType(spawnEgg);
        }
        return null;
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "spawn egg";
    }

}
