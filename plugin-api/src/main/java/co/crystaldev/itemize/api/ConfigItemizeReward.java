package co.crystaldev.itemize.api;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.itemize.api.loot.Chance;
import co.crystaldev.itemize.api.type.reward.CommandReward;
import co.crystaldev.itemize.api.type.reward.ItemizeItemReward;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.SerializeWith;
import de.exlll.configlib.Serializer;
import lombok.Getter;
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

    private ItemizeReward reward;

    private Identifier rewardIdentifier;

    @Getter
    private Chance chance;

    ConfigItemizeReward(@Nullable ItemizeReward reward, @Nullable Identifier rewardIdentifier, @NotNull Chance chance) {
        this.reward = reward;
        this.rewardIdentifier = rewardIdentifier;
        this.chance = chance;
    }

    /**
     * Awards this reward to the provided player.
     *
     * @param plugin       The plugin context.
     * @param player       The player to reward.
     * @param placeholders The placeholders.
     * @return The resulting rewards.
     */
    public @NotNull List<ResultingReward> execute(@NotNull AlpinePlugin plugin, @NotNull Player player, @NotNull Object... placeholders) {
        return this.get().execute(plugin, player, this.chance, placeholders);
    }

    /**
     * Retrieves the {@link ItemizeReward} defined by this ConfigItemizeReward.
     *
     * @return The ItemizeReward defined by this ConfigItemizeReward.
     * @throws IllegalStateException If the reward type is unsupported or invalid.
     */
    public @NotNull ItemizeReward get() {
        if (this.reward != null) {
            return this.reward;
        }
        else if (this.rewardIdentifier != null) {
            this.reward = Itemize.get().fetchReward(this.rewardIdentifier);
            if (this.reward != null) {
                return this.reward;
            }
        }

        throw new IllegalStateException("Unsupported or invalid ItemizeReward type \"" + this.rewardIdentifier + "\"");
    }

    /**
     * Retrieves the identifier of the reward.
     *
     * @return The identifier of the reward.
     * @throws IllegalStateException If the reward type is unsupported or invalid.
     */
    public @NotNull Identifier getRewardIdentifier() {
        Identifier identifier = this.resolveRewardIdentifier();
        if (identifier == null) {
            throw new IllegalStateException("Invalid ItemizeReward provided type");
        }
        else {
            return identifier;
        }
    }

    private @Nullable Identifier resolveRewardIdentifier() {
        if (this.rewardIdentifier != null) {
            return this.rewardIdentifier;
        }
        else if (this.reward != null) {
            this.rewardIdentifier = Itemize.get().getReward(this.reward).orElse(null);
            if (this.rewardIdentifier != null) {
                return this.rewardIdentifier;
            }
        }

        return null;
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

    public static @NotNull ConfigItemizeReward of(@NotNull Identifier identifier, @NotNull Chance chance) {
        return new ConfigItemizeReward(null, identifier, chance);
    }

    public static @NotNull ConfigItemizeReward of(@NotNull String identifier, @NotNull Chance chance) {
        return of(Identifier.fromString(identifier), chance);
    }

    public static @NotNull ConfigItemizeReward of(@NotNull ItemizeReward reward, @NotNull Chance chance) {
        return new ConfigItemizeReward(reward, null, chance);
    }

    static final class Adapter implements Serializer<ConfigItemizeReward, Map<String, Object>> {

        @Override
        public Map<String, Object> serialize(ConfigItemizeReward reward) {
            Identifier identifier = reward.resolveRewardIdentifier();
            if (identifier != null) {
                // itemize:reward_id: 0..5
                return Collections.singletonMap(identifier.toString(), reward.chance.serialize());
            }
            else if (reward.reward == null) {
                // reward is invalid
                return Collections.emptyMap();
            }

            Map<String, Object> serialized = new LinkedHashMap<>();

            if (reward.reward instanceof CommandReward) {
                CommandReward commandReward = (CommandReward) reward.reward;
                serialized.put("type", commandReward.getDisplayItemIdentifier().toString());
                serialized.put("chance", reward.chance.serialize());
                serialized.put("commands", commandReward.getCommands());
            }
            else if (reward.reward instanceof ItemizeItemReward) {
                ItemizeItemReward itemizeItemReward = (ItemizeItemReward) reward.reward;

                // itemize:reward_id: 0..5
                return Collections.singletonMap(itemizeItemReward.getIdentifier().toString(), reward.chance.serialize());
            }
            else {
                throw new IllegalArgumentException("Complex rewards must be registered with Itemize");
            }

            return serialized;
        }

        @Override
        public ConfigItemizeReward deserialize(Map<String, Object> serialized) {
            if (serialized.size() == 1) {
                // itemize:reward_id: 0..5

                Map.Entry<String, Object> first = serialized.entrySet().stream().findFirst().orElse(null);
                Identifier identifier = Identifier.fromString(first.getKey());
                Chance chance = Chance.deserialize(first.getValue());

                return new ConfigItemizeReward(ItemizeReward.fromItemize(identifier), null, chance);
            }

            Chance chance = serialized.containsKey("chance")
                    ? Chance.deserialize(serialized.get("chance"))
                    : Chance.literal(1);
            // display item
            Identifier type = serialized.containsKey("type")
                    ? Identifier.fromString(serialized.get("type").toString()) : null;
            // reward identifier
            Identifier identifier = serialized.containsKey("id")
                    ? Identifier.fromString(serialized.get("id").toString()) : null;

            List<String> commands = (List) serialized.get("command");
            if (type != null && (commands != null || (commands = (List) serialized.get("commands")) != null)) {
                return new ConfigItemizeReward(ItemizeReward.fromCommands(type, commands), identifier, chance);
            }
            else if (type == null) {
                return new ConfigItemizeReward(null, identifier, chance);
            }
            else {
                return new ConfigItemizeReward(ItemizeReward.fromItemize(type), identifier, chance);
            }
        }
    }
}
