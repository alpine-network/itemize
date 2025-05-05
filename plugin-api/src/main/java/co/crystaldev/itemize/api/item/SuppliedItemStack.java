package co.crystaldev.itemize.api.item;

import co.crystaldev.itemize.api.ItemizeItem;
import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * @since 0.1.0
 */
@AllArgsConstructor
public final class SuppliedItemStack implements ItemizeItem {

    private final Supplier<ItemStack> supplier;

    @Override
    public @NotNull ItemStack getItem() {
        return this.supplier.get();
    }

    @Override
    public boolean matches(@NotNull ItemStack itemStack) {
        return ItemSimilarity.isSimilar(this.getItem(), itemStack);
    }

    @Override
    public int getMaxStackSize() {
        return this.getItem().getMaxStackSize();
    }
}
