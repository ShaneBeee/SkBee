package tk.shanebee.bee.api.util;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import tk.shanebee.bee.api.reflection.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleUtil {

    private static final Map<String, Particle> PARTICLES = new HashMap<>();
    private static final boolean NEW_NMS = Skript.isRunningMinecraft(1, 17);

    // Load and map Minecraft particle names
    // Bukkit does not have any API for getting the Minecraft names of particles (how stupid)
    // This method fetches them from the server and maps them with the Bukkit particle enums
    static {
        Class<?> cbParticle = ReflectionUtils.getOBCClass("CraftParticle");
        Class<?> mcKey;
        if (NEW_NMS) {
            mcKey = ReflectionUtils.getNewNMSClass("net.minecraft.resources.MinecraftKey");
        } else {
            mcKey = ReflectionUtils.getNMSClass("MinecraftKey");
        }
        try {
            assert cbParticle != null;
            Field mc = cbParticle.getDeclaredField("minecraftKey");
            mc.setAccessible(true);
            Field pc = cbParticle.getDeclaredField("bukkit");
            pc.setAccessible(true);

            for (Object enumConstant : cbParticle.getEnumConstants()) {

                assert mcKey != null;
                String KEY = mcKey.getMethod("getKey").invoke(mc.get(enumConstant)).toString();
                Particle PARTICLE = ((Particle) pc.get(enumConstant));

                if (!PARTICLE.toString().contains("LEGACY")) {
                    PARTICLES.put(KEY, PARTICLE);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a string for docs of all names of particles
     *
     * @return Names of all particles in one long string
     */
    public static String getNamesAsString() {
        List<String> names = new ArrayList<>();
        PARTICLES.forEach((s, particle) -> {
            String name = s;

            if (particle.getDataType() != Void.class) {
                name = name + " [" + getDataType(particle) + "]";
            }
            names.add(name);
        });
        Collections.sort(names);
        return StringUtils.join(names, ", ");
    }

    /**
     * Get the Minecraft name of a particle
     *
     * @param particle Particle to get name of
     * @return Minecraft name of particle
     */
    public static String getName(Particle particle) {
        for (String key : PARTICLES.keySet()) {
            if (PARTICLES.get(key) == particle) {
                return key;
            }
        }
        return null;
    }

    /**
     * Parse a particle by it's Minecraft name
     *
     * @param key Minecraft name of particle
     * @return Bukkit particle from Minecraft name (null if not available)
     */
    @Nullable
    public static Particle parse(String key) {
        if (PARTICLES.containsKey(key)) {
            return PARTICLES.get(key);
        }
        return null;
    }

    private static String getDataType(Particle particle) {
        Class<?> t = particle.getDataType();
        if (t == ItemStack.class) {
            return "itemtype";
        } else if (t == Particle.DustOptions.class) {
            return "dust-option";
        } else if (t == BlockData.class) {
            return "blockdata/itemtype";
        }
        return "";
    }

    public static void spawnParticle(@Nullable Player[] players, Particle particle, Location location, int count, Vector offset, double extra, Object data) {
        if (offset == null) return;
        Object particleData = getData(particle, data);
        if (particle.getDataType() != Void.class && particleData == null) return;

        double x = offset.getX();
        double y = offset.getY();
        double z = offset.getZ();
        if (players == null) {
            World world = location.getWorld();
            if (world == null) return;
            world.spawnParticle(particle, location, count, x, y, z, extra, particleData);
        } else {
            for (Player player : players) {
                assert player != null;
                player.spawnParticle(particle, location, count, x, y, z, extra, particleData);
            }
        }
    }

    public static void spawnParticle(@Nullable Player[] players, Particle particle, Location location, int count, Vector offset, Object data) {
        if (offset == null) return;
        Object particleData = getData(particle, data);
        if (particle.getDataType() != Void.class && particleData == null) return;

        double x = offset.getX();
        double y = offset.getY();
        double z = offset.getZ();
        if (players == null) {
            World world = location.getWorld();
            if (world == null) return;
            world.spawnParticle(particle, location, count, x, y, z, particleData);
        } else {
            for (Player player : players) {
                assert player != null;
                player.spawnParticle(particle, location, count, x, y, z, particleData);
            }
        }
    }

    public static void spawnParticle(@Nullable Player[] players, Particle particle, Location location, int count, Object data) {
        Object particleData = getData(particle, data);
        if (particle.getDataType() != Void.class && particleData == null) return;

        if (players == null) {
            World world = location.getWorld();
            if (world == null) return;
            world.spawnParticle(particle, location, count, particleData);
        } else {
            for (Player player : players) {
                assert player != null;
                player.spawnParticle(particle, location, count, particleData);
            }
        }
    }

    private static Object getData(Particle particle, Object data) {
        Class<?> dataType = particle.getDataType();
        if (dataType == ItemStack.class && data instanceof ItemType) {
            return ((ItemType) data).getRandom();
        } else if (dataType == Particle.DustOptions.class && data instanceof Particle.DustOptions) {
            return data;
        } else if (dataType == BlockData.class) {
            if (data instanceof BlockData) {
                return data;
            } else if (data instanceof ItemType) {
                Material material = ((ItemType) data).getMaterial();
                if (material.isBlock()) {
                    return material.createBlockData();
                }
            }
        }
        return null;
    }

}
