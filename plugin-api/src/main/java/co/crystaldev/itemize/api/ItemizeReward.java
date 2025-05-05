package co.crystaldev.itemize.api;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.itemize.api.loot.Chance;
import co.crystaldev.itemize.api.reward.ItemizeRewardBuilder;
import co.crystaldev.itemize.api.reward.type.ItemizeItemReward;
import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a reward within the Itemize API.
 *
 * @since 0.2.0
 */
public interface ItemizeReward {

    Component UNKNOWN_DISPLAY_NAME = Component.text("<unknown>");

    /**
     * Retrieves the display name for this reward.
     *
     * @return The display name.
     */
    @NotNull Component getDisplayName();

    /**
     * Retrieves the display item stack for this reward.
     *
     * @return The display item.
     */
    @NotNull ItemStack getDisplayItem();

    /**
     * Executes this reward on the provided player.
     *
     * @param plugin       The plugin context.
     * @param player       The player.
     * @param rewardCount  The amount to reward.
     * @param placeholders The placeholders.
     * @return The resulting rewards.
     */
    @NotNull List<ResultingReward> execute(@NotNull AlpinePlugin plugin, @NotNull Player player,
                                           @NotNull Chance rewardCount, @NotNull Object... placeholders);

    /**
     * Executes this reward on the provided player once.
     *
     * @param plugin       The plugin context.
     * @param player       The player.
     * @param placeholders The placeholders.
     * @return The resulting rewards.
     */
    default @NotNull List<ResultingReward> execute(@NotNull AlpinePlugin plugin, @NotNull Player player,
                                                   @NotNull Object... placeholders) {
        return this.execute(plugin, player, Chance.ONE, placeholders);
    }

    static @NotNull ItemizeRewardBuilder builder() {
        return new ItemizeRewardBuilder();
    }

    static @NotNull ItemizeReward fromItem(@NotNull Identifier itemizeItem) {
        return new ItemizeItemReward(itemizeItem);
    }

    static @NotNull ItemizeReward fromItem(@NotNull String itemizeItem) {
        Identifier identifier = Identifier.fromString(itemizeItem);
        Preconditions.checkNotNull(identifier, "Unable to resolve ItemizeItem identifier for \"" + itemizeItem + "\"");
        return fromItem(identifier);
    }
}
