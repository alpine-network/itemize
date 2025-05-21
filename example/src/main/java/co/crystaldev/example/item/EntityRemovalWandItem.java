package co.crystaldev.example.item;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.example.config.ExampleConfig;
import co.crystaldev.itemize.api.Identifier;
import co.crystaldev.itemize.api.ItemizeItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class EntityRemovalWandItem implements ItemizeItem {

    public static final Identifier ID = Identifier.fromString("example:entity_removal_wand");

    private final ItemStack modelItem;

    public EntityRemovalWandItem(@NotNull AlpinePlugin plugin) {
        ExampleConfig config = plugin.getConfiguration(ExampleConfig.class);
        this.modelItem = config.entityRemovalWand.build(plugin);
    }

    /**
     * getItem() handles creating new instances of this item to be given to players.
     * <p>
     * This method should return a clone of the model item. Each instance should be independent
     * such that it can be modified without affecting other instances. When implementing your
     * own items, this is where you should apply:
     * <ul>
     *   <li>Unique identifiers</li>
     *   <li>NBT data or PersistentDataContainer entries</li>
     *   <li>Instance-specific properties</li>
     * </ul>
     */
    @Override
    public @NotNull ItemStack getItem() {
        return this.modelItem.clone();
    }

    /**
     * getDisplayItem() handles creating a display version of this item for preview purposes.
     * <p>
     * This method should return a model item directly, rather than a clone. It's intended for
     * display purposes in interfaces like:
     * <ul>
     *   <li>Shop menus</li>
     *   <li>Reward previews</li>
     *   <li>Item galleries</li>
     *   <li>Admin panels</li>
     * </ul>
     */
    @Override
    public @NotNull ItemStack getDisplayItem() {
        return this.modelItem;
    }

    /**
     * Determines the maximum stack size for this item.
     * <p>
     * Itemize uses this value for:
     * <ul>
     *   <li>Creating bulk quantities of this item</li>
     *   <li>Validating item stacks</li>
     *   <li>Optimizing inventory operations</li>
     * </ul>
     */
    @Override
    public int getMaxStackSize() {
        return this.modelItem.getMaxStackSize();
    }

    /**
     * Determines whether a given ItemStack represents this custom item.
     */
    @Override
    public boolean matches(@NotNull ItemStack itemStack) {
        return this.modelItem.isSimilar(itemStack);
    }
}
