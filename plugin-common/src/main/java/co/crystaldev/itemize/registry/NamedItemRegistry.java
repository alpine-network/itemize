package co.crystaldev.itemize.registry;

import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.alpinecore.util.CollectionUtils;
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
final class NamedItemRegistry {

    private LinkedHashMap<String, DefinedConfigItem> items = new LinkedHashMap<>();

    public NamedItemRegistry(@NotNull String k1, @NotNull DefinedConfigItem v1, @NotNull Object... items) {
        this.items.put(k1, v1);
        this.items.putAll((Map) CollectionUtils.linkedMapFromArray(items));
    }

    public void forEach(@NotNull BiConsumer<String, DefinedConfigItem> itemConsumer) {
        this.items.forEach(itemConsumer);
    }

    public int size() {
        return this.items.size();
    }
}
