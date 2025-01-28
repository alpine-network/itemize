package co.crystaldev.itemize.api.type.reward;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.util.ItemHelper;
import co.crystaldev.itemize.api.*;
import co.crystaldev.itemize.api.loot.Chance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * @since 0.2.0
 */
@RequiredArgsConstructor
public final class ItemizeItemReward implements ItemizeReward {

    @Getter
    private final Identifier identifier;

    private ItemizeItem cachedItem;

    @Override
    public @NotNull Component getDisplayName() {
        this.ensureItem();
        return ItemHelper.getDisplayName(this.cachedItem.getDisplayItem());
    }

    @Override
    public @NotNull ItemStack getDisplayItem() {
        this.ensureItem();
        return this.cachedItem.getDisplayItem();
    }

    @Override
    public @NotNull List<ResultingReward> execute(@NotNull AlpinePlugin plugin, @NotNull Player player, @NotNull Chance chance,
                                                  @NotNull Object... placeholders) {
        this.ensureItem();
        int amount = chance.getCount();
        List<ItemStack> rewards = this.cachedItem.getItem(amount);
        ResultingReward reward = new ResultingReward(this.getDisplayName(), this.getDisplayItem(), rewards, amount);
        return Collections.singletonList(reward);
    }

    private void ensureItem() {
        if (this.cachedItem == null) {
            this.cachedItem = Itemize.get().fetch(this.identifier);
        }
    }
}
