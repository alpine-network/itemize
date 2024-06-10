package co.crystaldev.itemize.api;

import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

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
    public @NotNull Set<PotionEffect> getEffects() {
        throw new UnsupportedOperationException("not of potion effect type");
    }

    @Override
    public @NotNull ItemType getType() {
        return ItemType.ITEM_STACK;
    }

    @Override
    public boolean matches(@NotNull ItemStack itemStack) {
        return this.itemStack.isSimilar(itemStack);
    }

    @Override
    public int getMaxStackSize() {
        return this.itemStack.getMaxStackSize();
    }
}
