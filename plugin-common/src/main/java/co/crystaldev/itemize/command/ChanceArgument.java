package co.crystaldev.itemize.command;

import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.itemize.api.loot.Chance;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.invocation.Invocation;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.CommandSender;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/07/2024
 */
public final class ChanceArgument extends AlpineArgumentResolver<Chance> {
    public ChanceArgument() {
        super(Chance.class, "itemizeChance");
    }

    @Override
    protected ParseResult<Chance> parse(Invocation<CommandSender> invocation, Argument<Chance> context, String argument) {
        if (NumberUtils.isNumber(argument)) {
            double chance = Double.parseDouble(argument);
            if (chance >= 0.0 && chance <= 1.0) {
                return ParseResult.success(Chance.chance(chance));
            }
            else {
                return ParseResult.success(Chance.literal((int) chance));
            }
        }

        return ParseResult.success(Chance.deserialize(argument));
    }
}
