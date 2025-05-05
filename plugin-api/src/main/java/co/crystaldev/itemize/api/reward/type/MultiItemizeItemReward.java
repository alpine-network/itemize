package co.crystaldev.itemize.api.reward.type;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import co.crystaldev.alpinecore.util.ItemHelper;
import co.crystaldev.itemize.api.*;
import co.crystaldev.itemize.api.loot.Chance;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @since 0.2.0
 */
public final class MultiItemizeItemReward implements ItemizeReward {

    private final ConfigMessage displayName;

    private final ConfigItemizeItem displayItem;

    @Getter
    private final Map<Identifier, Chance> items;

    private final Map<ItemizeItem, Chance> resolvedItems = new LinkedHashMap<>();
    private Component resolvedDisplayName;
    private ItemStack resolvedDisplayItem;

    public MultiItemizeItemReward(@Nullable ConfigMessage displayName, @Nullable ConfigItemizeItem displayItem,
                                  @NotNull Map<Identifier, Chance> items) {
        this.displayName = displayName;
        this.displayItem = displayItem;
        this.items = items;
    }

    @Override
    public @NotNull Component getDisplayName() {
        this.ensureName();
        return this.resolvedDisplayName;
    }

    @Override
    public @NotNull ItemStack getDisplayItem() {
        this.ensureItems();
        return this.resolvedDisplayItem;
    }

    @Override
    public @NotNull List<ResultingReward> execute(@NotNull AlpinePlugin plugin, @NotNull Player player,
                                                  @NotNull Chance rewardCount, @NotNull Object... placeholders) {
        this.ensureItems();
        this.ensureName();

        int count = rewardCount.getCount();

        List<ResultingReward> rewardList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            this.resolvedItems.forEach((item, itemChance) -> {
                int itemCount = itemChance.getCount();
                List<ItemStack> rewards = item.getItem(itemCount);
                rewardList.add(new ResultingReward(this.getDisplayName(), this.getDisplayItem(), rewards, itemCount));
            });
        }

        return rewardList;
    }

    private void ensureItems() {
        if (this.resolvedItems.size() != this.items.size()) {
            this.items.forEach((identifier, chance) -> {
                ItemizeItem item = Itemize.get().fetch(identifier);
                if (item == null) {
                    throw new RuntimeException("Unsupported or invalid ItemizeItemReward type \"" + identifier + "\"");
                }
                this.resolvedItems.put(item, chance);
            });
        }

        if (this.resolvedDisplayItem != null) {
            return;
        }

        if (this.displayItem == null) {
            this.resolvedDisplayItem = Itemize.get().fetch(Identifier.minecraft("stone")).getDisplayItem();
        }
        else {
            this.resolvedDisplayItem = this.displayItem.get().getDisplayItem();
        }
    }

    private void ensureName() {
        if (this.resolvedDisplayName != null) {
            return;
        }

        if (this.displayName == null) {
            // displayName is unavailable, fallback to display item

            this.ensureItems();

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
