package co.crystaldev.itemize.api.type.reward;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.integration.PlaceholderIntegration;
import co.crystaldev.alpinecore.util.Formatting;
import co.crystaldev.alpinecore.util.ItemHelper;
import co.crystaldev.itemize.api.*;
import co.crystaldev.itemize.api.loot.Chance;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @since 0.2.0
 */
public final class CommandReward implements ItemizeReward {

    @Getter
    private final Identifier displayItemIdentifier;

    private Component displayName;

    @Getter
    private final List<String> commands;

    private ItemizeItem resolvedItem;

    public CommandReward(@NotNull Identifier displayItem, @NotNull List<String> commands) {
        this.displayItemIdentifier = displayItem;
        this.commands = commands;
    }

    @Override
    public @NotNull Component getDisplayName() {
        this.ensureItem();
        return this.displayName;
    }

    @Override
    public @NotNull ItemStack getDisplayItem() {
        this.ensureItem();
        return this.resolvedItem.getDisplayItem();
    }

    @Override
    public @NotNull List<ResultingReward> execute(@NotNull AlpinePlugin plugin, @NotNull Player player, @NotNull Chance chance,
                                                  @NotNull Object... placeholders) {
        int count = chance.getCount();

        List<String> commands = this.commands.stream().map(command -> {
            PlaceholderIntegration integration = plugin.getActivatable(PlaceholderIntegration.class);
            if (integration != null) {
                command = integration.replace((OfflinePlayer) player, false, command);
            }
            return Formatting.placeholders(command, placeholders);
        }).collect(Collectors.toList());

        for (int i = 0; i < count; i++) {
            commands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
        }

        this.ensureItem();
        ResultingReward reward = new ResultingReward(this.displayName, this.resolvedItem.getDisplayItem(),
                Collections.emptyList(), count);
        return Collections.singletonList(reward);
    }

    private void ensureItem() {
        if (this.resolvedItem == null) {
            this.resolvedItem = Itemize.get().fetch(this.displayItemIdentifier);

            if (this.resolvedItem != null && this.displayName == null) {
                ItemStack displayItem = this.resolvedItem.getDisplayItem();
                this.displayName = ItemHelper.getDisplayName(displayItem);
            }
        }
    }
}
