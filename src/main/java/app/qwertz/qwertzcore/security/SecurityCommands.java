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

package app.qwertz.qwertzcore.security;

import app.qwertz.qwertzcore.ConfigManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class SecurityCommands {

    public static int setOnJoinSpawn(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        Vec3d playerPos = source.getPosition(); // Get the current player's position

        // Set the spawn location to the player's current position
        ConfigManager.setOnJoinSpawn(playerPos.x, playerPos.y, playerPos.z, source.getPlayer().getYaw(), source.getPlayer().getPitch());

        source.sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §aOn join spawn location set to your current position: " + playerPos), true);
        return Command.SINGLE_SUCCESS;
    }
    public static int toggleOnJoinSpawn(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        Vec3d playerPos = source.getPosition(); // Get the current player's position

        // Set the spawn location to the player's current position
        ConfigManager.toggleOnJoinTp();
        if (ConfigManager.getConfig().onJoinTp) {
        source.sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §aOn join tp is now enabled!"), true);
        } else {
            source.sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §cOn join tp is now disabled!"), true);
        }
        return Command.SINGLE_SUCCESS;
    }
}