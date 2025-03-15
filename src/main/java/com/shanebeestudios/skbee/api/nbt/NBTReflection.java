package com.shanebeestudios.skbee.api.nbt;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.reflection.ReflectionUtils;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings({"SequencedCollectionMethodCanBeUsed", "CallToPrintStackTrace", "DataFlowIssue"})
public class NBTReflection {

    // Classes
    private static Class<?> CRAFT_ITEM_STACK_CLASS;

    // Fields/Objects
    private static Object CODEC;
    private static Object REGISTRY_ACCESS;
    private static Object NBT_OPS_INSTANCE;

    // Methods
    private static Method GET_COMPONENTS_METHOD;
    private static Method ENCODE_METHOD;
    private static Method GET_OR_ELSE;
    private static Method CREATE_SERIALIZER_METHOD;

    // Constructors
    private static Constructor<?> NBT_COMPOUND_CONSTRUCTOR;

    static {
        try {
            // Classes
            CRAFT_ITEM_STACK_CLASS = ReflectionUtils.getOBCClass("inventory.CraftItemStack");
            Class<?> compoundTag = ReflectionUtils.getNMSClass("net.minecraft.nbt.CompoundTag");
            Class<?> craftWorld = ReflectionUtils.getOBCClass("CraftWorld");
            Class<?> dataComponentMap = ReflectionUtils.getNMSClass("net.minecraft.core.component.DataComponentMap");
            Class<?> dataResult = ReflectionUtils.getNMSClass("com.mojang.serialization.DataResult");
            Class<?> dynamicOps = ReflectionUtils.getNMSClass("com.mojang.serialization.DynamicOps");
            Class<?> encoder = ReflectionUtils.getNMSClass("com.mojang.serialization.Encoder");
            Class<?> holderLookup = ReflectionUtils.getNMSClass("net.minecraft.core.HolderLookup$Provider");
            Class<?> itemStack = ReflectionUtils.getNMSClass("net.minecraft.world.item.ItemStack");
            Class<?> level = ReflectionUtils.getNMSClass("net.minecraft.world.level.Level");
            Class<?> nbtOps = ReflectionUtils.getNMSClass("net.minecraft.nbt.NbtOps");

            // Fields/Objects
            CODEC = ReflectionUtils.getField("CODEC", dataComponentMap, null);
            Object nmsWorld = craftWorld.getDeclaredMethod("getHandle").invoke(Bukkit.getWorlds().get(0));
            REGISTRY_ACCESS = level.getDeclaredMethod("registryAccess").invoke(nmsWorld);
            NBT_OPS_INSTANCE = ReflectionUtils.getField("INSTANCE", nbtOps, null);

            // Methods
            GET_COMPONENTS_METHOD = itemStack.getDeclaredMethod("getComponents");
            ENCODE_METHOD = encoder.getMethod("encode", Object.class, dynamicOps, Object.class);
            GET_OR_ELSE = dataResult.getDeclaredMethod("getOrThrow");
            CREATE_SERIALIZER_METHOD = holderLookup.getDeclaredMethod("createSerializationContext", dynamicOps);

            // Constructors
            NBT_COMPOUND_CONSTRUCTOR = compoundTag.getDeclaredConstructor();

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            if (SkBee.isDebug()) e.printStackTrace();
        }
    }

    /**
     * Get the vanilla version of NBT of an item
     * <br>This will show components which don't normally show in NBT
     *
     * @param itemStack Item to grab NBT from
     * @return Vanilla NBT of item
     */
    @SuppressWarnings({"deprecation"})
    public static NBTCompound getVanillaNBT(ItemStack itemStack) {
        try {
            Object nmsItem = ReflectionUtils.getField("handle", CRAFT_ITEM_STACK_CLASS, itemStack);
            Object components = GET_COMPONENTS_METHOD.invoke(nmsItem);
            Object serial = CREATE_SERIALIZER_METHOD.invoke(REGISTRY_ACCESS, NBT_OPS_INSTANCE);
            Object newNBTCompound = NBT_COMPOUND_CONSTRUCTOR.newInstance();

            Object encoded = ENCODE_METHOD.invoke(CODEC, components, serial, newNBTCompound);
            Object nmsNbt = GET_OR_ELSE.invoke(encoded);
            NBTCompound itemNbt = NBTItem.convertItemtoNBT(itemStack);
            itemNbt.getOrCreateCompound("components").mergeCompound(new NBTContainer(nmsNbt));
            return itemNbt;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            if (SkBee.isDebug()) e.printStackTrace();
            return new NBTContainer();
        }
    }

}
