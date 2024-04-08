package co.crystaldev.itemize.api;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Represents an item within the Itemize API.
 *
 * @since 1.0.0
 */
public interface ItemizeItem {

    /**
     * Retrieves the underlying {@link ItemStack} associated with this item.
     *
     * @return The {@link ItemStack} representation of this item.
     */
    @NotNull
    ItemStack getItemStack();

    /**
     * Retrieves the {@link ItemStack} used to represent this item.
     *
     * @return The display {@link ItemStack}.
     */
    @NotNull
    default ItemStack getDisplyItemStack() {
        return this.getItemStack();
    }

    /**
     * Compares the specified {@link ItemStack} with this item to determine if they match.
     *
     * @param itemStack The {@link ItemStack} to compare against this item.
     * @return Whether the provided {@link ItemStack} matches this item.
     */
    boolean matches(@NotNull ItemStack itemStack);

    @NotNull
    static ItemizeItem fromItem(@NotNull ItemStack itemStack) {
        return new WrappedItemStack(itemStack);
    }

    @NotNull
    static ItemizeItem fromSupplier(@NotNull Supplier<ItemStack> itemStackSupplier) {
        return new SuppliedItemStack(itemStackSupplier);
    }
}
