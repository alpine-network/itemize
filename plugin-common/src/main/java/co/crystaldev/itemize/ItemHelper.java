package co.crystaldev.itemize;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 09/06/2024
 */
final class ItemHelper {
    public static boolean isItem(@NotNull Material material) {
        if (XMaterial.getVersion() >= 12) {
            return material.isItem();
        }

        switch (material) {
            case WATER:
            case STATIONARY_WATER:
            case LAVA:
            case STATIONARY_LAVA:
            case BED_BLOCK:
            case PISTON_EXTENSION:
            case PISTON_MOVING_PIECE:
            case DOUBLE_STEP:
            case FIRE:
            case REDSTONE_WIRE:
            case CROPS:
            case BURNING_FURNACE:
            case SIGN_POST:
            case WOODEN_DOOR:
            case WALL_SIGN:
            case IRON_DOOR_BLOCK:
            case GLOWING_REDSTONE_ORE:
            case REDSTONE_TORCH_OFF:
            case SUGAR_CANE_BLOCK:
            case PORTAL:
            case CAKE_BLOCK:
            case DIODE_BLOCK_OFF:
            case DIODE_BLOCK_ON:
            case PUMPKIN_STEM:
            case MELON_STEM:
            case NETHER_WARTS:
            case BREWING_STAND:
            case CAULDRON:
            case ENDER_PORTAL:
            case REDSTONE_LAMP_ON:
            case WOOD_DOUBLE_STEP:
            case COCOA:
            case TRIPWIRE:
            case FLOWER_POT:
            case CARROT:
            case POTATO:
            case SKULL:
            case REDSTONE_COMPARATOR_OFF:
            case REDSTONE_COMPARATOR_ON:
            case STANDING_BANNER:
            case WALL_BANNER:
            case DAYLIGHT_DETECTOR_INVERTED:
            case DOUBLE_STONE_SLAB2:
            case SPRUCE_DOOR:
            case BIRCH_DOOR:
            case JUNGLE_DOOR:
            case ACACIA_DOOR:
            case DARK_OAK_DOOR:
                return false;
            default:
                return true;
        }
    }
}
