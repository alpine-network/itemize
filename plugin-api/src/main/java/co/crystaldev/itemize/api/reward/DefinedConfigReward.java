package co.crystaldev.itemize.api.reward;

import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import co.crystaldev.itemize.api.ConfigItemizeItem;
import co.crystaldev.itemize.api.Identifier;
import co.crystaldev.itemize.api.ItemizeReward;
import co.crystaldev.itemize.api.loot.Chance;
import co.crystaldev.itemize.api.reward.type.CommandReward;
import co.crystaldev.itemize.api.reward.type.ItemizeItemReward;
import co.crystaldev.itemize.api.reward.type.MultiItemizeItemReward;
import de.exlll.configlib.Configuration;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a reward defined inside a config.
 *
 * @since 0.4.0
 */
@Configuration
@AllArgsConstructor @NoArgsConstructor
public final class DefinedConfigReward {

    private ConfigMessage displayName;

    private ConfigItemizeItem displayItem;

    private RewardType rewardType;

    private Object value;

    public @NotNull ItemizeReward build() {

        // populate reward values
        LinkedHashMap<String, Chance> values = new LinkedHashMap<>();
        if (this.value instanceof Map) {
            ((Map<String, Object>) this.value).forEach((value, amount) -> {
                values.put(value, Chance.deserialize(amount));
            });
        }
        else if (this.value instanceof Iterable) {
            for (String value : (Iterable<String>) this.value) {
                values.put(value, Chance.ONE);
            }
        }

        switch (this.rewardType) {
            case COMMAND:
                return new CommandReward(this.displayName, this.displayItem, values);
            case ITEM:
                Map.Entry<String, Chance> firstEntry = values.entrySet().stream().findFirst().orElse(null);
                boolean singleton = values.size() == 1 && firstEntry.getValue().equals(Chance.ONE);

                if (singleton) {
                    return new ItemizeItemReward(this.displayName, this.displayItem, Identifier.fromString(firstEntry.getKey()));
                }
                else {
                    Map<Identifier, Chance> formatted = new LinkedHashMap<>();
                    values.forEach((item, chance) -> formatted.put(Identifier.fromString(item), chance));
                    return new MultiItemizeItemReward(this.displayName, this.displayItem, formatted);
                }
            default:
                throw new IllegalStateException("Configured ItemizeItem has an undeclared reward type");
        }
    }

    public static @NotNull ItemizeRewardBuilder builder() {
        return new ItemizeRewardBuilder();
    }
}
