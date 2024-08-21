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

package app.qwertz.qwertzcore.npc;

import app.qwertz.qwertzcore.ConfigManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NPCEntity extends LivingEntity {
    private GameProfile gameProfile;
    private String npcId;
    private Map<UUID, Long> lastInteractionTimes = new HashMap<>();
    private static final long INTERACTION_COOLDOWN = 500; // 500 milliseconds cooldown
    private boolean isFloating;

    public NPCEntity(EntityType<? extends LivingEntity> entityType, ServerWorld world, Vec3d position, float yaw, float pitch, String name, GameProfile gameProfile, boolean isFloating) {
        super(entityType, world);
        this.gameProfile = gameProfile;
        this.refreshPositionAndAngles(position.x, position.y, position.z, yaw, pitch);
        this.setCustomName(Text.literal(name));
        this.setCustomNameVisible(true);
        this.setInvulnerable(true);
        this.isFloating = isFloating;
    }
    public boolean isFloating() {
        return isFloating;
    }
    public void setFloating(boolean floating) {
        isFloating = floating;
    }

    public static DefaultAttributeContainer.Builder createNPCAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.MOVEMENT_SPEED, 0.0)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    public void setNpcId(String npcId) {
        this.npcId = npcId;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.getWorld().isClient && source.getAttacker() instanceof ServerPlayerEntity) {
            // Trigger left-click interaction
            handleInteraction((ServerPlayerEntity) source.getAttacker());
        }
        // Make the NPC immune to all damage
        return false;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient && player instanceof ServerPlayerEntity) {
            handleInteraction((ServerPlayerEntity) player);
        }
        return ActionResult.SUCCESS;
    }

    private void handleInteraction(ServerPlayerEntity player) {
        UUID playerUUID = player.getUuid();
        long currentTime = System.currentTimeMillis();
        long lastInteractionTime = lastInteractionTimes.getOrDefault(playerUUID, 0L);

        if (currentTime - lastInteractionTime < INTERACTION_COOLDOWN) {
            return; // Ignore interaction if it's too soon after the last one for this player
        }
        lastInteractionTimes.put(playerUUID, currentTime);

        if (npcId != null) {
            CustomNPC customNPC = ConfigManager.loadNPC(npcId);
            if (customNPC != null) {
                customNPC.executeFunction(player);
            } else {
                player.sendMessage(Text.literal("Error: NPC data not found."), false);
            }
        }
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return List.of(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        // Do nothing to prevent being pushed
    }

    @Override
    public void pushAway(Entity entity) {
        // Do nothing to prevent pushing others
    }

    @Override
    public void tick() {
        super.tick();

        if (isFloating) {
            // Make the NPC float in place like a Blaze
            this.setVelocity(new Vec3d(0, Math.sin(this.age * 0.1) * 0.02, 0));
        } else {
            // Reset velocity to prevent any movement
            this.setVelocity(Vec3d.ZERO);
        }

        // Add any other custom behavior for your NPC here
    }

    @Override
    public boolean isCollidable() {
        return false; // This makes the entity act as a solid block
    }

    public boolean collides() {
        return false; // This ensures the entity is considered for collisions
    }


}