package co.crystaldev.itemize.command;

import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.itemize.ItemizePlugin;
import co.crystaldev.itemize.api.ItemizeItem;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 04/07/2024
 */
final class ItemArgument extends AlpineArgumentResolver<ItemizeItem> {
    public ItemArgument() {
        super(ItemizeItem.class, "itemizeItem");
    }

    @Override
    protected ParseResult<ItemizeItem> parse(Invocation<CommandSender> invocation, Argument<ItemizeItem> context, String argument) {
        int index = argument.indexOf(':');
        if (index == -1 || index != argument.lastIndexOf(':')) {
            return ParseResult.failure("<red>Invalid delegate provided");
        }

        // ensure the key is valid
        ItemizePlugin itemize = ItemizePlugin.getInstance();
        NamespacedKey key = NamespacedKey.fromString(argument);
        if (key == null || !itemize.contains(key)) {
            return ParseResult.failure("<red>Unregistered or invalid delegate provided");
        }

        return ParseResult.success(itemize.getRegistry().get(key));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<ItemizeItem> argument, SuggestionContext context) {
        String current = context.getCurrent().lastLevel().toLowerCase();
        return ItemizePlugin.getInstance().getRegistry().keySet()
                .stream()
                .map(NamespacedKey::asString)
                .filter(v -> v.startsWith(current))
                .collect(SuggestionResult.collector());
    }
}
