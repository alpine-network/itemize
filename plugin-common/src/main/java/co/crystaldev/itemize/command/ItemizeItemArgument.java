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
import co.crystaldev.itemize.api.ItemizeItem;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @since 0.1.0
 */
public class ItemizeItemArgument extends AlpineArgumentResolver<ItemizeItem> {
    public ItemizeItemArgument(@Nullable String key) {
        super(ItemizeItem.class, key);
    }

    public ItemizeItemArgument() {
        this(null);
    }

    @Override
    protected ParseResult<ItemizeItem> parse(Invocation<CommandSender> invocation, Argument<ItemizeItem> context, String argument) {
        int index = argument.indexOf(':');
        if (index != -1 && index != argument.lastIndexOf(':')) {
            return ParseResult.failure(ItemizeConfig.getInstance().invalidDelegateMessage.buildString(ItemizePlugin.getInstance()));
        }

        ItemizePlugin itemize = ItemizePlugin.getInstance();
        Identifier key = Identifier.fromString(argument, Identifier.MINECRAFT);
        if (key != null && itemize.getMinecraftRegistry().containsKey(key)) {
            return ParseResult.success(itemize.getMinecraftRegistry().get(key));
        }

        // no namespace was provided, and it also failed to match against MC identifiers
        // iterate over our identifiers
        if (index == -1) {
            for (Identifier next : itemize.keys()) {
                if (next.toString().equals(argument) || next.getKey().equals(argument)) {
                    return ParseResult.success(itemize.fetch(next));
                }
            }
        }

        if (key == null || !itemize.contains(key)) {
            return ParseResult.failure(ItemizeConfig.getInstance().invalidItemMessage.buildString(ItemizePlugin.getInstance()));
        }

        return ParseResult.success(itemize.fetch(key));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<ItemizeItem> argument, SuggestionContext context) {
        String current = context.getCurrent().multilevel().toLowerCase(Locale.ROOT);

        Set<String> suggestions = new HashSet<>();
        for (Identifier identifier : ItemizePlugin.getInstance().getCombinedRegistry().keySet()) {
            if (current.isEmpty()) {
                suggestions.add(identifier.toString());
            }
            else {
                String name = identifier.toString();
                if (name.contains(current)) {
                    suggestions.add(name);
                }

                name = identifier.getKey();
                if (name.contains(current)) {
                    suggestions.add(name);
                }
            }
        }

        return SuggestionResult.of(suggestions);
    }

    public static final class ItemArgument extends ItemizeItemArgument {
        public ItemArgument() {
            super("itemizeItem");
        }
    }
}
