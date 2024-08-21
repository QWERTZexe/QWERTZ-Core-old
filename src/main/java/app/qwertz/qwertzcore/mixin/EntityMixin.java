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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "hasPassenger", at = @At("RETURN"), cancellable = true)
    private void onHasPassenger(Entity passenger, CallbackInfoReturnable<Boolean> cir) {
        if (ConfigManager.getConfig().disableMinecartDismounting &&
                passenger instanceof PlayerEntity &&
                (Object)this instanceof MinecartEntity) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "hasPlayerRider", at = @At("RETURN"), cancellable = true)
    private void onHasPlayerRider(CallbackInfoReturnable<Boolean> cir) {
        if (ConfigManager.getConfig().disableMinecartDismounting &&
                (Object)this instanceof MinecartEntity &&
                ((Entity)(Object)this).getFirstPassenger() instanceof PlayerEntity) {
            cir.setReturnValue(true);
        }
    }
}