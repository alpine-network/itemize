/*
 * This file is part of Itemize - https://github.com/alpine-network/itemize
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.itemize;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.itemize.api.*;
import co.crystaldev.itemize.registry.NamedItemLoader;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @since 0.1.0
 */
@Getter
public final class ItemizePlugin extends AlpinePlugin implements Itemize {

    @Getter
    private static ItemizePlugin instance;
    { instance = this; }

    private final Map<Identifier, ItemizeItem> registry = new ConcurrentHashMap<>();

    private final Map<Identifier, ItemizeItem> minecraftRegistry;
    {
        ImmutableMap.Builder<Identifier, ItemizeItem> builder = ImmutableMap.builder();
        for (XMaterial value : XMaterial.values()) {
            Material type;
            if (!value.isSupported() || (type = value.get()) == null || !ItemHelper.isItem(type)) {
                continue;
            }

            builder.put(Identifier.minecraft(value.name().toLowerCase(Locale.ROOT)),
                    ItemizeItem.fromItem(value.parseItem()));
        }
        this.minecraftRegistry = builder.build();
    }

    private final Map<Identifier, ItemizeItem> combinedRegistry = new HashMap<>(this.minecraftRegistry);

    private final Map<Identifier, ItemizeReward> rewardRegistry = new ConcurrentHashMap<>();

    @Override
    public void onStart() {
        NamedItemLoader.load(this);
    }

    // region Item

    @Override
    public void register(@NotNull Identifier identifier, @NotNull ItemizeItem item) {
        this.registry.put(identifier, item);
        this.combinedRegistry.put(identifier, item);
    }

    @Override
    public @NotNull Optional<ItemizeItem> get(@NotNull Identifier identifier) {
        ItemizeItem value = this.registry.get(identifier);
        return Optional.ofNullable(value != null ? value
                : this.minecraftRegistry.get(identifier));
    }

    @Override
    public @NotNull Optional<Identifier> get(@NotNull ItemStack itemStack) {
        for (Map.Entry<Identifier, ItemizeItem> entry : this.registry.entrySet()) {
            Identifier key = entry.getKey();
            ItemizeItem value = entry.getValue();

            if (value.matches(itemStack)) {
                return Optional.of(key);
            }
        }

        String key = XMaterial.matchXMaterial(itemStack).name().toLowerCase(Locale.ROOT);
        Identifier identifier = Identifier.minecraft(key);
        return Optional.ofNullable(this.matches(identifier, itemStack) ? identifier : null);
    }

    @Override
    public @Nullable ItemizeItem fetch(@NotNull Identifier identifier) {
        return Optional.ofNullable(this.registry.get(identifier))
                .orElseGet(() -> this.minecraftRegistry.get(identifier));
    }

    @Override
    public boolean matches(@NotNull Identifier identifier, @NotNull ItemStack item) {
        ItemizeItem resolved = this.fetch(identifier);
        return resolved != null && resolved.matches(item);
    }

    @Override
    public boolean contains(@NotNull Identifier identifier) {
        return this.registry.containsKey(identifier)
                || this.minecraftRegistry.containsKey(identifier);
    }

    @Override
    public @NotNull Iterable<Identifier> keys() {
        return this.registry.keySet();
    }

    // endregion Item

    // region Reward

    @Override
    public void register(@NotNull Identifier identifier, @NotNull ItemizeReward reward) {
        this.rewardRegistry.put(identifier, reward);
    }

    @Override
    public @NotNull Optional<Identifier> getReward(@NotNull ItemizeReward reward) {
        for (Map.Entry<Identifier, ItemizeReward> entry : this.rewardRegistry.entrySet()) {
            if (entry.getValue().equals(reward)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    @Override
    public @NotNull Optional<ItemizeReward> getReward(@NotNull Identifier identifier) {
        return Optional.ofNullable(this.rewardRegistry.get(identifier));
    }

    @Override
    public @Nullable ItemizeReward fetchReward(@NotNull Identifier identifier) {
        return this.rewardRegistry.get(identifier);
    }

    @Override
    public boolean containsReward(@NotNull Identifier identifier) {
        return this.rewardRegistry.containsKey(identifier);
    }

    @Override
    public @NotNull Iterable<Identifier> rewardKeys() {
        return this.rewardRegistry.keySet();
    }

    // endregion Reward
}
