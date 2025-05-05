package co.crystaldev.itemize.api.reward.type;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.util.ItemHelper;
import co.crystaldev.itemize.api.*;
import co.crystaldev.itemize.api.loot.Chance;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @since 0.2.0
 */
@RequiredArgsConstructor
public final class ItemStackReward implements ItemizeReward {

    private final ItemStack item;

    @Override
    public @NotNull Component getDisplayName() {
        return ItemHelper.getDisplayName(this.item);
    }

    @Override
    public @NotNull ItemStack getDisplayItem() {
        return this.item;
    }

    @Override
    public @NotNull List<ResultingReward> execute(@NotNull AlpinePlugin plugin, @NotNull Player player,
                                                  @NotNull Chance rewardCount, @NotNull Object... placeholders) {
        int count = rewardCount.getCount();
        List<ItemStack> rewards = new ArrayList<>();

        int remaining = Math.max(0, count);
        while (remaining > 0) {
            int stackSize = Math.min(remaining, this.item.getMaxStackSize());
            remaining -= stackSize;

            ItemStack builtItem = this.item.clone();
            builtItem.setAmount(stackSize);
            rewards.add(builtItem);
        }

        ResultingReward reward = new ResultingReward(this.getDisplayName(), this.item, rewards, count);
        return Collections.singletonList(reward);
    }
}
