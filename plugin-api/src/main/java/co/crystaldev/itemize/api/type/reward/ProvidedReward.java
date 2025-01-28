package co.crystaldev.itemize.api.type.reward;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.util.ItemHelper;
import co.crystaldev.itemize.api.ItemizeReward;
import co.crystaldev.itemize.api.ResultingReward;
import co.crystaldev.itemize.api.loot.Chance;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @since 0.2.0
 */
public final class ProvidedReward implements ItemizeReward {

    private final ItemStack displayItem;

    private final Component displayName;

    private final Consumer<Player> rewardProvider;

    public ProvidedReward(@NotNull ItemStack displayItem, @NotNull Consumer<Player> rewardProvider) {
        this.displayItem = displayItem;
        this.displayName = ItemHelper.getDisplayName(displayItem);
        this.rewardProvider = rewardProvider;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return this.displayName;
    }

    @Override
    public @NotNull ItemStack getDisplayItem() {
        return this.displayItem;
    }

    @Override
    public @NotNull List<ResultingReward> execute(@NotNull AlpinePlugin plugin, @NotNull Player player, @NotNull Chance chance,
                                                  @NotNull Object... placeholders) {
        int count = chance.getCount();

        for (int i = 0; i < count; i++) {
            this.rewardProvider.accept(player);
        }

        ResultingReward reward = new ResultingReward(this.displayName, this.displayItem,
                Collections.singletonList(this.displayItem), count);
        return Collections.singletonList(reward);
    }
}
