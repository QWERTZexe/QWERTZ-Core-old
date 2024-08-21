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

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class NPCUtils {
    public static final Map<String, EntityType<? extends LivingEntity>> ALLOWED_ENTITY_TYPES = new HashMap<>();

    static {
        ALLOWED_ENTITY_TYPES.put("zombie", EntityType.ZOMBIE);
        ALLOWED_ENTITY_TYPES.put("creeper", EntityType.CREEPER);
        ALLOWED_ENTITY_TYPES.put("pig", EntityType.PIG);
        ALLOWED_ENTITY_TYPES.put("cow", EntityType.COW);
        ALLOWED_ENTITY_TYPES.put("sheep", EntityType.SHEEP);
        ALLOWED_ENTITY_TYPES.put("chicken", EntityType.CHICKEN);
        ALLOWED_ENTITY_TYPES.put("blaze", EntityType.BLAZE);
        ALLOWED_ENTITY_TYPES.put("allay", EntityType.ALLAY);
        ALLOWED_ENTITY_TYPES.put("armadillo", EntityType.ARMADILLO);
        ALLOWED_ENTITY_TYPES.put("armor_stand", EntityType.ARMOR_STAND);
        ALLOWED_ENTITY_TYPES.put("axolotl", EntityType.AXOLOTL);
        ALLOWED_ENTITY_TYPES.put("bat", EntityType.BAT);
        ALLOWED_ENTITY_TYPES.put("bee", EntityType.BEE);
        ALLOWED_ENTITY_TYPES.put("bogged", EntityType.BOGGED);
        ALLOWED_ENTITY_TYPES.put("breeze", EntityType.BREEZE);
        ALLOWED_ENTITY_TYPES.put("camel", EntityType.CAMEL);
        ALLOWED_ENTITY_TYPES.put("cat", EntityType.CAT);
        ALLOWED_ENTITY_TYPES.put("cave_spider", EntityType.CAVE_SPIDER);
        ALLOWED_ENTITY_TYPES.put("cod", EntityType.COD);
        ALLOWED_ENTITY_TYPES.put("dolphin", EntityType.DOLPHIN);
        ALLOWED_ENTITY_TYPES.put("donkey", EntityType.DONKEY);
        ALLOWED_ENTITY_TYPES.put("drowned", EntityType.DROWNED);
        ALLOWED_ENTITY_TYPES.put("elder_guardian", EntityType.ELDER_GUARDIAN);
        ALLOWED_ENTITY_TYPES.put("ender_dragon", EntityType.ENDER_DRAGON);
        ALLOWED_ENTITY_TYPES.put("enderman", EntityType.ENDERMAN);
        ALLOWED_ENTITY_TYPES.put("endermite", EntityType.ENDERMITE);
        ALLOWED_ENTITY_TYPES.put("evoker", EntityType.EVOKER);
        ALLOWED_ENTITY_TYPES.put("frog", EntityType.FROG);
        ALLOWED_ENTITY_TYPES.put("fox", EntityType.FOX);
        ALLOWED_ENTITY_TYPES.put("ghast", EntityType.GHAST);
        ALLOWED_ENTITY_TYPES.put("giant", EntityType.GIANT);
        ALLOWED_ENTITY_TYPES.put("glow_squid", EntityType.GLOW_SQUID);
        ALLOWED_ENTITY_TYPES.put("goat", EntityType.GOAT);
        ALLOWED_ENTITY_TYPES.put("guardian", EntityType.GUARDIAN);
        ALLOWED_ENTITY_TYPES.put("hoglin", EntityType.HOGLIN);
        ALLOWED_ENTITY_TYPES.put("horse", EntityType.HORSE);
        ALLOWED_ENTITY_TYPES.put("husk", EntityType.HUSK);
        ALLOWED_ENTITY_TYPES.put("illusioner", EntityType.ILLUSIONER);
        ALLOWED_ENTITY_TYPES.put("iron_golem", EntityType.IRON_GOLEM);
        ALLOWED_ENTITY_TYPES.put("llama", EntityType.LLAMA);
        ALLOWED_ENTITY_TYPES.put("magma_cube", EntityType.MAGMA_CUBE);
        ALLOWED_ENTITY_TYPES.put("mooshroom", EntityType.MOOSHROOM);
        ALLOWED_ENTITY_TYPES.put("mule", EntityType.MULE);
        ALLOWED_ENTITY_TYPES.put("ocelot", EntityType.OCELOT);
        ALLOWED_ENTITY_TYPES.put("panda", EntityType.PANDA);
        ALLOWED_ENTITY_TYPES.put("parrot", EntityType.PARROT);
        ALLOWED_ENTITY_TYPES.put("phantom", EntityType.PHANTOM);
        ALLOWED_ENTITY_TYPES.put("piglin", EntityType.PIGLIN);
        ALLOWED_ENTITY_TYPES.put("piglin_brute", EntityType.PIGLIN_BRUTE);
        ALLOWED_ENTITY_TYPES.put("pillager", EntityType.PILLAGER);
        ALLOWED_ENTITY_TYPES.put("player", EntityType.PLAYER);
        ALLOWED_ENTITY_TYPES.put("polar_bear", EntityType.POLAR_BEAR);
        ALLOWED_ENTITY_TYPES.put("pufferfish", EntityType.PUFFERFISH);
        ALLOWED_ENTITY_TYPES.put("rabbit", EntityType.RABBIT);
        ALLOWED_ENTITY_TYPES.put("ravager", EntityType.RAVAGER);
        ALLOWED_ENTITY_TYPES.put("salmon", EntityType.SALMON);
        ALLOWED_ENTITY_TYPES.put("shulker", EntityType.SHULKER);
        ALLOWED_ENTITY_TYPES.put("silverfish", EntityType.SILVERFISH);
        ALLOWED_ENTITY_TYPES.put("skeleton", EntityType.SKELETON);
        ALLOWED_ENTITY_TYPES.put("skeleton_horse", EntityType.SKELETON_HORSE);
        ALLOWED_ENTITY_TYPES.put("slime", EntityType.SLIME);
        ALLOWED_ENTITY_TYPES.put("sniffer", EntityType.SNIFFER);
        ALLOWED_ENTITY_TYPES.put("snow_golem", EntityType.SNOW_GOLEM);
        ALLOWED_ENTITY_TYPES.put("spider", EntityType.SPIDER);
        ALLOWED_ENTITY_TYPES.put("squid", EntityType.SQUID);
        ALLOWED_ENTITY_TYPES.put("stray", EntityType.STRAY);
        ALLOWED_ENTITY_TYPES.put("strider", EntityType.STRIDER);
        ALLOWED_ENTITY_TYPES.put("tadpole", EntityType.TADPOLE);
        ALLOWED_ENTITY_TYPES.put("trader_llama", EntityType.TRADER_LLAMA);
        ALLOWED_ENTITY_TYPES.put("tropical_fish", EntityType.TROPICAL_FISH);
        ALLOWED_ENTITY_TYPES.put("turtle", EntityType.TURTLE);
        ALLOWED_ENTITY_TYPES.put("vex", EntityType.VEX);
        ALLOWED_ENTITY_TYPES.put("villager", EntityType.VILLAGER);
        ALLOWED_ENTITY_TYPES.put("vindicator", EntityType.VINDICATOR);
        ALLOWED_ENTITY_TYPES.put("wandering_trader", EntityType.WANDERING_TRADER);
        ALLOWED_ENTITY_TYPES.put("warden", EntityType.WARDEN);
        ALLOWED_ENTITY_TYPES.put("witch", EntityType.WITCH);
        ALLOWED_ENTITY_TYPES.put("wither", EntityType.WITHER);
        ALLOWED_ENTITY_TYPES.put("wither_skeleton", EntityType.WITHER_SKELETON);
        ALLOWED_ENTITY_TYPES.put("wolf", EntityType.WOLF);
        ALLOWED_ENTITY_TYPES.put("zoglin", EntityType.ZOGLIN);
        ALLOWED_ENTITY_TYPES.put("zombie_horse", EntityType.ZOMBIE_HORSE);
        ALLOWED_ENTITY_TYPES.put("zombie_villager", EntityType.ZOMBIE_VILLAGER);
        ALLOWED_ENTITY_TYPES.put("zombified_piglin", EntityType.ZOMBIFIED_PIGLIN);
        // TODO: Make entities like EntityType.TNT, EntityType.Minecart and so on work too!
    }

    public static EntityType<? extends LivingEntity> getEntityTypeFromString(String type) {
        return ALLOWED_ENTITY_TYPES.get(type.toLowerCase());
    }
}