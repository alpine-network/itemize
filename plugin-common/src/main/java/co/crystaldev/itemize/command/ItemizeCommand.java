package co.crystaldev.itemize.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.itemize.api.ItemizeItem;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.Optional;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 04/07/2024
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
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < amount.orElse(1); i++) {
            // each item could be unique; query each item individually
            inventory.addItem(item.getItemStack());
        }
    }
}
