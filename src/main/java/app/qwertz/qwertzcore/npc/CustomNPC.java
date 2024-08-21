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

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;


public class CustomNPC {
    private String id;
    private String name;
    private Vec3d position;
    private float yaw;
    private float pitch;
    private GameProfile gameProfile;
    private EntityType<? extends LivingEntity> entityType;
    private UUID entityUuid; // This will be initialized upon creation
    private String function;
    private FunctionType functionType;
    private boolean isFloating;
    public enum FunctionType {
        TEXT,
        COMMAND,
        OP_COMMAND
    }
    // Constructor
    public CustomNPC(String id, String name, Vec3d position, float yaw, float pitch, GameProfile gameProfile, EntityType<? extends LivingEntity> entityType, boolean isFloating) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
        this.gameProfile = gameProfile;
        this.entityType = entityType;
        this.entityUuid = UUID.randomUUID(); // Generate a new UUID upon creation
        this.functionType = FunctionType.TEXT;
        this.function = "Hello! I am an NPC made by QWERTZcore!";
        this.isFloating = isFloating;
        System.out.println("CustomNPC created: " + name + ", UUID: " + entityUuid);
    }
    public boolean isFloating() {
        return isFloating;
    }

    public void setFloating(boolean floating) {
        isFloating = floating;
    }
    public void spawn(ServerWorld world) {
        if (this.entityUuid == null) {
            this.entityUuid = UUID.randomUUID();
        }
        NPCEntity npcEntity = new NPCEntity(entityType, world, position, yaw, pitch, name, gameProfile, isFloating);
        npcEntity.setUuid(this.entityUuid);
        npcEntity.setNpcId(this.id);
        world.spawnEntity(npcEntity);
        System.out.println("NPC spawned with UUID: " + this.entityUuid);
    }
    public void remove(ServerWorld world) {
        if (entityUuid != null) {
            NPCEntity npcEntity = (NPCEntity) world.getEntity(entityUuid);
            if (npcEntity != null) {
                npcEntity.remove(NPCEntity.RemovalReason.DISCARDED);
            }
        }
    }

    public void updatePosition(ServerWorld world, Vec3d newPosition, float newYaw, float newPitch) {
        System.out.println("Updating position for NPC: " + name + ", Current UUID: " + this.entityUuid);
        this.position = newPosition;
        this.yaw = newYaw;
        this.pitch = newPitch;

        Entity entity = findExistingEntity(world);
        if (entity instanceof NPCEntity) {
            NPCEntity npcEntity = (NPCEntity) entity;
            npcEntity.refreshPositionAndAngles(newPosition.x, newPosition.y, newPosition.z, newYaw, newPitch);
            System.out.println("NPC position updated: " + name + " to " + newPosition);
        } else {
            System.out.println("NPC entity not found in world, respawning: " + name + " (UUID: " + entityUuid + ")");
            removeExistingEntity(world);
            spawn(world);
        }
    }

    public void setFunction(String function, FunctionType functionType) {
        this.function = function;
        this.functionType = functionType;
    }

    private Entity findExistingEntity(ServerWorld world) {
        if (entityUuid != null) {
            Entity entity = world.getEntity(entityUuid);
            if (entity != null) {
                return entity;
            }
        }
        return null;
    }

    private void removeExistingEntity(ServerWorld world) {
        if (entityUuid != null) {
            Entity existingEntity = world.getEntity(entityUuid);
            if (existingEntity != null) {
                existingEntity.remove(Entity.RemovalReason.DISCARDED);
                System.out.println("Removed existing entity with UUID: " + entityUuid);
            }
        }
    }

    public void executeFunction(ServerPlayerEntity player) {
        if (function == null || function.isEmpty()) {
            return;
        }

        switch (functionType) {
            case TEXT:
                player.sendMessage(Text.literal(function), false);
                break;
            case COMMAND:
                player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), function);
                break;
            case OP_COMMAND:
                ServerCommandSource opSource = player.getCommandSource()
                        .withLevel(4) // OP level 4
                        .withEntity(player);
                player.getServer().getCommandManager().executeWithPrefix(opSource, function);
                break;
        }
    }
    public void setName(String newName, ServerWorld world) {
        this.name = newName;
        // Update the entity's custom name if it exists
        if (entityUuid != null) {
            Entity entity = world.getEntity(entityUuid);
            if (entity instanceof NPCEntity) {
                entity.setCustomName(Text.literal(newName));
            }
        }
    }


    // Getters

    public String getId() {
        return id;
    }
    public UUID getEntityUuid() {
        return entityUuid;
    }
    public String getName() { return name; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public Vec3d getPosition() { return position; }
    public GameProfile getGameProfile() { return gameProfile; }
    public EntityType<? extends LivingEntity> getEntityType() { return entityType; }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("posX", position.x);
        json.addProperty("posY", position.y);
        json.addProperty("posZ", position.z);
        json.addProperty("yaw", yaw);
        json.addProperty("pitch", pitch);
        json.addProperty("entityType", EntityType.getId(entityType).toString());
        json.addProperty("gameProfileUuid", gameProfile.getId().toString());
        json.addProperty("entityUuid", entityUuid.toString());
        json.addProperty("function", function);
        json.addProperty("functionType", functionType.name());
        json.addProperty("isFloating", isFloating);
        return json;
    }

    public static CustomNPC fromJson(String id, JsonObject json) {
        String name = json.get("name").getAsString();
        Vec3d position = new Vec3d(
                json.get("posX").getAsDouble(),
                json.get("posY").getAsDouble(),
                json.get("posZ").getAsDouble()
        );
        float yaw = json.get("yaw").getAsFloat();
        float pitch = json.get("pitch").getAsFloat();
        EntityType<?> entityType = EntityType.get(json.get("entityType").getAsString()).orElse(EntityType.PIG);
        UUID gameProfileUuid = UUID.fromString(json.get("gameProfileUuid").getAsString());
        UUID entityUuid = UUID.fromString(json.get("entityUuid").getAsString());
        String function = json.get("function").getAsString();
        GameProfile gameProfile = new GameProfile(gameProfileUuid, name);
        boolean isFloating = json.has("isFloating") && json.get("isFloating").getAsBoolean();
        CustomNPC npc = new CustomNPC(id, name, position, yaw, pitch, gameProfile, (EntityType<? extends LivingEntity>) entityType,isFloating);
        npc.entityUuid = entityUuid;
        npc.function = function;
        npc.functionType = FunctionType.valueOf(json.get("functionType").getAsString());

        return npc;
    }
}