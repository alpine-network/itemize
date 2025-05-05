package co.crystaldev.itemize.api;

import co.crystaldev.itemize.api.item.SuppliedItemStack;
import co.crystaldev.itemize.api.item.WrappedItemStack;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Represents an item within the Itemize API.
 *
 * @since 0.1.0
 */
public interface ItemizeItem {

    /**
     * Retrieves the underlying {@link ItemStack} associated with this item.
     *
     * @return The {@link ItemStack} representation of this item.
     */
    @NotNull ItemStack getItem();

    /**
     * Retrieves the underlying {@link ItemStack} associated with this item.
     *
     * @param amount The item amount.
     * @return The items.
     */
    default @NotNull List<ItemStack> getItem(int amount) {
        List<ItemStack> items = new ArrayList<>();

        int remaining = Math.max(0, amount);
        while (remaining > 0) {
            int stackSize = Math.min(remaining, this.getMaxStackSize());
            remaining -= stackSize;

            ItemStack builtItem = this.getItem();
            builtItem.setAmount(stackSize);
            items.add(builtItem);
        }

        return items;
    }

    /**
     * Retrieves the {@link ItemStack} used to represent this item.
     *
     * @return The display {@link ItemStack}.
     */
    default @NotNull ItemStack getDisplayItem() {
        return this.getItem();
    }

    /**
     * Gets the fixed item type, if present.
     *
     * @return the item type, or null if the item type is variable.
     */
    default @Nullable XMaterial getFixedItemType() {
        return null;
    }

    /**
     * Retrieves the maximum stack size for this item.
     *
     * @return The maximum stack size.
     */
    int getMaxStackSize();

    /**
     * Compares the specified {@link ItemStack} with this item to determine if they match.
     *
     * @param itemStack The {@link ItemStack} to compare against this item.
     * @return Whether the provided {@link ItemStack} matches this item.
     */
    boolean matches(@NotNull ItemStack itemStack);

    static @NotNull ItemizeItem fromItem(@NotNull ItemStack itemStack) {
        return new WrappedItemStack(itemStack);
    }

    static @NotNull ItemizeItem fromSupplier(@NotNull Supplier<ItemStack> itemStackSupplier) {
        return new SuppliedItemStack(itemStackSupplier);
    }
}
