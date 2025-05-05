package co.crystaldev.itemize.api.reward.type;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
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
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @since 0.2.0
 */
public final class CommandReward implements ItemizeReward {

    private final ConfigMessage displayName;

    private final ConfigItemizeItem displayItem;

    @Getter
    private final Map<String, Chance> commands;

    private Component resolvedDisplayName;
    private ItemStack resolvedDisplayItem;

    public CommandReward(@Nullable ConfigMessage displayName, @Nullable ConfigItemizeItem displayItem,
                         @NotNull Map<String, Chance> commands) {
        this.displayName = displayName;
        this.displayItem = displayItem;
        this.commands = commands;
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
        this.ensureName();

        int count = rewardCount.getCount();

        List<String> commands = new ArrayList<>();
        this.commands.entrySet().forEach(entry -> {
            String command = entry.getKey();
            int amount = entry.getValue().getCount();

            // format placeholders within the command
            PlaceholderIntegration integration = plugin.getActivatable(PlaceholderIntegration.class);
            if (integration != null) {
                command = integration.replace((OfflinePlayer) player, false, command);
            }
            command = Formatting.placeholders(plugin, command, placeholders);

            // duplicate the commands
            for (int i = 0; i < amount; i++) {
                commands.add(command);
            }
        });

        // execute x times
        for (int i = 0; i < count; i++) {
            commands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
        }

        ResultingReward reward = new ResultingReward(this.resolvedDisplayName, this.resolvedDisplayItem,
                Collections.emptyList(), count);
        return Collections.singletonList(reward);
    }

    private void ensureItem() {
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
