package co.crystaldev.itemize.api;

import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @since 1.0.0
 */
@AllArgsConstructor
final class WrappedItemStack implements ItemizeItem {

    private final ItemStack itemStack;

    @Override
    public @NotNull ItemStack getItem() {
        return this.itemStack;
    }

    @Override
    public boolean matches(@NotNull ItemStack itemStack) {
        return this.itemStack.equals(itemStack);
    }
}
