/*
 * This file is part of Itemize - https://github.com/alpine-network/itemize
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.itemize.api;

import lombok.Data;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @since 0.2.0
 */
@Data
public final class ResultingReward {

    private final @NotNull Component displayName;

    private final @NotNull ItemStack displayItem;

    private final List<ItemStack> rewards;

    private final int count;

    public @NotNull List<ItemStack> addToInventory(@NotNull Inventory inventory) {
        HashMap<Integer, ItemStack> failed = inventory.addItem(this.rewards.toArray(new ItemStack[0]));

        if (failed.isEmpty()) {
            return Collections.emptyList();
        }

        ArrayList<Map.Entry<Integer, ItemStack>> entries = new ArrayList<>(failed.entrySet());
        entries.sort(Map.Entry.comparingByKey());

        return entries.stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public @NotNull List<ItemStack> addToInventory(@NotNull Player player) {
        return this.addToInventory(player.getInventory());
    }

    public void addToInventoryOrDrop(@NotNull Player player) {
        World world = player.getWorld();
        Location location = player.getLocation();
        for (ItemStack failedItem : this.addToInventory(player.getInventory())) {
            world.dropItemNaturally(location, failedItem);
        }
    }
}
