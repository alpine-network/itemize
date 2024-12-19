package co.crystaldev.itemize.command;

import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.itemize.api.Identifier;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invocation.Invocation;
import org.bukkit.command.CommandSender;

/**
 * @since 0.1.1
 */
public final class IdentifierArgument extends AlpineArgumentResolver<Identifier> {
    public IdentifierArgument() {
        super(Identifier.class, null);
    }

    @Override
    protected ParseResult<Identifier> parse(Invocation<CommandSender> invocation, Argument<Identifier> context, String argument) {
        Identifier identifier = Identifier.fromString(argument);
        if (identifier == null) {
            return ParseResult.failure(InvalidUsage.Cause.INVALID_ARGUMENT);
        }
        else {
            return ParseResult.success(identifier);
        }
    }
}
