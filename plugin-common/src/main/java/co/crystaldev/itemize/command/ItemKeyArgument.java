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
import co.crystaldev.itemize.ItemizeConfig;
import co.crystaldev.itemize.ItemizePlugin;
import co.crystaldev.itemize.api.Identifier;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

/**
 * @since 0.1.0
 */
final class ItemKeyArgument extends AlpineArgumentResolver<Identifier> {
    public ItemKeyArgument() {
        super(Identifier.class, "itemizeKey");
    }

    @Override
    protected ParseResult<Identifier> parse(Invocation<CommandSender> invocation, Argument<Identifier> context, String argument) {
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

        return ParseResult.success(key);
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Identifier> argument, SuggestionContext context) {
        String current = context.getCurrent().lastLevel().toLowerCase();
        return ItemizePlugin.getInstance().getCombinedRegistry().keySet()
                .stream()
                .map(Identifier::toString)
                .filter(v -> v.contains(current))
                .collect(SuggestionResult.collector());
    }
}
