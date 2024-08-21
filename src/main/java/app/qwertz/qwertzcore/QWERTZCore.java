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

import app.qwertz.qwertzcore.command.CommandRegistrationCallback;
import app.qwertz.qwertzcore.event.interaction.player.AttackBlockCallback;
import app.qwertz.qwertzcore.event.interaction.player.UseBlockCallback;
import app.qwertz.qwertzcore.npc.CustomNPC;
import app.qwertz.qwertzcore.npc.NPCUtils;
import app.qwertz.qwertzcore.security.SecurityCommands;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QWERTZCore implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("QWERTZ Core is initializing!");
        ConfigManager.load(); // Load the config
        RollercoasterManager.loadRollercoasters();
        disableVanillaCommands();
        hideVanillaCommands();
        registerCommands();
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!ConfigManager.getConfig().allowBlockBreak) {
                displayActionBlockedMessage(player, "break blocks");
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        // Register event for block placing
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!ConfigManager.getConfig().allowBlockPlace) {
                if (player.getStackInHand(hand).getItem() instanceof net.minecraft.item.BlockItem) {
                    displayActionBlockedMessage(player, "place blocks");
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });
    }


    private void disableVanillaCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            List<CommandNode<ServerCommandSource>> commandsToRemove = new ArrayList<>();
            List<CommandNode<ServerCommandSource>> commandsToAdd = new ArrayList<>();

            dispatcher.getRoot().getChildren().forEach(command -> {
                if (!ConfigManager.getConfig().commandsToKeep.contains(command.getName())) {
                    commandsToRemove.add(command);
                    commandsToAdd.add(createDisabledCommand(command.getName()));
                }
            });

            // rename pardon to unban
            CommandNode<ServerCommandSource> pardonCommand = dispatcher.getRoot().getChild("pardon");
            if (pardonCommand != null) {
                commandsToRemove.add(pardonCommand);
                commandsToAdd.add(CommandManager.literal("unban")
                        .requires(pardonCommand.getRequirement())
                        .redirect(pardonCommand)
                        .build());
            }

            commandsToRemove.forEach(cmd -> dispatcher.getRoot().getChildren().remove(cmd));

            commandsToAdd.forEach(cmd -> dispatcher.getRoot().addChild(cmd));
        });
    }

    private void hideVanillaCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            // Store commands to keep
            List<CommandNode<ServerCommandSource>> commandsToKeep = new ArrayList<>();

            // Find and store commands to keep
            dispatcher.getRoot().getChildren().forEach(command -> {
                if (ConfigManager.getConfig().commandsToKeep.contains(command.getName())) {
                    commandsToKeep.add(command);
                }
            });

            // Clear all commands
            dispatcher.getRoot().getChildren().clear();

            // Re-add kept commands
            commandsToKeep.forEach(command -> dispatcher.getRoot().addChild(command));
        });
    }

    private CommandNode<ServerCommandSource> createDisabledCommand(String name) {
        return CommandManager.literal(name)
                .executes(context -> {
                    context.getSource().sendError(Text.literal("This command is disabled!"));
                    return 0;
                })
                .build();
    }
    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("mods").executes(context -> {
                ServerCommandSource source = context.getSource();
                StringBuilder modList = new StringBuilder("Server-side mods:\n");

                // List all mods
                FabricLoader.getInstance().getAllMods().forEach(mod -> modList.append("- ")
                        .append(mod.getMetadata().getName())
                        .append(" (")
                        .append(mod.getMetadata().getVersion().getFriendlyString())
                        .append(")\n"));

                source.sendFeedback(() -> Text.literal(modList.toString()).formatted(Formatting.GREEN), false);
                return 1;
            }));
            dispatcher.register(CommandManager.literal("QWERTZcore")
                    .then(CommandManager.literal("rollercoaster")
                            .then(CommandManager.literal("mount")
                                    .then(CommandManager.argument("name", StringArgumentType.word())
                                            .suggests((context, builder) -> {
                                                RollercoasterManager.getRollercoasters().keySet().forEach(builder::suggest);
                                                return builder.buildFuture();
                                            })
                                            .executes(context -> {
                                                String name = StringArgumentType.getString(context, "name");
                                                RollercoasterManager.queuePlayer(name, context.getSource().getPlayerOrThrow());
                                                return 1;
                                            })))));
            dispatcher.register(CommandManager.literal("QWERTZcore")
                    .requires(source -> source.hasPermissionLevel(4))
                    .then(CommandManager.literal("minecart")
                            .requires(source -> source.hasPermissionLevel(4))
                            .then(CommandManager.literal("destroyOnLeave")
                                    .requires(source -> source.hasPermissionLevel(4))
                                    .executes(context -> {
                                        ConfigManager.toggleDestroyMinecartOnLeave();
                                        String status = ConfigManager.getConfig().destroyMinecartOnLeave ? "§aenabled" : "§cdisabled";
                                        context.getSource().sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §aDestroying minecarts when players leave is now " + status + "."), true);
                                        return 1;
                                    }))
                            .then(CommandManager.literal("allowDismounting")
                                    .requires(source -> source.hasPermissionLevel(4))
                                    .executes(context -> {
                                        ConfigManager.toggleMinecartDismounting();
                                        String status = ConfigManager.getConfig().disableMinecartDismounting ? "§cdisabled" : "§aenabled";
                                        context.getSource().sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §aMinecart dismounting is now " + status + "."), true);
                                        return 1;
                                    })
                                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                                            .executes(context -> {
                                                boolean allow = BoolArgumentType.getBool(context, "value");
                                                ConfigManager.getConfig().disableMinecartDismounting = !allow;
                                                ConfigManager.save();
                                                String status = ConfigManager.getConfig().disableMinecartDismounting ? "§cdisabled" : "§aenabled";
                                                context.getSource().sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §eMinecart dismounting is now " + status + "."), true);
                                                return 1;
                                            }))))
                    .then(CommandManager.literal("list")
                            .requires(source -> source.hasPermissionLevel(4))
                            .executes(context -> {
                                listAll(context.getSource());
                                return 1;
                            })
                            .then(CommandManager.literal("minecart")
                                    .executes(context -> {
                                        listMinecartSettings(context.getSource());
                                        return 1;
                                    }))
                            .then(CommandManager.literal("commands")
                                    .executes(context -> {
                                        listCommands(context.getSource());
                                        return 1;
                                    }))
                            .then(CommandManager.literal("security")
                                    .executes(context -> {
                                        listSecurity(context.getSource());
                                        return 1;
                                    })))
                    .then(CommandManager.literal("commands")
                            .requires(source -> source.hasPermissionLevel(4))
                            .then(CommandManager.literal("enableCommand")
                                    .requires(source -> source.hasPermissionLevel(4))
                                    .then(CommandManager.argument("command", StringArgumentType.word())
                                            .executes(context -> {
                                                String command = StringArgumentType.getString(context, "command");
                                                ConfigManager.addCommand(command);
                                                context.getSource().sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §aEnabled command: " + command+ "\nRestart the server for these changes to take effect."), true);
                                                return 1;
                                            })))
                            .then(CommandManager.literal("disableCommand")
                                    .requires(source -> source.hasPermissionLevel(4))
                                    .then(CommandManager.argument("command", StringArgumentType.word())
                                            .executes(context -> {
                                                String command = StringArgumentType.getString(context, "command");
                                                ConfigManager.removeCommand(command);
                                                context.getSource().sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §cDisabled command: " + command + "\nRestart the server for these changes to take effect."), true);
                                                return 1;
                                            }))))
                            .then(CommandManager.literal("rollercoaster")
                                    .then(CommandManager.literal("create")
                                            .requires(source -> source.hasPermissionLevel(4))
                                            .then(CommandManager.argument("name", StringArgumentType.word())
                                                    .then(CommandManager.argument("pos", Vec3ArgumentType.vec3())
                                                            .executes(context -> {
                                                                String name = StringArgumentType.getString(context, "name");
                                                                Vec3d pos = Vec3ArgumentType.getVec3(context, "pos");
                                                                BlockPos blockPos = new BlockPos((int)pos.x, (int)pos.y, (int)pos.z);
                                                                RollercoasterManager.createRollercoaster(name, blockPos);
                                                                context.getSource().sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §aCreated rollercoaster '" + name + "' at " + blockPos.toShortString()), true);
                                                                return 1;
                                                            }))))
                                    .then(CommandManager.literal("delete")
                                            .requires(source -> source.hasPermissionLevel(4))
                                            .then(CommandManager.argument("name", StringArgumentType.word())
                                                    .suggests((context, builder) -> {
                                                        RollercoasterManager.getRollercoasters().keySet().forEach(builder::suggest);
                                                        return builder.buildFuture();
                                                    })
                                                    .executes(context -> {
                                                        String name = StringArgumentType.getString(context, "name");
                                                        RollercoasterManager.deleteRollercoaster(name);
                                                        context.getSource().sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §cDeleted rollercoaster '" + name + "'"), true);
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("list")
                                            .executes(context -> {
                                                context.getSource().sendFeedback(() -> Text.literal("§6§lRollercoasters:"), false);
                                                for (String name : RollercoasterManager.getRollercoasters().keySet()) {
                                                    context.getSource().sendFeedback(() -> Text.literal("§a- " + name), false);
                                                }
                                                return 1;
                                            })))
                    .then(CommandManager.literal("security")
                            .requires(source -> source.hasPermissionLevel(4))
                                            .then(CommandManager.literal("allowBlockBreak")
                                                    .requires(source -> source.hasPermissionLevel(4))
                                                    .then(CommandManager.argument("state", BoolArgumentType.bool())
                                                            .executes(context -> {
                                                                boolean state = BoolArgumentType.getBool(context, "state");
                                                                ConfigManager.setAllowBlockBreak(state);
                                                                context.getSource().sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §eBlock breaking is now " + (state ? "§aallowed." : "§cdisallowed.")), true);
                                                                return 1;
                                                            }))
                                                    .executes(context -> {
                                                        boolean currentState = ConfigManager.getConfig().allowBlockBreak;
                                                        ConfigManager.setAllowBlockBreak(!currentState);
                                                        context.getSource().sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §eBlock breaking is now " + (!currentState ? "§aallowed." : "§cdisallowed.")), true);
                                                        return 1;
                                                    }))
                                            .then(CommandManager.literal("allowBlockPlace")
                                                    .requires(source -> source.hasPermissionLevel(4))
                                                    .then(CommandManager.argument("state", BoolArgumentType.bool())
                                                            .executes(context -> {
                                                                boolean state = BoolArgumentType.getBool(context, "state");
                                                                ConfigManager.setAllowBlockPlace(state);
                                                                context.getSource().sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §eBlock placing is now " + (state ? "§aallowed." : "§cdisallowed.")), true);
                                                                return 1;
                                                            }))
                                                    .executes(context -> {
                                                        boolean currentState = ConfigManager.getConfig().allowBlockPlace;
                                                        ConfigManager.setAllowBlockPlace(!currentState);
                                                        context.getSource().sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §eBlock placing is now " + (!currentState ? "§aallowed." : "§cdisallowed.")), true);
                                                        return 1;
                                                    }))
                            .then(CommandManager.literal("allowPvp")
                                                    .requires(source -> source.hasPermissionLevel(4))
                                                    .then(CommandManager.argument("state", BoolArgumentType.bool())
                                                            .executes(context -> {
                                                                boolean state = BoolArgumentType.getBool(context, "state");
                                                                ConfigManager.setAllowPvp(state);
                                                                context.getSource().sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §ePvP is now " + (state ? "§aallowed." : "§cdisallowed.")), true);
                                                                return 1;
                                                            }))
                                                    .executes(context -> {
                                                        boolean currentState = ConfigManager.getConfig().allowPvp;
                                                        ConfigManager.setAllowPvp(!currentState);
                                                        context.getSource().sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §ePvP is now " + (!currentState ? "§aallowed." : "§cdisallowed.")), true);
                                                        return 1;

                                                    }))
                            .then(CommandManager.literal("setOnJoinTpSpawn")
                                    .executes(SecurityCommands::setOnJoinSpawn))
                            .then(CommandManager.literal("toggleOnJoinTp")
                                    .executes(SecurityCommands::toggleOnJoinSpawn)))
                    .then(CommandManager.literal("npc")
                            .requires(source -> source.hasPermissionLevel(4))
                            .then(CommandManager.literal("create")
                                    .requires(source -> source.hasPermissionLevel(4))
                                    .then(CommandManager.argument("id", StringArgumentType.string())
                                            .then(CommandManager.argument("type", StringArgumentType.word())
                                                    .suggests((context, builder) -> {
                                                        String input = builder.getRemaining().toLowerCase();
                                                        NPCUtils.ALLOWED_ENTITY_TYPES.keySet().stream()
                                                                .filter(type -> type.startsWith(input))
                                                                .forEach(builder::suggest);
                                                        return builder.buildFuture();
                                                    })
                                                    .then(CommandManager.argument("isFloating", BoolArgumentType.bool())
                                                            .executes(this::createNPC)))))
                            .then(CommandManager.literal("list")
                                    .requires(source -> source.hasPermissionLevel(4))
                                    .executes(this::listNPCs))
                            .then(CommandManager.literal("delete")
                                    .requires(source -> source.hasPermissionLevel(4))
                                    .then(CommandManager.argument("id", StringArgumentType.word())
                                            .suggests((context, builder) -> {
                                                ConfigManager.getConfig().npcs.keySet().forEach(builder::suggest);
                                                return builder.buildFuture();
                                            })
                                            .executes(this::deleteNPC)))
                            .then(CommandManager.literal("rename")
                                    .requires(source -> source.hasPermissionLevel(4))
                                    .then(CommandManager.argument("id", StringArgumentType.word())
                                            .suggests((context, builder) -> {
                                                ConfigManager.getConfig().npcs.keySet().forEach(builder::suggest);
                                                return builder.buildFuture();
                                            })
                                            .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                                    .executes(this::renameNPC))))
                            .then(CommandManager.literal("function")
                                    .requires(source -> source.hasPermissionLevel(4))
                                    .then(CommandManager.argument("id", StringArgumentType.word())
                                            .suggests((context, builder) -> {
                                                ConfigManager.getConfig().npcs.keySet().forEach(builder::suggest);
                                                return builder.buildFuture();
                                            })
                                            .then(CommandManager.argument("type", StringArgumentType.word())
                                                    .suggests((context, builder) -> {
                                                        builder.suggest("Text");
                                                        builder.suggest("Command");
                                                        builder.suggest("OPCommand");
                                                        return builder.buildFuture();
                                                    })
                                                    .then(CommandManager.argument("content", StringArgumentType.greedyString())
                                                            .executes(this::setNPCFunction)))))
                            .then(CommandManager.literal("move")
                                    .requires(source -> source.hasPermissionLevel(4))
                                    .then(CommandManager.argument("id", StringArgumentType.word())
                                            .suggests((context, builder) -> {
                                                ConfigManager.getConfig().npcs.keySet().forEach(builder::suggest);
                                                return builder.buildFuture();
                                            })
                                            .executes(this::moveNPC)))
                    ));
        });
    }

    private int createNPC(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String id = StringArgumentType.getString(context, "id");
        String type = StringArgumentType.getString(context, "type");
        boolean isFloating = BoolArgumentType.getBool(context, "isFloating");
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();
        Vec3d pos = source.getPosition();
        float yaw = source.getRotation().y;
        float pitch = source.getRotation().x;
        GameProfile profile = new GameProfile(UUID.randomUUID(), id);

        CustomNPC npc = new CustomNPC(id, id, pos, yaw, pitch, profile, NPCUtils.ALLOWED_ENTITY_TYPES.get(type), isFloating);
        npc.spawn(world);
        ConfigManager.saveNPC(npc);

        source.sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §aNPC " + id + "§a created as " + type + " at " + pos + (isFloating ? " (floating)" : "")), true);
        return 1;
    }
    private int renameNPC(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String npcId = StringArgumentType.getString(context, "id");
        String newName = StringArgumentType.getString(context, "name").replace("&","§");
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();

        CustomNPC npc = ConfigManager.loadNPC(npcId);
        if (npc != null) {
            npc.setName(newName, world);
            ConfigManager.saveNPC(npc);
            source.sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §eRenamed NPC with the id " + npcId + "§r§e to: " + newName), true);
        } else {
            source.sendError(Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §cNPC " + npcId + "§c not found."));
        }
        return 1;
    }

    private int setNPCFunction(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String id = StringArgumentType.getString(context, "id");
        String type = StringArgumentType.getString(context, "type");
        String content = StringArgumentType.getString(context, "content");
        ServerCommandSource source = context.getSource();

        CustomNPC npc = ConfigManager.loadNPC(id);
        if (npc != null) {
            CustomNPC.FunctionType functionType;
            switch (type.toLowerCase()) {
                case "text":
                    functionType = CustomNPC.FunctionType.TEXT;
                    content = content.replace("&","§");
                    break;
                case "command":
                    functionType = CustomNPC.FunctionType.COMMAND;
                    break;
                case "opcommand":
                    functionType = CustomNPC.FunctionType.OP_COMMAND;
                    break;
                default:
                    source.sendError(Text.literal("Invalid function type. Use Text, Command, or OPCommand."));
                    return 0;
            }

            npc.setFunction(content, functionType);
            ConfigManager.saveNPC(npc);
            String finalContent = content;
            source.sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §aSet " + type + " function for NPC " + id + "§r§a: " + finalContent), true);
        } else {
            source.sendError(Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §cNPC " + id + "§r§c not found."));
        }
        return 1;
    }
    private int deleteNPC(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String npcId = StringArgumentType.getString(context, "id");
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();
        CustomNPC npc = ConfigManager.loadNPC(npcId);
        if (npc != null) {
            npc.remove(world);
            ConfigManager.removeNPC(npcId);
            source.sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §cNPC " + npcId + "§r§c deleted."), true);
        } else {
            source.sendError(Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §cNPC " + npcId + "§r§c not found."));
        }
        return 1;
    }
    private int listNPCs(CommandContext<ServerCommandSource> context) {
        Map<String, JsonObject> npcs = ConfigManager.getConfig().npcs;
        if (npcs.isEmpty()) {
            context.getSource().sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §cNo NPCs found."), false);
        } else {
            context.getSource().sendFeedback(() -> Text.literal("§6§lNPCs:"), false);
            for (Map.Entry<String, JsonObject> entry : npcs.entrySet()) {
                CustomNPC npc = CustomNPC.fromJson(entry.getKey(), entry.getValue());
                context.getSource().sendFeedback(() -> Text.literal("§a- " + npc.getName() + "§r§a at " + npc.getPosition()), false);
            }
        }
        return 1;
    }
    private int moveNPC(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String npcId = StringArgumentType.getString(context, "id");
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();
        ServerPlayerEntity player = source.getPlayer();
        CustomNPC npc = ConfigManager.loadNPC(npcId);

        if (npc != null) {
            Vec3d newPos = player.getPos();
            float newYaw = player.getYaw();
            float newPitch = player.getPitch();
            System.out.println(npc.getEntityUuid());
            npc.updatePosition(world, newPos, newYaw, newPitch);
            ConfigManager.saveNPC(npc);
            source.sendFeedback(() -> Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §aNPC " + npcId + "§r§a moved to " + newPos), true);
        } else {
            source.sendError(Text.literal("§6[§aQWERTZ §4Core§6]§c:§r §cNPC " + npcId + "§r§c not found."));
        }
        return 1;
    }

    public static void displayActionBlockedMessage(PlayerEntity player, String action) {
        player.sendMessage(Text.literal("§cYou are not allowed to " + action + " here."), true);
    }
    private void listAll(ServerCommandSource source) {
        listMinecartSettings(source);
        listCommands(source);
        listSecurity(source);
    }

    private void listMinecartSettings(ServerCommandSource source) {
        source.sendFeedback(() -> Text.literal("§6§lMinecart Settings:"), false);
        source.sendFeedback(() -> Text.literal("§e- Destroy on leave: " + (ConfigManager.getConfig().destroyMinecartOnLeave ? "§aenabled" : "§cdisabled")), false);
        source.sendFeedback(() -> Text.literal("§e- Allow dismounting: " + (!ConfigManager.getConfig().disableMinecartDismounting ? "§aenabled" : "§cdisabled")), false);
    }
    private void listSecurity(ServerCommandSource source) {
        source.sendFeedback(() -> Text.literal("§6§lSecurity:"), false);
        source.sendFeedback(() -> Text.literal("§e- Allow Block Break: " + (ConfigManager.getConfig().allowBlockBreak ? "§aenabled" : "§cdisabled")), false);
        source.sendFeedback(() -> Text.literal("§e- Allow Block Place: " + (ConfigManager.getConfig().allowBlockPlace ? "§aenabled" : "§cdisabled")), false);
        source.sendFeedback(() -> Text.literal("§e- Allow PVP: " + (ConfigManager.getConfig().allowPvp ? "§aenabled" : "§cdisabled")), false);
    }
    private void listCommands(ServerCommandSource source) {
        source.sendFeedback(() -> Text.literal("§6§lEnabled Commands:"), false);
        String commands = String.join(", ", ConfigManager.getConfig().commandsToKeep);
        source.sendFeedback(() -> Text.literal("§e- " + commands), false);
    }
}