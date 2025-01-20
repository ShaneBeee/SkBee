package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.registrations.Classes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Spawn Egg from Entity")
@Description("Gets a spawn egg from an entity/entityType. Requires Paper, or Spigot 1.20.1+.")
@Examples({"set {_egg} to spawn egg of last spawned entity",
    "set {_egg} to spawn egg of (spawner type of target block)"})
@Since("2.14.0")
public class ExprSpawnEggFromEntity extends SimplePropertyExpression<Object, ItemType> {

    private static final ItemFactory ITEM_FACTORY = Bukkit.getItemFactory();


    static {
        if (Skript.methodExists(ItemFactory.class, "getSpawnEgg", EntityType.class)) {
            register(ExprSpawnEggFromEntity.class, ItemType.class, "spawn egg", "entities/entitydatas");
        }
    }

    @Override
    public @Nullable ItemType convert(Object object) {
        EntityType entityType;
        if (object instanceof Entity entity) {
            entityType = entity.getType();
        } else if (object instanceof EntityData<?> entityData) {
            entityType = EntityUtils.toBukkitEntityType(entityData);
        } else {
            return null;
        }
        if (entityType == null) {
            return null;
        }
        Object spawnEggObject = ITEM_FACTORY.getSpawnEgg(entityType);
        // Paper method originally returned ItemStack
        // New Bukkit method returns Material
        if (spawnEggObject instanceof ItemStack itemStack) {
            return new ItemType(itemStack);
        } else if (spawnEggObject instanceof Material material) {
            return new ItemType(material);
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
