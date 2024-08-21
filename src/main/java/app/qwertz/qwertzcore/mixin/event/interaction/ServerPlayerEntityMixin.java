package app.qwertz.qwertzcore.mixin.event.interaction;

import app.qwertz.qwertzcore.event.interaction.player.AttackEntityCallback;

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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    public void onPlayerInteractEntity(Entity target, CallbackInfo info) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        ActionResult result = AttackEntityCallback.EVENT.invoker().interact(player, player.getEntityWorld(), Hand.MAIN_HAND, target, null);

        if (result != ActionResult.PASS) {
            info.cancel();
        }
    }
}