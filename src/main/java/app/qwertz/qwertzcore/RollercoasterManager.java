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

import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RollercoasterManager {
    private static final Map<String, Rollercoaster> rollercoasters = new HashMap<>();
    private static final long SPAWN_DELAY = 10000; // 10 seconds in milliseconds

    // Create a new rollercoaster
    public static void createRollercoaster(String name, BlockPos spawnPos) {
        rollercoasters.put(name, new Rollercoaster(name, spawnPos));
        saveRollercoasters(); // Save rollercoasters to config
    }

    // Delete a rollercoaster
    public static void deleteRollercoaster(String name) {
        rollercoasters.remove(name);
        saveRollercoasters(); // Save changes to config
    }

    // Get all rollercoasters
    public static Map<String, Rollercoaster> getRollercoasters() {
        return rollercoasters;
    }

    // Queue a player for a rollercoaster
    public static void queuePlayer(String rollercoasterName, ServerPlayerEntity player) {
        Rollercoaster rollercoaster = rollercoasters.get(rollercoasterName);

        // Check if the player is currently riding a minecart
        if (player.getVehicle() instanceof MinecartEntity) {
            player.sendMessage(Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §cYou cannot join a queue while riding a minecart."), false);
            return;
        }

        // Check if the player is already in a queue
        for (Rollercoaster rc : rollercoasters.values()) {
            if (rc.getPlayerQueue().contains(player.getUuid())) {
                player.sendMessage(Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §cYou are already in a queue for another rollercoaster."), false);
                return;
            }
        }

        // If checks passed, add the player to the queue
        if (rollercoaster != null) {
            rollercoaster.queuePlayer(player);
            player.sendMessage(Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §aYou've been added to the queue for " + rollercoasterName + "."), false);
        } else {
            player.sendMessage(Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §cRollercoaster '" + rollercoasterName + "' not found."), false);
        }
    }

    // Check the queue and spawn minecarts for players
    public static void checkAndSpawnMinecart(ServerWorld world) {
        long currentTime = System.currentTimeMillis();
        for (Rollercoaster rollercoaster : rollercoasters.values()) {
            if (currentTime - rollercoaster.getLastSpawnTime() >= SPAWN_DELAY) {
                UUID playerId = rollercoaster.getPlayerQueue().poll();
                ServerPlayerEntity player = playerId != null ? world.getServer().getPlayerManager().getPlayer(playerId) : null;

                if (player != null) {
                    BlockPos spawnPos = rollercoaster.getSpawnPos();
                    MinecartEntity minecart = new MinecartEntity(world, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                    world.spawnEntity(minecart);
                    player.startRiding(minecart);
                    player.sendMessage(Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §aEnjoy your ride on " + rollercoaster.getName() + "!"), false);
                }

                rollercoaster.setLastSpawnTime(currentTime);
                rollercoaster.updateQueueInfo(world.getServer().getPlayerManager());
            }
        }
    }

    // Load rollercoasters from the config
    public static void loadRollercoasters() {
        rollercoasters.clear();
        rollercoasters.putAll(ConfigManager.getConfig().rollercoasters);
        for (Rollercoaster coaster : rollercoasters.values()) {
            coaster.initTransientFields(); // Reinitialize transient fields
        }
    }

    // Save rollercoasters to the config
    public static void saveRollercoasters() {
        ConfigManager.getConfig().rollercoasters = rollercoasters;
        ConfigManager.save();
    }
}