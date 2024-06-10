package co.crystaldev.itemize.api;

import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @since 1.1.0
 */
@AllArgsConstructor
final class WrappedStatusEffects implements ItemizeItem {

    private final Set<PotionEffect> effects;

    @Override
    public @NotNull ItemStack getItem() {
        throw new UnsupportedOperationException("not of item stack type");
    }

    @Override
    public @NotNull Set<PotionEffect> getEffects() {
        return this.effects;
    }

    @Override
    public @NotNull ItemType getType() {
        return ItemType.STATUS_EFFECT;
    }

    @Override
    public boolean matches(@NotNull ItemStack itemStack) {
        throw new UnsupportedOperationException("not of item stack type");
    }

    @Override
    public int getMaxStackSize() {
        return -1;
    }
}
