/*
 * This file is part of Itemize - https://github.com/alpine-network/itemize
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.itemize.api.loot;

import co.crystaldev.itemize.api.ConfigItemizeItem;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.SerializeWith;
import de.exlll.configlib.Serializer;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * @since 0.1.0
 */
@Configuration @SerializeWith(serializer = LootTable.Adapter.class)
@NoArgsConstructor
public final class LootTable {

    private Map<ConfigItemizeItem, Chance> items;

    public LootTable(@NotNull Object... entries) {
        this.items = new LinkedHashMap<>();
        for (int i = 0; i < entries.length; i += 2) {
            ConfigItemizeItem item = (ConfigItemizeItem) entries[i];
            Chance chance = (Chance) entries[i + 1];
            this.items.put(item, chance);
        }
    }

    public LootTable(@NotNull Map<ConfigItemizeItem, Chance> items) {
        this.items = new LinkedHashMap<>(items);
    }

    public @NotNull List<ItemStack> getItems(int amount, @Nullable Function<ItemStack, ItemStack> itemFunction) {
        Map<ItemStack, Integer> totals = new HashMap<>();
        for (int i = 0; i < amount; i++) {
            this.items.forEach((item, chance) -> {
                ItemStack template = item.getItem();
                int count = chance.getCount();

                for (int j = 0; j < count; j++) {
                    ItemStack clone = template.clone();
                    if (itemFunction != null)
                        clone = itemFunction.apply(clone);
                    clone.setAmount(1);
                    totals.merge(clone, 1, Integer::sum);
                }
            });
        }

        List<ItemStack> items = new ArrayList<>();
        for (Map.Entry<ItemStack, Integer> entry : totals.entrySet()) {
            ItemStack item = entry.getKey();
            int total = entry.getValue();
            int maxStackSize = item.getMaxStackSize();

            while (total > 0) {
                ItemStack clone = item.clone();
                clone.setAmount(Math.min(total, maxStackSize));
                items.add(clone);
                total -= maxStackSize;
            }
        }

        return items;
    }

    public @NotNull List<ItemStack> getItems(int amount) {
        return this.getItems(amount, null);
    }

    public @NotNull List<ItemStack> getItems(@Nullable Function<ItemStack, ItemStack> itemFunction) {
        return this.getItems(1, itemFunction);
    }

    public @NotNull List<ItemStack> getItems() {
        return this.getItems(1, null);
    }

    static final class Adapter implements Serializer<LootTable, HashMap<String, Object>> {
        @Override
        public HashMap<String, Object> serialize(LootTable element) {
            if (element == null || element.items == null) {
                return new HashMap<>();
            }

            HashMap<String, Object> items = new LinkedHashMap<>();
            element.items.forEach((item, chance) -> {
                items.put(item.asString(), chance.serialize());
            });

            return items;
        }

        @Override
        public LootTable deserialize(HashMap<String, Object> element) {
            Map<ConfigItemizeItem, Chance> items = new LinkedHashMap<>();
            element.forEach((item, chance) -> {
                items.put(ConfigItemizeItem.of(item), Chance.deserialize(chance));
            });

            return new LootTable(items);
        }
    }
}
