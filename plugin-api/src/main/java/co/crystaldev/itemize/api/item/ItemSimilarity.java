/*
 * This file is part of Itemize - https://github.com/alpine-network/itemize
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.itemize.api.item;

import co.crystaldev.alpinecore.util.ItemHelper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.2.2
 */
final class ItemSimilarity {
    static boolean isSimilar(@NotNull ItemStack item, @Nullable ItemStack similarItem) {
        // Pretty hacky but we need to ignore durability

        if (similarItem == null || item.getType() != similarItem.getType()) {
            return false;
        }

        // TODO: eventually use NBT or some other system so we can allow renaming items etc.
        //  this hack allows enchantments and durability :bruh:
        if ((item.getItemMeta() instanceof Repairable || item.getDurability() != similarItem.getDurability())) {
            // in what scenario will this not be enough for most custom items?
            // this is also the final iteration of about 2 hours of slamming
            // my head and deffo not the best method. better method is probably
            // just using reflection to access internal comparison tools
            // or set temporarily durability to the same value for both -bearr
            return ItemHelper.getDisplayName(item).equals(ItemHelper.getDisplayName(similarItem));
        }
        else {
            return item.isSimilar(similarItem);
        }
    }
}
