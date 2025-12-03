package com.shanebeestudios.skbee.api.nbt;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.reflection.ReflectionUtils;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings({"SequencedCollectionMethodCanBeUsed", "CallToPrintStackTrace", "DataFlowIssue"})
public class NBTReflection {

    // Classes
    private static Class<?> CRAFT_ITEM_STACK_CLASS;
    private static final Class<?> NMS_COMPONENT_CLASS = ReflectionUtils.getNMSClass("net.minecraft.network.chat.Component");
    private static final Class<?> CRAFT_CHAT_MESSAGE_CLASS = ReflectionUtils.getOBCClass("util.CraftChatMessage");
    private static final Class<?> TEXT_TAG_VISITOR_CLASS = ReflectionUtils.getNMSClass("net.minecraft.nbt.TextComponentTagVisitor");
    private static final Class<?> NBT_TAG_CLASS = ReflectionUtils.getNMSClass("net.minecraft.nbt.Tag");

    // Fields/Objects
    private static Object CODEC;
    private static Object REGISTRY_ACCESS;
    private static Object NBT_OPS_INSTANCE;

    // Methods
    private static Method GET_COMPONENTS_METHOD;
    private static Method ENCODE_METHOD;
    private static Method GET_OR_ELSE;
    private static Method CREATE_SERIALIZER_METHOD;
    private static final Method FROM_COMPONENT;
    private static final Method VISIT_METHOD;

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

        Method from_comp = null;
        Method visit = null;
        try {
            assert TEXT_TAG_VISITOR_CLASS != null;
            assert CRAFT_CHAT_MESSAGE_CLASS != null;
            visit = TEXT_TAG_VISITOR_CLASS.getDeclaredMethod("visit", NBT_TAG_CLASS);
            from_comp = CRAFT_CHAT_MESSAGE_CLASS.getMethod("fromComponent", NMS_COMPONENT_CLASS);
        } catch (NoSuchMethodException e) {
            if (SkBee.isDebug()) e.printStackTrace();
        }
        FROM_COMPONENT = from_comp;
        VISIT_METHOD = visit;
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

    /**
     * Get a pretty NBT string
     * <p>This is the same as what vanilla Minecraft outputs when using the '/data' command</p>
     *
     * @param compound Compound to convert to pretty
     * @param split    When null NBT will print on one long line, if not null NBT compound will be
     *                 split into lines with JSON style, and this string will start each line off
     *                 (usually spaces)
     * @return Pretty string of NBTCompound
     */
    @SuppressWarnings("deprecation")
    public static @Nullable String getPrettyNBT(NBTCompound compound, String split) {
        Object nmsNBT = new NBTContainer(compound.toString()).getCompound();
        String s = split != null ? split : "";
        try {
            Object tagVisitorInstance = TEXT_TAG_VISITOR_CLASS.getConstructor(String.class).newInstance(s);
            Object prettyComponent = VISIT_METHOD.invoke(tagVisitorInstance, nmsNBT);
            return ((String) FROM_COMPONENT.invoke(CRAFT_CHAT_MESSAGE_CLASS, prettyComponent));
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
