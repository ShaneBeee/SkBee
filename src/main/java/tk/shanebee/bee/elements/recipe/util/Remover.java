package tk.shanebee.bee.elements.recipe.util;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class Remover {

    private final String VERSION;
    private final Constructor<?> keyConstructor;
    private final Map<?, Map<?, ?>> recipeMap;

    public Remover() throws ClassNotFoundException, NoSuchMethodException {
        VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        Class<?> MC_KEY = getNMSClass("MinecraftKey");
        keyConstructor = MC_KEY.getDeclaredConstructor(String.class);
        recipeMap = getRecipeMap();
    }

    public void removeRecipeByKey(String recipeKey) {
        if (recipeKey.equals("*")) {
            removeAll();
            return;
        }
        Object key;
        try {
            key = keyConstructor.newInstance(recipeKey);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return;
            //e.printStackTrace();
        }
        recipeMap.values().forEach(recipes -> recipes.entrySet().removeIf(entry -> entry.getKey().equals(key)));
    }

    public void removeAll() {
        recipeMap.values().forEach(recipes -> recipes.entrySet().removeIf(entry ->
                entry.getKey().toString().contains("minecraft:")));
    }

    @SuppressWarnings("unchecked")
    private Map<?, Map<?, ?>> getRecipeMap() {
        try {
            Class<?> CRAFT_SERVER = getCBClass("CraftServer");
            Method getServer = CRAFT_SERVER.getMethod("getServer");
            Object dediServer = getServer.invoke(Bukkit.getServer());

            Class<?> DEDI_SERVER = getNMSClass("DedicatedServer");
            Method getCraftingManager = DEDI_SERVER.getMethod("getCraftingManager");
            Object craftingManager = getCraftingManager.invoke(dediServer);

            Class<?> CRAFTING_MANAGER = getNMSClass("CraftingManager");
            Field recipeField = CRAFTING_MANAGER.getField("recipes");

            return ((Map<?, Map<?, ?>>) recipeField.get(craftingManager));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
        String name = "net.minecraft.server." + VERSION + "." + nmsClassString;
        return Class.forName(name);
    }

    @SuppressWarnings("SameParameterValue")
    private Class<?> getCBClass(String cbClassString) throws ClassNotFoundException {
        String name = "org.bukkit.craftbukkit." + VERSION + "." + cbClassString;
        return Class.forName(name);
    }
}
