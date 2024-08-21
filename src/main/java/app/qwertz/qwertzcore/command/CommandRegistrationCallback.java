package app.qwertz.qwertzcore.command;

import app.qwertz.qwertzcore.event.Event;
import app.qwertz.qwertzcore.event.EventFactory;

/*
   QWERTZ Core side note: The following file was fetched from the Fabric API Repository on GitHub (21/08/2024)

   The File itself belongs to FabricMC and their respective License at that time (See below)
   The File itself was NOT MODIFIED! The Only thing which was edited are the imports and the package name.
   (Everything above this notice)
*/

/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

/**
 * Callback for when a server registers all commands.
 *
 * <p>To register some commands, you would register an event listener and implement the callback.
 *
 * <pre>{@code
 * CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
 *     // For example, this command is only registered on an integrated server like the vanilla publish command
 *     if (environment.integrated) dispatcher.register(CommandManager.literal("integrated_command").executes(context -> {...}));
 * })};
 * }</pre>
 */
public interface CommandRegistrationCallback {
    Event<CommandRegistrationCallback> EVENT = EventFactory.createArrayBacked(CommandRegistrationCallback.class, (callbacks) -> (dispatcher, registryAccess, environment) -> {
        for (CommandRegistrationCallback callback : callbacks) {
            callback.register(dispatcher, registryAccess, environment);
        }
    });

    /**
     * Called when the server is registering commands.
     *
     * @param dispatcher the command dispatcher to register commands to
     * @param registryAccess object exposing access to the game's registries
     * @param environment environment the registrations should be done for, used for commands that are dedicated or integrated server only
     */
    void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment);
}