/*
 * This file is part of Itemize - https://github.com/alpine-network/itemize
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.itemize.api;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.itemize.api.loot.Chance;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.SerializeWith;
import de.exlll.configlib.Serializer;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents a configurable reward used in the Itemize system.
 *
 * @since 0.2.0
 */
@NoArgsConstructor
@Configuration @SerializeWith(serializer = ConfigItemizeReward.Adapter.class)
public final class ConfigItemizeReward {

    public static final Identifier UNKNOWN_REWARD = Identifier.fromString("itemize:null_reward");

    private Identifier rewardIdentifier;
    private Identifier itemIdentifier;

    private transient ItemizeReward resolvedReward;
    private transient Identifier resolvedIdentifier;

    private transient Identifier lazyloadIdentifier;

    private transient boolean resolved;

    ConfigItemizeReward(@Nullable Identifier rewardIdentifier, @Nullable Identifier itemIdentifier) {
        this.rewardIdentifier = rewardIdentifier;
        this.itemIdentifier = itemIdentifier;
    }

    ConfigItemizeReward(@NotNull Identifier lazyloadId) {
        this.lazyloadIdentifier = lazyloadId;
    }

    /**
     * Awards this reward to the provided player.
     *
     * @param plugin       The plugin context.
     * @param player       The player to reward.
     * @param placeholders The placeholders.
     * @return The resulting rewards.
     */
    public @NotNull List<ResultingReward> execute(@NotNull AlpinePlugin plugin, @NotNull Player player,
                                                  @NotNull Chance chance, @NotNull Object... placeholders) {
        return this.get().execute(plugin, player, chance, placeholders);
    }

    /**
     * Awards this reward to the provided player once.
     *
     * @param plugin       The plugin context.
     * @param player       The player to reward.
     * @param placeholders The placeholders.
     * @return The resulting rewards.
     */
    public @NotNull List<ResultingReward> execute(@NotNull AlpinePlugin plugin, @NotNull Player player,
                                                  @NotNull Object... placeholders) {
        return this.get().execute(plugin, player, Chance.ONE, placeholders);
    }

    /**
     * Retrieves the {@link ItemizeReward} defined by this ConfigItemizeReward.
     *
     * @return The ItemizeReward defined by this ConfigItemizeReward.
     * @throws IllegalStateException If the reward type is unsupported or invalid.
     */
    public @NotNull ItemizeReward get() {
        this.ensureLoaded();

        if (this.resolvedReward != null) {
            return this.resolvedReward;
        }
        else if (this.itemIdentifier != null) {
            this.resolvedReward = ItemizeReward.fromItem(this.itemIdentifier);
            this.resolvedIdentifier = this.itemIdentifier;
            return this.resolvedReward;
        }
        else if (this.rewardIdentifier != null) {
            this.resolvedReward = Itemize.get().fetchReward(this.rewardIdentifier);
            this.resolvedIdentifier = this.rewardIdentifier;
            if (this.resolvedReward != null) {
                return this.resolvedReward;
            }
        }

        throw new IllegalStateException("Unsupported or invalid ItemizeReward type \"" + this.lazyloadIdentifier + "\"");
    }

    /**
     * Retrieves the identifier of the reward.
     *
     * @return The identifier of the reward.
     * @throws IllegalStateException If the reward type is unsupported or invalid.
     */
    public @NotNull Identifier getRewardIdentifier() {
        this.ensureLoaded();

        Identifier identifier = this.resolveRewardIdentifier();
        if (identifier == null) {
            Identifier source = this.rewardIdentifier == null ? this.itemIdentifier : this.rewardIdentifier;
            if (source == null) {
                source = UNKNOWN_REWARD;
            }
            throw new IllegalStateException(String.format("Unable to resolve reward identifier for \"%s\"", source));
        }
        else {
            return identifier;
        }
    }

    private @Nullable Identifier resolveRewardIdentifier() {
        this.ensureLoaded();

        if (this.resolvedIdentifier != null) {
            return this.resolvedIdentifier;
        }
        else if (this.rewardIdentifier != null) {
            this.resolvedIdentifier = this.rewardIdentifier;
            return this.rewardIdentifier;
        }
        else if (this.itemIdentifier != null) {
            this.resolvedIdentifier = this.itemIdentifier;
            return this.itemIdentifier;
        }

        return null;
    }

    private void ensureLoaded() {
        if (this.lazyloadIdentifier != null && !this.resolved) {
            Itemize itemize = Itemize.get();
            if (itemize.containsReward(this.lazyloadIdentifier)) {
                this.resolvedIdentifier = this.lazyloadIdentifier;
                this.resolvedReward = itemize.fetchReward(this.lazyloadIdentifier);
            }
            else if (itemize.contains(this.lazyloadIdentifier)) {
                this.resolvedIdentifier = this.lazyloadIdentifier;
                this.itemIdentifier = this.lazyloadIdentifier;
            }

            this.resolved = true;
        }
    }

    /**
     * Retrieves the identifier of the reward.
     *
     * @return The identifier of the reward.
     * @throws IllegalStateException If the reward type is unsupported or invalid.
     */
    public @NotNull String asString() {
        return this.getRewardIdentifier().toString();
    }

    public static @NotNull ConfigItemizeReward fromItem(@NotNull Identifier itemId) {
        return new ConfigItemizeReward(null, itemId);
    }

    public static @NotNull ConfigItemizeReward fromItem(@NotNull String itemId) {
        return fromItem(Identifier.fromString(itemId));
    }

    public static @NotNull ConfigItemizeReward fromReward(@NotNull Identifier rewardId) {
        return new ConfigItemizeReward(rewardId, null);
    }

    public static @NotNull ConfigItemizeReward fromReward(@NotNull String rewardId) {
        return fromReward(Identifier.fromString(rewardId));
    }

    static final class Adapter implements Serializer<ConfigItemizeReward, String> {

        @Override
        public String serialize(ConfigItemizeReward reward) {
            try {
                return reward.asString();
            }
            catch (IllegalStateException ex) {
                // reward is not registered, fallback to configured identifier

                Identifier source = reward.rewardIdentifier == null ? reward.itemIdentifier : reward.rewardIdentifier;

                // ensure the identifier is present
                if (source == null) {
                    throw new IllegalArgumentException("Unable to resolve ItemizeReward identifier", ex);
                }

                return source.toString();
            }
        }

        @Override
        public ConfigItemizeReward deserialize(String serialized) {
            Identifier identifier = Identifier.fromString(serialized);
            if (identifier == null) {
                throw new IllegalArgumentException("Unable to resolve ItemizeReward identifier for \"" + serialized + "\"");
            }

            return new ConfigItemizeReward(identifier);
        }
    }
}
