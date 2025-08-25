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
