package app.qwertz.qwertzcore.impl.event.interaction;

import app.qwertz.qwertzcore.impl.networking.UntrackedNetworkHandler;

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

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public final class FakePlayerNetworkHandler extends ServerPlayNetworkHandler implements UntrackedNetworkHandler {
    private static final ClientConnection FAKE_CONNECTION = new FakeClientConnection();

    public FakePlayerNetworkHandler(ServerPlayerEntity player) {
        super(player.getServer(), FAKE_CONNECTION, player, ConnectedClientData.createDefault(player.getGameProfile(), false));
    }

    @Override
    public void send(Packet<?> packet, @Nullable PacketCallbacks callbacks) { }

    private static final class FakeClientConnection extends ClientConnection {
        private FakeClientConnection() {
            super(NetworkSide.CLIENTBOUND);
        }
    }
}