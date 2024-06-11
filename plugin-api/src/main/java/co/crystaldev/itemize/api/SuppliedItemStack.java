package co.crystaldev.itemize.api;

import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Supplier;

/**
 * @since 0.1.0
 */
@AllArgsConstructor
final class SuppliedItemStack implements ItemizeItem {

    private final Supplier<ItemStack> supplier;

    @Override
    public @NotNull ItemStack getItem() {
        return this.supplier.get();
    }

    @Override
    public @NotNull Set<PotionEffect> getEffects() {
        throw new UnsupportedOperationException("not of potion effect type");
    }

    @Override
    public @NotNull ItemType getType() {
        return ItemType.ITEM_STACK;
    }

    @Override
    public boolean matches(@NotNull ItemStack itemStack) {
        return this.getItem().equals(itemStack);
    }

    @Override
    public int getMaxStackSize() {
        return this.getItem().getMaxStackSize();
    }
}
