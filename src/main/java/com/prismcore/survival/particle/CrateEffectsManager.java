package com.prismcore.survival.manager;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.particle.ParticleCompat;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Refactored replacement for the legacy crate particle renderer.
 *
 * Important:
 * Bukkit/Paper particle spawning is deliberately synchronous. The renderer
 * performs only lightweight trig calculations on that same tick and never
 * touches Bukkit objects from an async thread.
 */
public final class CrateEffectsManager {

    private static final double TWO_PI = Math.PI * 2.0D;

    private final PrismSurvival plugin;
    private final Map<String, List<String>> crateEffectsCache = new ConcurrentHashMap<>();

    private double time;
    private boolean running;

    public CrateEffectsManager(PrismSurvival plugin) {
        this.plugin = plugin;
        startTask();
    }

    private void startTask() {
        if (running) {
            return;
        }
        running = true;

        // Keep the original cadence: every 4 ticks.
        plugin.getSchedulerAdapter().runTaskTimer(this::tick, 1L, 4L);
    }

    private void tick() {
        // Prevent a long-running double from growing without bound.
        time += 0.1D;
        if (time >= TWO_PI * 1000.0D) {
            time = 0.0D;
        }

        Map<Location, String> locations = plugin.getCrateLocationRegistry().getAllLocations();
        if (locations == null || locations.isEmpty()) {
            return;
        }

        for (Map.Entry<Location, String> entry : locations.entrySet()) {
            Location crate = entry.getKey();
            if (!isRenderable(crate)) {
                continue;
            }

            List<String> effects = getEffectsForCrate(entry.getValue());
            if (effects.isEmpty()) {
                continue;
            }

            Location center = crate.clone().add(0.5D, 0.5D, 0.5D);
            for (String effect : effects) {
                renderEffect(center, effect);
            }
        }
    }

    private boolean isRenderable(Location location) {
        if (location == null) {
            return false;
        }

        World world = location.getWorld();
        if (world == null) {
            return false;
        }

        return world.isChunkLoaded(
                location.getBlockX() >> 4,
                location.getBlockZ() >> 4
        );
    }

    private List<String> getEffectsForCrate(String crateType) {
        if (crateType == null || crateType.isBlank()) {
            return Collections.emptyList();
        }

        String key = crateType.toLowerCase(Locale.ROOT);
        List<String> cached = crateEffectsCache.get(key);
        if (cached != null) {
            return cached;
        }

        File file = new File(plugin.getDataFolder(), key + ".yml");
        if (!file.exists()) {
            return Collections.emptyList();
        }

        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        List<String> loaded = configuration.getStringList("effects");

        if (loaded == null || loaded.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> immutable = Collections.unmodifiableList(new ArrayList<>(loaded));
        crateEffectsCache.put(key, immutable);
        return immutable;
    }

    public void clearCache(String crateType) {
        if (crateType != null) {
            crateEffectsCache.remove(crateType.toLowerCase(Locale.ROOT));
        }
    }

    public void clearAllCache() {
        crateEffectsCache.clear();
    }

    private void renderEffect(Location location, String effectName) {
        if (location == null || effectName == null) {
            return;
        }

        String effect = effectName.trim().toUpperCase(Locale.ROOT);

        switch (effect) {
            case "HELIX" -> renderHelix(location);
            case "DOUBLE_HELIX" -> renderDoubleHelix(location);
            case "HALO" -> renderHalo(location);
            case "GROUND_RINGS" -> renderGroundRings(location);
            case "VORTEX" -> renderVortex(location);
            case "FOUNTAIN" -> renderFountain(location);
            case "DISCO" -> renderDisco(location);
            case "BEACON" -> renderBeacon(location);
            case "PULSE" -> renderPulse(location);
            case "ORBIT" -> renderOrbit(location);
            case "ENDER" -> renderEnder(location);
            case "TORNADO" -> renderTornado(location);
            case "SPHERE" -> renderSphere(location);
            case "LAVA_DRIP" -> renderLavaDrip(location);
            case "ENCHANT" -> renderEnchant(location);
            case "FLAME_CROWN" -> renderFlameCrown(location);
            default -> {
                // Unknown effect names are ignored instead of crashing the
                // entire repeating task.
            }
        }
    }

    private void renderHelix(Location c) {
        World w = c.getWorld();
        var p = ParticleCompat.resolve("FIREWORK");
        if (w == null || p == null) return;

        for (double y = 0.0D; y <= 2.0D; y += 0.15D) {
            double a = y * 4.0D + time;
            ParticleCompat.spawn(w, p, c.clone().add(
                    1.2D * Math.cos(a),
                    y - 0.5D,
                    1.2D * Math.sin(a)
            ), 1);
        }
    }

    private void renderDoubleHelix(Location c) {
        World w = c.getWorld();
        if (w == null) return;

        var p1 = ParticleCompat.resolve("HAPPY_VILLAGER");
        var p2 = ParticleCompat.resolve("SOUL_FIRE_FLAME");
        if (p1 == null && p2 == null) return;

        for (double y = 0.0D; y <= 2.0D; y += 0.2D) {
            double a = y * 3.0D + time;
            double x = Math.cos(a);
            double z = Math.sin(a);

            if (p1 != null) {
                ParticleCompat.spawn(w, p1, c.clone().add(x, y - 0.5D, z), 1);
            }
            if (p2 != null) {
                ParticleCompat.spawn(w, p2, c.clone().add(-x, y - 0.5D, -z), 1);
            }
        }
    }

    private void renderHalo(Location c) {
        World w = c.getWorld();
        var p = ParticleCompat.resolve("END_ROD");
        if (w == null || p == null) return;

        double radius = 0.8D;
        double y = 1.2D + Math.sin(time) * 0.2D;

        for (int i = 0; i < 20; i++) {
            double a = TWO_PI * i / 20.0D + time;
            ParticleCompat.spawn(w, p, c.clone().add(
                    radius * Math.cos(a), y, radius * Math.sin(a)
            ), 1);
        }
    }

    private void renderGroundRings(Location c) {
        World w = c.getWorld();
        var p = ParticleCompat.resolve("WITCH");
        if (w == null || p == null) return;

        double radius = (time % 2.0D);
        for (int i = 0; i < 30; i++) {
            double a = TWO_PI * i / 30.0D;
            ParticleCompat.spawn(w, p, c.clone().add(
                    radius * Math.cos(a), -0.4D, radius * Math.sin(a)
            ), 1);
        }
    }

    private void renderVortex(Location c) {
        World w = c.getWorld();
        var p = ParticleCompat.resolve("PORTAL");
        if (w == null || p == null) return;

        for (int i = 0; i < 3; i++) {
            double phase = (time * 2.0D + i * 2.0D) % 3.0D;
            double radius = 1.5D * (1.0D - phase / 3.0D);
            double a = phase * 4.0D + time * 2.0D;

            ParticleCompat.spawn(w, p, c.clone().add(
                    radius * Math.cos(a),
                    phase,
                    radius * Math.sin(a)
            ), 1);
        }
    }

    private void renderFountain(Location c) {
        World w = c.getWorld();
        var p = ParticleCompat.resolve("WATER_SPLASH");
        if (w == null || p == null) return;

        for (int i = 0; i < 12; i++) {
            double a = TWO_PI * i / 12.0D + time;
            double r = 0.35D + (i % 3) * 0.15D;
            double y = 0.2D + Math.abs(Math.sin(time + i)) * 1.3D;

            ParticleCompat.spawnDirectional(
                    w, p, c.clone().add(
                            r * Math.cos(a), y, r * Math.sin(a)
                    ),
                    1, 0.0D, 0.08D, 0.0D, 0.0D
            );
        }
    }

    private void renderDisco(Location c) {
        World w = c.getWorld();
        var p = ParticleCompat.resolve("NOTE");
        if (w == null || p == null) return;

        for (int i = 0; i < 12; i++) {
            double a = TWO_PI * i / 12.0D + time;
            double y = 0.4D + (i % 4) * 0.35D;

            ParticleCompat.spawn(w, p, c.clone().add(
                    0.8D * Math.cos(a),
                    y,
                    0.8D * Math.sin(a)
            ), 1);
        }
    }

    private void renderBeacon(Location c) {
        World w = c.getWorld();
        var p = ParticleCompat.resolve("END_ROD");
        if (w == null || p == null) return;

        for (double y = 0.0D; y <= 2.5D; y += 0.2D) {
            double r = 0.15D + y * 0.08D;
            double a = time * 1.5D + y * 2.0D;

            ParticleCompat.spawn(w, p, c.clone().add(
                    r * Math.cos(a), y, r * Math.sin(a)
            ), 1);
        }
    }

    private void renderPulse(Location c) {
        World w = c.getWorld();
        var p = ParticleCompat.resolve("ELECTRIC_SPARK");
        if (w == null || p == null) return;

        double radius = 0.35D + (Math.sin(time * 2.0D) + 1.0D) * 0.35D;
        for (int i = 0; i < 18; i++) {
            double a = TWO_PI * i / 18.0D;
            ParticleCompat.spawn(w, p, c.clone().add(
                    radius * Math.cos(a),
                    1.0D,
                    radius * Math.sin(a)
            ), 1);
        }
    }

    private void renderOrbit(Location c) {
        World w = c.getWorld();
        var p = ParticleCompat.resolve("ENCHANT");
        if (w == null || p == null) return;

        for (int i = 0; i < 3; i++) {
            double a = time * 1.7D + i * TWO_PI / 3.0D;
            ParticleCompat.spawn(w, p, c.clone().add(
                    0.9D * Math.cos(a),
                    1.0D + 0.25D * Math.sin(a * 2.0D),
                    0.9D * Math.sin(a)
            ), 1);
        }
    }

    private void renderEnder(Location c) {
        World w = c.getWorld();
        var p = ParticleCompat.resolve("PORTAL");
        if (w == null || p == null) return;

        for (int i = 0; i < 16; i++) {
            double a = TWO_PI * i / 16.0D + time;
            double y = (i % 8) * 0.22D;

            ParticleCompat.spawn(w, p, c.clone().add(
                    0.55D * Math.cos(a),
                    y,
                    0.55D * Math.sin(a)
            ), 1);
        }
    }

    private void renderTornado(Location c) {
        World w = c.getWorld();
        var p = ParticleCompat.resolve("CLOUD");
        if (w == null || p == null) return;

        for (int i = 0; i < 16; i++) {
            double y = i * 0.14D;
            double radius = 0.15D + y * 0.32D;
            double a = time * 2.0D + y * 5.0D;

            ParticleCompat.spawn(w, p, c.clone().add(
                    radius * Math.cos(a),
                    y,
                    radius * Math.sin(a)
            ), 1);
        }
    }

    private void renderSphere(Location c) {
        World w = c.getWorld();
        var p = ParticleCompat.resolve("END_ROD");
        if (w == null || p == null) return;

        final int latitudes = 7;
        final int longitudes = 14;

        for (int lat = 0; lat <= latitudes; lat++) {
            double phi = Math.PI * lat / latitudes;
            double y = Math.cos(phi) * 1.0D + 1.0D;
            double ring = Math.sin(phi);

            for (int lon = 0; lon < longitudes; lon++) {
                double a = TWO_PI * lon / longitudes + time;
                ParticleCompat.spawn(w, p, c.clone().add(
                        ring * Math.cos(a),
                        y - 1.0D,
                        ring * Math.sin(a)
                ), 1);
            }
        }
    }

    private void renderLavaDrip(Location c) {
        World w = c.getWorld();
        var p = ParticleCompat.resolve("DRIPPING_LAVA");
        if (w == null || p == null) return;

        for (int i = 0; i < 6; i++) {
            double a = TWO_PI * i / 6.0D + time;
            double y = 0.4D + (i % 3) * 0.55D;

            ParticleCompat.spawn(w, p, c.clone().add(
                    0.45D * Math.cos(a),
                    y,
                    0.45D * Math.sin(a)
            ), 1);
        }
    }

    private void renderEnchant(Location c) {
        World w = c.getWorld();
        var p = ParticleCompat.resolve("ENCHANT");
        if (w == null || p == null) return;

        for (int i = 0; i < 5; i++) {
            double x = (Math.random() - 0.5D) * 3.0D;
            double z = (Math.random() - 0.5D) * 3.0D;
            Location target = c.clone().add(x, 2.0D, z);

            double dx = c.getX() - target.getX();
            double dy = c.getY() + 0.5D - target.getY();
            double dz = c.getZ() - target.getZ();

            double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (length < 1.0E-6D) continue;

            ParticleCompat.spawnDirectional(
                    w, p, target, 1,
                    dx / length * 0.2D,
                    dy / length * 0.2D,
                    dz / length * 0.2D,
                    0.0D
            );
        }
    }

    private void renderFlameCrown(Location c) {
        World w = c.getWorld();
        var p = ParticleCompat.resolve("FLAME");
        if (w == null || p == null) return;

        final int points = 8;
        for (int i = 0; i < points; i++) {
            double a = TWO_PI * i / points + time;
            ParticleCompat.spawnDirectional(
                    w,
                    p,
                    c.clone().add(
                            0.7D * Math.cos(a),
                            1.2D,
                            0.7D * Math.sin(a)
                    ),
                    1,
                    0.0D,
                    0.05D,
                    0.0D,
                    0.0D
            );
        }
    }
}
