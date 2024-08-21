package app.qwertz.qwertzcore.mixin.event.interaction;

import app.qwertz.qwertzcore.event.interaction.player.UseEntityCallback;

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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(targets = "net/minecraft/server/network/ServerPlayNetworkHandler$1")
public abstract class ServerPlayNetworkHandlerMixin implements PlayerInteractEntityC2SPacket.Handler {
    @Shadow
    @Final
    ServerPlayNetworkHandler field_28963;

    @Shadow
    @Final
    Entity field_28962;

    @Inject(method = "interactAt(Lnet/minecraft/util/Hand;Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onPlayerInteractEntity(Hand hand, Vec3d hitPosition, CallbackInfo info) {
        PlayerEntity player = field_28963.player;
        World world = player.getEntityWorld();

        EntityHitResult hitResult = new EntityHitResult(field_28962, hitPosition.add(field_28962.getX(), field_28962.getY(), field_28962.getZ()));
        ActionResult result = UseEntityCallback.EVENT.invoker().interact(player, world, hand, field_28962, hitResult);

        if (result != ActionResult.PASS) {
            info.cancel();
        }
    }

    @Inject(method = "interact(Lnet/minecraft/util/Hand;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onPlayerInteractEntity(Hand hand, CallbackInfo info) {
        PlayerEntity player = field_28963.player;
        World world = player.getEntityWorld();

        ActionResult result = UseEntityCallback.EVENT.invoker().interact(player, world, hand, field_28962, null);

        if (result != ActionResult.PASS) {
            info.cancel();
        }
    }
}