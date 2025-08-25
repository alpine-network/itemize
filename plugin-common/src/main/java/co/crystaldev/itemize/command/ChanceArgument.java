/*
 * This file is part of Itemize - https://github.com/alpine-network/itemize
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.itemize.command;

import co.crystaldev.alpinecore.framework.command.AlpineArgumentResolver;
import co.crystaldev.itemize.api.loot.Chance;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.invocation.Invocation;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.CommandSender;

/**
 * @author BestBearr
 * @since 0.2.0
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
