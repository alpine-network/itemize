package co.crystaldev.itemize.api;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * @since 1.0.0
 */
record SuppliedItemStack(@NotNull Supplier<ItemStack> supplier) implements ItemizeItem {

    @Override
    public @NotNull ItemStack getItemStack() {
        return this.supplier.get();
    }

    @Override
    public boolean matches(@NotNull ItemStack itemStack) {
        return this.getItemStack().equals(itemStack);
    }
}
