package co.crystaldev.itemize;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.itemize.api.Identifier;
import co.crystaldev.itemize.api.Itemize;
import co.crystaldev.itemize.api.ItemizeItem;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
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
            ItemStack item;
            if (!value.isSupported() || (item = value.parseItem()) == null || !ItemHelper.isItem(item.getType())) {
                continue;
            }

            builder.put(Identifier.minecraft(value.name().toLowerCase()), ItemizeItem.fromItem(item));
        }
        this.minecraftRegistry = builder.build();
    }

    private final Map<Identifier, ItemizeItem> combinedRegistry = new HashMap<>(this.minecraftRegistry);

    @Override
    public void register(@NotNull Identifier identifier, @NotNull ItemizeItem item) {
        this.registry.put(identifier, item);
        this.combinedRegistry.put(identifier, item);
    }

    @Override
    public @NotNull Optional<ItemizeItem> get(@NotNull Identifier identifier) {
        ItemizeItem value = this.registry.get(identifier);
        return Optional.ofNullable(value != null ? value : this.minecraftRegistry.get(identifier));
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

        Identifier identifier = Identifier.minecraft(XMaterial.matchXMaterial(itemStack).name().toLowerCase());
        return Optional.ofNullable(this.matches(identifier, itemStack) ? identifier : null);
    }

    @Override
    public @Nullable ItemizeItem fetch(@NotNull Identifier identifier) {
        return Optional.ofNullable(this.registry.get(identifier)).orElseGet(() -> this.minecraftRegistry.get(identifier));
    }

    @Override
    public boolean matches(@NotNull Identifier identifier, @NotNull ItemStack item) {
        ItemizeItem resolved = this.fetch(identifier);
        return resolved != null && resolved.matches(item);
    }

    @Override
    public boolean contains(@NotNull Identifier identifier) {
        return this.registry.containsKey(identifier) || this.minecraftRegistry.containsKey(identifier);
    }

    @Override
    public @NotNull Iterable<Identifier> keys() {
        return this.registry.keySet();
    }
}
