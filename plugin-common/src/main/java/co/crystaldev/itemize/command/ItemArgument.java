package co.crystaldev.itemize.command;

import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.itemize.ItemizeConfig;
import co.crystaldev.itemize.ItemizePlugin;
import co.crystaldev.itemize.api.Identifier;
import co.crystaldev.itemize.api.ItemizeItem;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

/**
 * @since 0.1.0
 */
final class ItemArgument extends AlpineArgumentResolver<ItemizeItem> {
    public ItemArgument() {
        super(ItemizeItem.class, "itemizeItem");
    }

    @Override
    protected ParseResult<ItemizeItem> parse(Invocation<CommandSender> invocation, Argument<ItemizeItem> context, String argument) {
        int index = argument.indexOf(':');
        if (index != -1 && index != argument.lastIndexOf(':')) {
            return ParseResult.failure(ItemizeConfig.getInstance().invalidDelegateMessage.buildString(ItemizePlugin.getInstance()));
        }

        // ensure the key is valid
        ItemizePlugin itemize = ItemizePlugin.getInstance();
        Identifier key = Identifier.fromString(argument, Identifier.MINECRAFT);
        if (key == null || !itemize.contains(key)) {
            return ParseResult.failure(ItemizeConfig.getInstance().invalidItemMessage.buildString(ItemizePlugin.getInstance()));
        }

        return ParseResult.success(itemize.fetch(key));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<ItemizeItem> argument, SuggestionContext context) {
        String current = context.getCurrent().lastLevel().toLowerCase();
        return ItemizePlugin.getInstance().getCombinedRegistry().keySet()
                .stream()
                .map(Identifier::toString)
                .filter(v -> v.contains(current))
                .collect(SuggestionResult.collector());
    }
}
