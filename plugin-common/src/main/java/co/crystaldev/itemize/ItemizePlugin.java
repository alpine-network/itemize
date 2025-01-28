package co.crystaldev.itemize;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.config.ConfigLoader;
import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.itemize.api.*;
import co.crystaldev.itemize.api.loot.Chance;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.Material;
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
            Material type;
            if (!value.isSupported() || (type = value.parseMaterial()) == null || !ItemHelper.isItem(type)) {
                continue;
            }

            builder.put(Identifier.minecraft(value.name().toLowerCase()), ItemizeItem.fromItem(value.parseItem()));
        }
        this.minecraftRegistry = builder.build();
    }

    private final Map<Identifier, ItemizeItem> combinedRegistry = new HashMap<>(this.minecraftRegistry);

    private final Map<Identifier, ItemizeReward> rewardRegistry = new ConcurrentHashMap<>();

    @Override
    public void onStart() {
        ItemizeConfig config = ItemizeConfig.getInstance();
        config.registry.forEach((identifier, item) -> {
            Identifier id = Identifier.fromString(identifier, this);
            this.register(id, ItemizeItem.fromItem(item.build(this)));
        });

        ConfigLoader<NamedItemRegistry> itemRegistryLoader = ConfigLoader
                .builder(this, NamedItemRegistry.class, "registry/items")
                .addConfiguration("default", new NamedItemRegistry(
                        "itemize:example_item",
                        DefinedConfigItem.builder(XMaterial.SUGAR)
                                .name("<info>CrustoSugar</info>")
                                .lore(
                                        "<emphasis>Crafted under <i>unusual circumstances</i>, this</emphasis>",
                                        "<emphasis>sugar will grant you great discomfort because it is</emphasis>",
                                        "<emphasis>simply dried cum and dick cheese.</emphasis>"
                                )
                                .build()
                ))
                .build();
        for (String key : itemRegistryLoader.getConfigKeys()) {
            NamedItemRegistry registry = itemRegistryLoader.getConfig(key);
            registry.forEach((identifier, item) -> {
                Identifier id = Identifier.fromString(identifier, this);
                this.register(id, ItemizeItem.fromItem(item.build(this)));
            });

            this.log(String.format("&aRegistered %d items from item registry \"&d%s&a\"", registry.size(), key));
        }

        ConfigLoader<NamedRewardRegistry> rewardRegistryLoader = ConfigLoader
                .builder(this, NamedRewardRegistry.class, "registry/rewards")
                .addConfiguration("default", new NamedRewardRegistry(
                        "itemize:example_reward",
                        ConfigItemizeReward.of("itemize:example_item", Chance.literal(1)),
                        "itemize:example_command_reward",
                        ConfigItemizeReward.of(
                                ItemizeReward.fromCommands("itemize:example_item",
                                        "minecraft:tell %player_name% You did it!",
                                        "itemize rng %player_name% minecraft:dirt 1..5"),
                                Chance.literal(1)
                        )
                ))
                .build();
        for (String key : rewardRegistryLoader.getConfigKeys()) {
            NamedRewardRegistry registry = rewardRegistryLoader.getConfig(key);
            registry.forEach((identifier, reward) -> {
                Identifier id = Identifier.fromString(identifier, this);
                this.register(id, reward.get());
            });

            this.log(String.format("&aRegistered %d items from reward registry \"&d%s&a\"", registry.size(), key));
        }
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
