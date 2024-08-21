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

import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class Rollercoaster implements Serializable {
    private String name;
    private BlockPos spawnPos;
    private transient Queue<UUID> playerQueue;
    private transient long lastSpawnTime;
    private static final long SPAWN_DELAY = 10000; // 10 seconds in milliseconds

    public Rollercoaster(String name, BlockPos spawnPos) {
        this.name = name;
        this.spawnPos = spawnPos;
        this.playerQueue = new LinkedList<>();
        this.lastSpawnTime = System.currentTimeMillis() - SPAWN_DELAY; // Initialize to allow immediate first spawn
    }

    public String getName() {
        return name;
    }

    public BlockPos getSpawnPos() {
        return spawnPos;
    }

    public Queue<UUID> getPlayerQueue() {
        return playerQueue;
    }

    public long getLastSpawnTime() {
        return lastSpawnTime;
    }

    public void setLastSpawnTime(long time) {
        this.lastSpawnTime = time;
    }

    public void queuePlayer(ServerPlayerEntity player) {
        playerQueue.offer(player.getUuid());
        updateQueueInfo(player.getServer().getPlayerManager());
    }

    public void updateQueueInfo(PlayerManager playerManager) {
        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < playerQueue.size(); i++) {
            UUID uuid = (UUID) playerQueue.toArray()[i]; // Get the UUID of the player in the queue
            ServerPlayerEntity player = playerManager.getPlayer(uuid);
            if (player != null) {
                int position = i + 1; // Player's position in the queue
                int timeUntilYourRide = (position - 1) * 10 + 5; // Calculate time until their ride
                // Send title and subtitle using packets
                player.networkHandler.sendPacket(new TitleS2CPacket(Text.of("§6Queue Position: " + position)));
                player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.of("§cTime until your ride: " + timeUntilYourRide + " seconds")));
            }
        }
    }

    public void initTransientFields() {
        if (playerQueue == null) {
            playerQueue = new LinkedList<>();
        }
        lastSpawnTime = System.currentTimeMillis() - SPAWN_DELAY; // Initialize to allow immediate first spawn
    }
}