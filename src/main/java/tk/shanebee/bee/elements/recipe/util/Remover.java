package tk.shanebee.bee.elements.recipe.util;

import ch.njol.skript.Skript;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class Remover {

    private final String VERSION;
    private final Map<?, Map<?, ?>> recipeMap;
    private Method getMCKey;
    private Class<?> CB_KEY;

    public Remover() {
        VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            CB_KEY = getCBClass("util.CraftNamespacedKey");
            getMCKey = CB_KEY.getDeclaredMethod("toMinecraft", NamespacedKey.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            Skript.warning("[SkRecipe] - Recipe remover failed to load!");
        }
        recipeMap = getRecipeMap();
    }

    public void removeRecipeByKey(NamespacedKey recipeKey) {
        Object key;
        try {
            key = getMCKey.invoke(CB_KEY, recipeKey);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Skript.error("Recipe cant be removed: " + recipeKey.toString());
            return;
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
