package co.crystaldev.itemize.api.reward.type;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import co.crystaldev.alpinecore.util.ItemHelper;
import co.crystaldev.itemize.api.*;
import co.crystaldev.itemize.api.loot.Chance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @since 0.2.0
 */
@RequiredArgsConstructor
public final class ItemizeItemReward implements ItemizeReward {

    private final @Nullable ConfigMessage displayName;

    private final @Nullable ConfigItemizeItem displayItem;

    @Getter
    private final @NotNull Identifier identifier;

    private ItemizeItem resolvedItem;
    private Component resolvedDisplayName;
    private ItemStack resolvedDisplayItem;

    public ItemizeItemReward(@NotNull Identifier identifier) {
        this(null, null, identifier);
    }

    @Override
    public @NotNull Component getDisplayName() {
        this.ensureName();
        return this.resolvedDisplayName;
    }

    @Override
    public @NotNull ItemStack getDisplayItem() {
        this.ensureItem();
        return this.resolvedDisplayItem;
    }

    @Override
    public @NotNull List<ResultingReward> execute(@NotNull AlpinePlugin plugin, @NotNull Player player,
                                                  @NotNull Chance rewardCount, @NotNull Object... placeholders) {
        this.ensureItem();
        int count = rewardCount.getCount();
        List<ItemStack> rewards = this.resolvedItem.getItem(count);
        ResultingReward reward = new ResultingReward(this.getDisplayName(), this.getDisplayItem(), rewards, count);
        return Collections.singletonList(reward);
    }

    private void ensureItem() {
        // attempt to resolve the item first
        if (this.resolvedItem == null) {
            this.resolvedItem = Itemize.get().fetch(this.identifier);
        }
        if (this.resolvedItem == null) {
            throw new RuntimeException("Unsupported or invalid ItemizeItemReward type \"" + this.identifier + "\"");
        }

        // attempt to use provided display item
        if (this.resolvedDisplayItem == null && this.displayItem != null) {
            this.resolvedDisplayItem = this.displayItem.get().getDisplayItem();
        }

        // fallback to displaying reward item
        if (this.resolvedDisplayItem == null) {
            this.resolvedDisplayItem = this.resolvedItem.getDisplayItem();
        }
    }

    private void ensureName() {
        if (this.resolvedDisplayName != null) {
            return;
        }

        if (this.displayName == null) {
            // displayName is unavailable, fallback to display item

            this.ensureItem();

            if (this.resolvedDisplayItem != null) {
                this.resolvedDisplayName = ItemHelper.getDisplayName(this.resolvedDisplayItem);
            }
        }
        else {
            AlpinePlugin plugin = (AlpinePlugin) Itemize.get();
            this.resolvedDisplayName = this.displayName.build(plugin);
        }

        // no display name set, use unknown
        if (this.resolvedDisplayItem == null) {
            this.resolvedDisplayName = UNKNOWN_DISPLAY_NAME;
        }
    }
}
