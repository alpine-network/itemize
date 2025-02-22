package co.crystaldev.itemize.api.type.item;

import co.crystaldev.alpinecore.util.ItemHelper;
import org.bukkit.entity.Damageable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.2.2
 */
final class ItemSimilarity {
    static boolean isSimilar(@NotNull ItemStack item, @Nullable ItemStack similarItem) {
        // Pretty hacky but we need to ignore durability

        if (similarItem == null) {
            return false;
        }

        if (item.getType() == similarItem.getType() && (item.getDurability() != similarItem.getDurability() || item.getItemMeta() instanceof Damageable)) {
            // todo: do we need to compare more than just the name and lore
            // in what scenario will this not be enough for most custom items?
            // this is also the final iteration of about 2 hours of slamming
            // my head and deffo not the best method. better method is probably
            // just using reflection to access internal comparison tools
            // or set temporarily durability to the same value for both items
            return ItemHelper.getDisplayName(item).equals(ItemHelper.getDisplayName(similarItem));
        }
        else {
            return item.isSimilar(similarItem);
        }
    }
}
