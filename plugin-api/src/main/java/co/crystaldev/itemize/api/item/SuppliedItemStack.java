/*
 * This file is part of Itemize - https://github.com/alpine-network/itemize
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.itemize.api.item;

import co.crystaldev.itemize.api.ItemizeItem;
import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * @since 0.1.0
 */
@AllArgsConstructor
public final class SuppliedItemStack implements ItemizeItem {

    private final Supplier<ItemStack> supplier;

    @Override
    public @NotNull ItemStack getItem() {
        return this.supplier.get();
    }

    @Override
    public boolean matches(@NotNull ItemStack itemStack) {
        return ItemSimilarity.isSimilar(this.getItem(), itemStack);
    }

    @Override
    public int getMaxStackSize() {
        return this.getItem().getMaxStackSize();
    }
}
