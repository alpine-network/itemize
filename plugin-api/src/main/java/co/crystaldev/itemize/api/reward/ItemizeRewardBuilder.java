/*
 * This file is part of Itemize - https://github.com/alpine-network/itemize
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.itemize.api.reward;

import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import co.crystaldev.itemize.api.ConfigItemizeItem;
import co.crystaldev.itemize.api.Identifier;
import co.crystaldev.itemize.api.ItemizeReward;
import co.crystaldev.itemize.api.loot.Chance;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 0.4.0
 */
public final class ItemizeRewardBuilder {

    private ConfigMessage displayName;
    private ConfigItemizeItem displayItem;
    private RewardType rewardType;
    private final Map<Object, Chance> value = new LinkedHashMap<>();

    public @NotNull ItemizeRewardBuilder displayName(@NotNull ConfigMessage displayName) {
        this.displayName = displayName;
        return this;
    }

    public @NotNull ItemizeRewardBuilder displayName(@NotNull String displayName) {
        this.displayName = ConfigMessage.of(displayName);
        return this;
    }

    public @NotNull ItemizeRewardBuilder displayItem(@NotNull ConfigItemizeItem displayItem) {
        this.displayItem = displayItem;
        return this;
    }

    public @NotNull ItemizeRewardBuilder displayItem(@NotNull String itemizeItem) {
        Identifier identifier = Identifier.fromString(itemizeItem);
        Preconditions.checkNotNull(identifier, itemizeItem + " is not a valid identifier");
        this.displayItem = ConfigItemizeItem.of(identifier);
        return this;
    }

    public @NotNull ItemizeRewardBuilder addCommand(@NotNull String command, @NotNull Chance amount) {
        this.ensureType(RewardType.COMMAND);
        this.value.put(command, amount);
        return this;
    }

    public @NotNull ItemizeRewardBuilder addCommand(@NotNull String command) {
        return this.addCommand(command, Chance.ONE);
    }

    public @NotNull ItemizeRewardBuilder addItem(@NotNull String itemizeItem, @NotNull Chance amount) {
        this.ensureType(RewardType.ITEM);
        Identifier identifier = Identifier.fromString(itemizeItem);
        Preconditions.checkNotNull(identifier, itemizeItem + " is not a valid identifier");
        this.value.put(identifier.toString(), amount);
        return this;
    }

    public @NotNull ItemizeRewardBuilder addItem(@NotNull ConfigItemizeItem item, @NotNull Chance amount) {
        return this.addItem(item.asString(), amount);
    }

    public @NotNull ItemizeRewardBuilder addItem(@NotNull ConfigItemizeItem item) {
        return this.addItem(item, Chance.ONE);
    }

    public @NotNull ItemizeRewardBuilder addItem(@NotNull String itemizeItem) {
        return this.addItem(itemizeItem, Chance.ONE);
    }

    public @NotNull DefinedConfigReward buildConfig() {
        if (this.value.isEmpty()) {
            throw new IllegalStateException("Configured ItemizeReward has no entries");
        }

        // if all rewards are 1x, use a list instead
        boolean singletonAmounts = this.value.values().stream()
                .map(v -> v.equals(Chance.ONE))
                .reduce(true, Boolean::logicalAnd);
        Object value = singletonAmounts ? new ArrayList<>(this.value.keySet()) : this.value;

        return new DefinedConfigReward(this.displayName, this.displayItem, this.rewardType, value);
    }

    public @NotNull ItemizeReward buildReward() {
        return this.buildConfig().build();
    }

    private void ensureType(@NotNull RewardType type) {
        if (this.rewardType != type) {
            this.rewardType = type;
            this.value.clear();
        }
    }
}
