package com.prismcore.survival.particle;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Runtime-safe particle bridge.
 *
 * Design goals:
 * - Never references version-specific Particle enum constants directly.
 * - Resolves names at runtime with aliases.
 * - Keeps Bukkit particle spawning on the server thread.
 * - Gracefully falls back when a particle does not exist on an older server.
 *
 * Compile target: modern Paper API.
 * Runtime strategy: dynamic enum lookup allows the same source to survive
 * renamed/removed Particle constants across supported Bukkit generations.
 */
public final class ParticleCompat {

    private static final Map<String, Particle> CACHE = new ConcurrentHashMap<>();

    private ParticleCompat() {
    }

    public static Particle resolve(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }

        String key = normalize(input);
        Particle cached = CACHE.get(key);
        if (cached != null) {
            return cached;
        }

        for (String candidate : candidates(key)) {
            try {
                Particle particle = Particle.valueOf(candidate);
                CACHE.put(key, particle);
                return particle;
            } catch (IllegalArgumentException ignored) {
                // Try the next compatibility alias.
            }
        }

        return null;
    }

    public static void spawn(World world, Particle particle, Location location, int count) {
        if (world == null || particle == null || location == null || count <= 0) {
            return;
        }

        world.spawnParticle(particle, location, count, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    public static void spawn(World world, String particleName, Location location, int count) {
        spawn(world, resolve(particleName), location, count);
    }

    public static void spawnDirectional(
            World world,
            Particle particle,
            Location location,
            int count,
            double offsetX,
            double offsetY,
            double offsetZ,
            double extra
    ) {
        if (world == null || particle == null || location == null || count <= 0) {
            return;
        }

        world.spawnParticle(
                particle,
                location,
                count,
                offsetX,
                offsetY,
                offsetZ,
                extra
        );
    }

    public static void spawnDust(
            World world,
            String particleName,
            Location location,
            int count,
            Color color,
            float size
    ) {
        Particle particle = resolve(particleName);
        if (particle == null || world == null || location == null || count <= 0) {
            return;
        }

        // DUST has existed for a long time and DustOptions remains the Bukkit
        // data contract on modern Paper.
        if (particle == Particle.DUST) {
            world.spawnParticle(
                    particle,
                    location,
                    count,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.0D,
                    new Particle.DustOptions(color, Math.max(0.01F, size))
            );
            return;
        }

        // If a config accidentally points a non-dust particle at a color
        // setting, still render the requested effect instead of crashing.
        spawn(world, particle, location, count);
    }

    private static String normalize(String value) {
        return value.trim()
                .toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
    }

    private static String[] candidates(String key) {
        return switch (key) {
            case "BLOCK_CRACK", "BLOCK_DUST", "BLOCK" ->
                    new String[]{"BLOCK", "BLOCK_CRACK", "BLOCK_DUST"};
            case "CRIT_MAGIC", "ENCHANTED_HIT" ->
                    new String[]{"ENCHANTED_HIT", "CRIT_MAGIC", "CRIT"};
            case "DRIP_LAVA", "DRIPPING_LAVA" ->
                    new String[]{"DRIPPING_LAVA", "DRIP_LAVA"};
            case "DRIP_WATER", "DRIPPING_WATER" ->
                    new String[]{"DRIPPING_WATER", "DRIP_WATER"};
            case "SMOKE_NORMAL", "SMOKE" ->
                    new String[]{"SMOKE_NORMAL", "SMOKE"};
            case "REDSTONE" ->
                    new String[]{"DUST", "REDSTONE"};
            default ->
                    new String[]{key};
        };
    }
}
