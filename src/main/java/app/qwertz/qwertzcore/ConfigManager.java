/*
        Copyright (C) 2024 QWERTZ_EXE

        This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
        as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

        This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
        without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
        See the GNU Lesser General Public License for more details.

        You should have received a copy of the GNU Lesser General Public License along with this program.
        If not, see <http://www.gnu.org/licenses/>.
*/

package app.qwertz.qwertzcore;

import app.qwertz.qwertzcore.npc.CustomNPC;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.world.ServerWorld;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private static final String CONFIG_FILE = "qwertzcore_config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Config instance;

    public static void setOnJoinSpawn(double x, double y, double z, float yaw, float pitch) {
        instance.spawnX = x;
        instance.spawnY = y;
        instance.spawnZ = z;
        instance.spawnYaw = yaw;
        instance.spawnPitch = pitch;
        save();
    }
    public static class Config {
        public List<String> commandsToKeep = Arrays.asList("op", "deop", "gamemode", "gamerule", "give", "kick", "ban", "unban");
        public boolean disableMinecartDismounting = false;
        public boolean destroyMinecartOnLeave = true;
        public boolean allowBlockBreak = false;
        public boolean allowBlockPlace = false;
        public boolean allowPvp = false;
        public double spawnX = 0;
        public double spawnY = 100;
        public double spawnZ = 0;
        public float spawnYaw = 0;
        public float spawnPitch = 0;
        public boolean onJoinTp = false;
        public Map<String, Rollercoaster> rollercoasters = new HashMap<>();
        public Map<String, JsonObject> npcs = new HashMap<>();
    }
    public static void toggleMinecartDismounting() {
        instance.disableMinecartDismounting = !instance.disableMinecartDismounting;
        save();
    }
    public static void toggleOnJoinTp() {
        instance.onJoinTp = !instance.onJoinTp;
        save();
    }
    public static void saveNPC(CustomNPC npc) {
        instance.npcs.put(npc.getId(), npc.toJson());
        save();
    }
    public static CustomNPC loadNPC(String id) {
        JsonObject npcJson = instance.npcs.get(id);
        return npcJson != null ? CustomNPC.fromJson(id, npcJson) : null;
    }
    public static void updateAllNPCs(ServerWorld world) {
        instance.npcs.forEach((name, npcJson) -> {
            CustomNPC npc = ConfigManager.loadNPC(name);
            if (npc != null) {
                npc.spawn(world); // Spawn the NPC to set its position in the world
                npc.updatePosition(world, npc.getPosition(), npc.getYaw(), npc.getPitch()); // Ensure the NPC is at the correct position
                System.out.println("Updated NPC position on startup: " + npc.getName() + " to " + npc.getPosition());
            }
        });
    }
    public static void removeNPC(String name) {
        instance.npcs.remove(name);
        save();
    }
    public static void setAllowBlockBreak(boolean b) {
        instance.allowBlockBreak = b;
        save();
    }
    public static void setAllowBlockPlace(boolean b) {
        instance.allowBlockPlace = b;
        save();
    }
    public static void setAllowPvp(boolean b) {
        instance.allowPvp = b;
        save();
    }
    public static void toggleDestroyMinecartOnLeave() {
        instance.destroyMinecartOnLeave = !instance.destroyMinecartOnLeave;
        save();
    }

    public static void addCommand(String command) {
        if (!instance.commandsToKeep.contains(command)) {
            instance.commandsToKeep.add(command);
            save();
        }
    }

    public static void removeCommand(String command) {
        if (instance.commandsToKeep.remove(command)) {
            save();
        }
    }
    public static Config getConfig() {
        if (instance == null) {
            load();
        }
        return instance;
    }

    public static void load() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE);
        if (configPath.toFile().exists()) {
            try (Reader reader = new FileReader(configPath.toFile())) {
                instance = GSON.fromJson(reader, Config.class);
            } catch (IOException e) {
                System.err.println("Error loading config: " + e.getMessage());
                instance = new Config();
            }
        } else {
            instance = new Config();
            save();
        }
    }

    public static void save() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE);
        try (Writer writer = new FileWriter(configPath.toFile())) {
            GSON.toJson(instance, writer);
        } catch (IOException e) {
            System.err.println("Error saving config: " + e.getMessage());
        }
    }
}