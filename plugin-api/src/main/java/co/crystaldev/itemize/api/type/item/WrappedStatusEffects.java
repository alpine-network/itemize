package co.crystaldev.itemize.api.type.item;

import co.crystaldev.itemize.api.ItemType;
import co.crystaldev.itemize.api.ItemizeItem;
import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @since 0.1.0
 * @deprecated since 0.2.0. Being removed in a future version
 */
@AllArgsConstructor
@Deprecated
public final class WrappedStatusEffects implements ItemizeItem {

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
