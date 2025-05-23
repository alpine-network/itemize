package co.crystaldev.itemize.registry;

import co.crystaldev.alpinecore.util.CollectionUtils;
import co.crystaldev.itemize.api.reward.DefinedConfigReward;
import de.exlll.configlib.Configuration;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @since 0.2.0
 */
@Configuration @NoArgsConstructor
final class NamedRewardRegistry {

    private LinkedHashMap<String, DefinedConfigReward> rewards = new LinkedHashMap<>();

    public NamedRewardRegistry(@NotNull String k1, @NotNull DefinedConfigReward v1, @NotNull Object... items) {
        this.rewards.put(k1, v1);
        this.rewards.putAll((Map) CollectionUtils.linkedMapFromArray(items));
    }

    public void forEach(@NotNull BiConsumer<String, DefinedConfigReward> itemConsumer) {
        this.rewards.forEach(itemConsumer);
    }

    public int size() {
        return this.rewards.size();
    }
}
