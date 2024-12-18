package co.crystaldev.itemize.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.alpinecore.util.ItemHelper;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.itemize.ItemizeConfig;
import co.crystaldev.itemize.api.ItemizeItem;
import co.crystaldev.itemize.api.loot.Chance;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Optional;

/**
 * @since 0.1.0
 */
@Command(name = "itemize")
@Description("The primary command for Itemize")
@Permission("itemize.command")
final class ItemizeCommand extends AlpineCommand {
    ItemizeCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player player,
            @Arg("type") @Key("itemizeItem") ItemizeItem item,
            @Arg("amount") Optional<Integer> amount
    ) {
        this.execute(player, player, item, amount);
    }

    @Execute
    public void execute(
            @Context CommandSender player,
            @Arg("recipient") Player recipient,
            @Arg("type") @Key("itemizeItem") ItemizeItem item,
            @Arg("amount") Optional<Integer> amount
    ) {
        PlayerInventory inventory = recipient.getInventory();

        int remaining = Math.max(1, amount.orElse(1));
        while (remaining > 0) {
            int stackSize = Math.min(remaining, item.getMaxStackSize());
            remaining -= stackSize;

            ItemStack builtItem = item.getItem();
            builtItem.setAmount(stackSize);
            inventory.addItem(builtItem);
        }

        if (player instanceof Player) {
            ItemizeConfig config = ItemizeConfig.getInstance();
            Component message = (player.equals(recipient) ? config.giveMessage : config.giveOtherMessage).build(this.plugin,
                    recipient, (Player) player,
                    "amount", amount.orElse(1),
                    "item", ItemHelper.createHoverComponent(item.getDisplayItem()));
            Messaging.send(player, message);
        }
    }

    @Execute(name = "rng")
    public void rng(
            @Context CommandSender player,
            @Arg("recipient") Player recipient,
            @Arg("type") @Key("itemizeItem") ItemizeItem item,
            @Arg("amount") @Key("itemizeChance") Chance chance
    ) {
        PlayerInventory inventory = recipient.getInventory();

        int giveAmount = Math.max(1, chance.getCount());
        int remaining = giveAmount;
        while (remaining > 0) {
            int stackSize = Math.min(remaining, item.getMaxStackSize());
            remaining -= stackSize;

            ItemStack builtItem = item.getItem();
            builtItem.setAmount(stackSize);
            inventory.addItem(builtItem);
        }

        if (player instanceof Player) {
            ItemizeConfig config = ItemizeConfig.getInstance();
            Component message = (player.equals(recipient) ? config.giveMessage : config.giveOtherMessage).build(this.plugin,
                    recipient, (Player) player,
                    "amount", giveAmount,
                    "item", ItemHelper.createHoverComponent(item.getDisplayItem()));
            Messaging.send(player, message);
        }
    }
}
