package co.crystaldev.itemize;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.itemize.api.Itemize;
import co.crystaldev.itemize.api.ItemizeItem;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 04/07/2024
 */
public final class ItemizePlugin extends AlpinePlugin implements Itemize {

    @Getter
    private static ItemizePlugin instance;
    { instance = this; }

    @Getter
    private final Map<NamespacedKey, ItemizeItem> registry = new ConcurrentHashMap<>();

    @Override
    public void register(@NotNull NamespacedKey identifier, @NotNull ItemizeItem item) {
        this.registry.put(identifier, item);
    }

    @Override
    public @NotNull Optional<ItemizeItem> get(@NotNull NamespacedKey identifier) {
        return Optional.ofNullable(this.registry.get(identifier));
    }

    @Override
    public @NotNull Optional<ItemStack> lookup(@NotNull NamespacedKey identifier) {
        return Optional.ofNullable(this.registry.get(identifier)).map(ItemizeItem::getItem);
    }

    @Override
    public boolean matches(@NotNull NamespacedKey identifier, @NotNull ItemStack item) {
        ItemizeItem resolved = this.registry.get(identifier);
        return resolved != null && resolved.matches(item);
    }

    @Override
    public boolean contains(@NotNull NamespacedKey identifier) {
        return this.registry.containsKey(identifier);
    }

    @Override
    public @NotNull Iterable<NamespacedKey> keys() {
        return this.registry.keySet();
    }

    @Override
    public @NotNull Iterable<ItemStack> values() {
        return this.registry.values().stream().map(ItemizeItem::getItem).collect(Collectors.toUnmodifiableSet());
    }
}
