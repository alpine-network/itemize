package co.crystaldev.itemize.registry;

import co.crystaldev.alpinecore.framework.config.ConfigLoader;
import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.itemize.ItemizePlugin;
import co.crystaldev.itemize.api.*;
import co.crystaldev.itemize.api.loot.Chance;
import com.cryptomorin.xseries.XMaterial;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.0
 */
@ApiStatus.Internal
public final class NamedItemLoader {

    public static void load(@NotNull ItemizePlugin itemize) {
        registerItems(itemize);
        registerRewards(itemize);
    }

    private static void registerItems(@NotNull ItemizePlugin itemize) {
        ConfigLoader<NamedItemRegistry> itemRegistryLoader = ConfigLoader
                .builder(itemize, NamedItemRegistry.class, "registry/items")
                .addConfiguration("default", new NamedItemRegistry(
                        "itemize:example_item",
                        DefinedConfigItem.builder(XMaterial.SUGAR)
                                .name("<info>CrustoSugar</info>")
                                .lore(
                                        "<emphasis>Crafted under <i>unusual circumstances</i>, this</emphasis>",
                                        "<emphasis>sugar will grant you great discomfort because it</emphasis>",
                                        "<emphasis>is concrete powder.</emphasis>"
                                )
                                .build()
                ))
                .build();

        for (String key : itemRegistryLoader.getConfigKeys()) {
            NamedItemRegistry registry = itemRegistryLoader.getConfig(key);
            registry.forEach((identifier, item) -> {
                Identifier id = Identifier.fromString(identifier, itemize);
                itemize.register(id, ItemizeItem.fromItem(item.build(itemize)));
            });

            itemize.log(String.format("&aRegistered %d items from item registry \"&d%s&a\"", registry.size(), key));
        }
    }

    private static void registerRewards(@NotNull ItemizePlugin itemize) {
        ConfigLoader<NamedRewardRegistry> rewardRegistryLoader = ConfigLoader
                .builder(itemize, NamedRewardRegistry.class, "registry/rewards")
                .addConfiguration("default", new NamedRewardRegistry(
                        "itemize:example_reward",
                        ConfigItemizeReward.fromItem("itemize:example_item", Chance.literal(1)),
                        "itemize:example_command_reward",
                        ConfigItemizeReward.fromReward(
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
                Identifier id = Identifier.fromString(identifier, itemize);
                itemize.register(id, reward.get());
            });

            itemize.log(String.format("&aRegistered %d items from reward registry \"&d%s&a\"", registry.size(), key));
        }
    }
}
