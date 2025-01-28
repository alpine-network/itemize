package co.crystaldev.itemize.api;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.itemize.api.loot.Chance;
import co.crystaldev.itemize.api.type.reward.CommandReward;
import co.crystaldev.itemize.api.type.reward.ItemizeItemReward;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Represents a reward within the Itemize API.
 *
 * @since 0.2.0
 */
public interface ItemizeReward {

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
     * @param chance       The chance to roll this reward.
     * @param placeholders The placeholders.
     * @return The rewards.
     */
    @NotNull List<ResultingReward> execute(@NotNull AlpinePlugin plugin, @NotNull Player player, @NotNull Chance chance,
                                           @NotNull Object... placeholders);

    static @NotNull ItemizeReward fromItemize(@NotNull String identifier) {
        return fromItemize(Identifier.fromString(identifier));
    }

    static @NotNull ItemizeReward fromItemize(@NotNull Identifier itemizeItem) {
        return new ItemizeItemReward(itemizeItem);
    }

    static @NotNull ItemizeReward fromCommands(@NotNull Identifier displayItem, @NotNull String... commands) {
        return fromCommands(displayItem, Arrays.asList(commands));
    }

    static @NotNull ItemizeReward fromCommands(@NotNull String displayItem, @NotNull String... commands) {
        return fromCommands(displayItem, Arrays.asList(commands));
    }

    static @NotNull ItemizeReward fromCommands(@NotNull Identifier displayItem, @NotNull Collection<String> commands) {
        return new CommandReward(displayItem, new ArrayList<>(commands));
    }

    static @NotNull ItemizeReward fromCommands(@NotNull String displayItem, @NotNull Collection<String> commands) {
        return new CommandReward(Identifier.fromString(displayItem), new ArrayList<>(commands));
    }
}
