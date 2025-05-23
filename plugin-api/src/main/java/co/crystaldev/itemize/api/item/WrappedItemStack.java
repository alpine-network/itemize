package co.crystaldev.itemize.api.item;

import co.crystaldev.itemize.api.ItemizeItem;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public final class WrappedItemStack implements ItemizeItem {

    private final ItemStack itemStack;

    public WrappedItemStack(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;

        // Force initialize the CraftItemStack
        this.itemStack.setType(itemStack.getType());
    }

    @Override
    public @NotNull ItemStack getItem() {
        return this.itemStack;
    }

    @Override
    public @NotNull XMaterial getFixedItemType() {
        return XMaterial.matchXMaterial(this.itemStack);
    }

    @Override
    public int getMaxStackSize() {
        return this.itemStack.getMaxStackSize();
    }

    @Override
    public boolean matches(@NotNull ItemStack itemStack) {
        return ItemSimilarity.isSimilar(this.itemStack, itemStack);
    }
}
