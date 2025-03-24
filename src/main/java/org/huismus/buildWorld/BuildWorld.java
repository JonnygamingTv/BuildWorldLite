package com.huismus.buildworld;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Random;

public class BuildWorld extends JavaPlugin {

    private FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        String worldName = config.getString("world-name", "build_world");

        if (Bukkit.getWorld(worldName) == null) {
            WorldCreator creator = new WorldCreator(worldName);
            creator.generator(new CustomFlatChunkGenerator(config));
            World buildWorld = creator.createWorld();
            configureWorld(buildWorld);
        }

        getCommand("tpbuild").setExecutor(this);
        getCommand("tpdefault").setExecutor(this);
    }

    private void configureWorld(World world) {
        if (world == null) return;

        if (config.getBoolean("clear-weather", true)) {
            world.setStorm(false);
            world.setWeatherDuration(Integer.MAX_VALUE);
        }

        if (config.getBoolean("lock-daylight", true)) {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setTime(1000);
        }

        if (config.getBoolean("enable-flight", true)) {
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        }

        String gamemode = config.getString("gamemode", "creative").toUpperCase();
        GameMode mode = GameMode.valueOf(gamemode);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setSpawnLocation(
                config.getInt("spawn-coordinates.x", 0),
                config.getInt("spawn-coordinates.y", 71),
                config.getInt("spawn-coordinates.z", 0)
        );
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (label.equalsIgnoreCase("tpbuild")) {
            String worldName = config.getString("world-name", "build_world");
            World buildWorld = Bukkit.getWorld(worldName);
            if (buildWorld != null) {
                int x = config.getInt("spawn-coordinates.x", 0);
                int y = config.getInt("spawn-coordinates.y", 71);
                int z = config.getInt("spawn-coordinates.z", 0);
                player.teleport(new Location(buildWorld, x + 0.5, y, z + 0.5));
            }
            return true;
        }

        if (label.equalsIgnoreCase("tpdefault")) {
            World defaultWorld = Bukkit.getWorlds().get(0);
            Location spawn = defaultWorld.getSpawnLocation();
            player.teleport(spawn);
            return true;
        }

        return false;
    }

    public static class CustomFlatChunkGenerator extends ChunkGenerator {

        private final FileConfiguration config;

        public CustomFlatChunkGenerator(FileConfiguration config) {
            this.config = config;
        }

        @Override
        public void generateSurface(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
            List<?> layers = config.getList("layers");
            if (layers == null) return;

            for (Object entry : layers) {
                if (entry instanceof java.util.Map<?, ?> map) {
                    String blockName = (String) map.get("block");
                    int fromY = (int) map.get("fromY");
                    int toY = (int) map.get("toY");

                    Material material = Material.getMaterial(blockName.toUpperCase());
                    if (material == null || !material.isBlock()) continue;

                    for (int y = fromY; y <= toY; y++) {
                        chunkData.setRegion(0, y, 0, 16, y + 1, 16, material);
                    }
                }
            }
        }

        @Override
        public boolean isParallelCapable() {
            return true;
        }
    }
}