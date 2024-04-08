package co.crystaldev.itemize.api;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @since 1.0.0
 */
record WrappedItemStack(@NotNull ItemStack itemStack) implements ItemizeItem {

    @Override
    public @NotNull ItemStack getItem() {
        return this.itemStack;
    }

    @Override
    public boolean matches(@NotNull ItemStack itemStack) {
        return this.itemStack.equals(itemStack);
    }
}
