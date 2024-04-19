package co.crystaldev.itemize.api;

import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * @since 1.0.0
 */
@AllArgsConstructor
final class SuppliedItemStack implements ItemizeItem {

    private final Supplier<ItemStack> supplier;

    @Override
    public @NotNull ItemStack getItem() {
        return this.supplier.get();
    }

    @Override
    public boolean matches(@NotNull ItemStack itemStack) {
        return this.getItem().equals(itemStack);
    }
}
