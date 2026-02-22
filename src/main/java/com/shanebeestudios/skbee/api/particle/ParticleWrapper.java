package com.shanebeestudios.skbee.api.particle;

import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.util.StringUtils;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleWrapper {

    private static final Map<String, ParticleWrapper> PARTICLES = new HashMap<>();
    private static final Map<ParticleWrapper, String> PARTICLE_NAMES = new HashMap<>();

    static {
        Registry.PARTICLE_TYPE.forEach(particle -> {
            String key = particle.getKey().getKey();
            ParticleWrapper particleWrapper = new ParticleWrapper(key, particle);

            PARTICLES.put(key, particleWrapper);
            PARTICLE_NAMES.put(particleWrapper, key);
        });
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

            if (particle.getParticle().getDataType() != Void.class) {
                name = name + " [" + ParticleUtil.getDataType(particle.getParticle()) + "]";
            }
            names.add(name);
        });
        Collections.sort(names);
        return StringUtils.join(names, ", ");
    }

    /**
     * Get a list of all available particles
     *
     * @return List of all available particles
     */
    public static List<ParticleWrapper> getAvailableParticles() {
        return new ArrayList<>(PARTICLES.values());
    }

    public static Parser<ParticleWrapper> getParser() {
        return new Parser<>() {

            @Override
            public @Nullable ParticleWrapper parse(String s, ParseContext context) {
                return ParticleWrapper.parse(s);
            }

            @Override
            public String toString(ParticleWrapper particle, int flags) {
                return particle.getKey();
            }

            @Override
            public String toVariableNameString(ParticleWrapper particle) {
                return "minecraft_particle:" + particle.getKey();
            }
        };
    }

    /**
     * Get the Minecraft name of a particle
     *
     * @param particle Particle to get name of
     * @return Minecraft name of particle
     */
    public static String getName(ParticleWrapper particle) {
        return particle.getKey();
    }


    public static @Nullable ParticleWrapper parse(String key) {
        key = key.toLowerCase();
        if (key.contains(" ")) {
            key = key.replace(" ", "_");
        }
        if (PARTICLES.containsKey(key)) {
            return PARTICLES.get(key);
        }
        return null;
    }

    private final String key;
    private final Particle particle;

    private ParticleWrapper(String key, Particle particle) {
        this.key = key;
        this.particle = particle;
    }

    public String getKey() {
        return this.key;
    }

    public Particle getParticle() {
        return this.particle;
    }

}
