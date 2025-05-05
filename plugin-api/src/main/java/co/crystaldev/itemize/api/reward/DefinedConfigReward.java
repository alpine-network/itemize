package co.crystaldev.itemize.api.reward;

import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import co.crystaldev.itemize.api.ConfigItemizeItem;
import co.crystaldev.itemize.api.Identifier;
import co.crystaldev.itemize.api.ItemizeReward;
import co.crystaldev.itemize.api.loot.Chance;
import co.crystaldev.itemize.api.reward.type.CommandReward;
import co.crystaldev.itemize.api.reward.type.ItemizeItemReward;
import co.crystaldev.itemize.api.reward.type.MultiItemizeItemReward;
import de.exlll.configlib.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents a reward defined inside a config.
 *
 * @since 0.4.0
 */
@Configuration
@NoArgsConstructor
public final class DefinedConfigReward {

    private ConfigMessage displayName;

    private ConfigItemizeItem displayItem;

    private RewardType rewardType;

    @SerializeWith(serializer = ValueAdapter.class)
    private ValueHolder value;

    public DefinedConfigReward(@Nullable ConfigMessage displayName, @Nullable ConfigItemizeItem displayItem,
                                        @NotNull RewardType rewardType, @NotNull Object value) {
        this.displayName = displayName;
        this.displayItem = displayItem;
        this.rewardType = rewardType;
        this.value = new ValueHolder(value);
    }

    @PostProcess
    private void onInit() {
        if (this.rewardType == null) {
            this.rewardType = RewardType.ITEM;
        }
    }

    public @NotNull ItemizeReward build() {

        // populate reward values
        LinkedHashMap<String, Chance> values = new LinkedHashMap<>();
        Object val = this.value == null ? null : this.value.value;
        if (val instanceof Map) {
            ((Map<String, Object>) val).forEach((value, amount) -> {
                values.put(value, Chance.deserialize(amount));
            });
        }
        else if (val instanceof Iterable) {
            for (String value : (Iterable<String>) val) {
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

    @Configuration @SerializeWith(serializer = ValueAdapter.class)
    @AllArgsConstructor
    static final class ValueHolder {
        private Object value;
    }

    static final class ValueAdapter implements Serializer<ValueHolder, Object> {
        @Override
        public Object serialize(ValueHolder element) {
            Object value = element.value;

            if (value instanceof Map) {
                Map<String, Chance> valueMap = (Map<String, Chance>) value;
                Map<String, Object> serializedValues = new LinkedHashMap<>();
                valueMap.forEach((k, v) -> serializedValues.put(k, v.serialize()));
                return serializedValues;
            }
            else if (value instanceof List) {
                List<String> values = (List<String>) value;
                return values.size() == 1 ? values.get(0) : values;
            }

            return null;
        }

        @Override
        public ValueHolder deserialize(Object element) {
            if (element instanceof Map) {
                Map<String, Object> serializedValues = (Map<String, Object>) element;
                Map<String, Chance> valueMap = new LinkedHashMap<>();
                serializedValues.forEach((k, v) -> valueMap.put(k, Chance.deserialize(v)));
                return new ValueHolder(valueMap);
            }
            else if (element instanceof List) {
                return new ValueHolder(element);
            }
            else if (element instanceof String) {
                return new ValueHolder(Collections.singletonList((String) element));
            }

            return null;
        }
    }
}
