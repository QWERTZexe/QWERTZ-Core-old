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

package app.qwertz.qwertzcore.mixin;

import app.qwertz.qwertzcore.ConfigManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerJoinMixin {

    @Inject(method = "onSpawn", at = @At("HEAD"))
    private void onSpawn(CallbackInfo ci) {
        if (ConfigManager.getConfig().onJoinTp) {
            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
            teleportPlayerToSpawn(player);
    }}

    private void teleportPlayerToSpawn(ServerPlayerEntity player) {
        Vec3d spawnLocation = new Vec3d(ConfigManager.getConfig().spawnX,ConfigManager.getConfig().spawnY,ConfigManager.getConfig().spawnZ); // Get the spawn location

        // Teleport the player to the spawn location
        player.teleport(player.getServerWorld(), spawnLocation.x, spawnLocation.y, spawnLocation.z, ConfigManager.getConfig().spawnYaw, ConfigManager.getConfig().spawnPitch, true);
    }
}