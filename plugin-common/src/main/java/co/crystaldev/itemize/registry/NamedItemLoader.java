package co.crystaldev.itemize.registry;

import co.crystaldev.alpinecore.framework.config.ConfigLoader;
import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import co.crystaldev.itemize.ItemizePlugin;
import co.crystaldev.itemize.api.*;
import co.crystaldev.itemize.api.loot.Chance;
import co.crystaldev.itemize.api.reward.DefinedConfigReward;
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
                        DefinedConfigReward.builder()
                                .addItem("itemize:example_item")
                                .buildConfig(),

                        "itemize:example_multi_reward",
                        DefinedConfigReward.builder()
                                .displayName("<gold>Crust o' Sugar")
                                .displayItem("minecraft:sugar")
                                .addItem("minecraft:sugar")
                                .addItem("minecraft:cocoa_beans")
                                .addItem("minecraft:paper", Chance.range(2, 5))
                                .buildConfig(),

                        "itemize:example_command_reward",
                        DefinedConfigReward.builder()
                                .displayName("<aqua>Diamond")
                                .displayItem("minecraft:diamond")
                                .addCommand("give %player_name% diamond 1")
                                .addCommand("tell %player_name% shine bright light a diamond")
                                .buildConfig()
                ))
                .build();

        for (String key : rewardRegistryLoader.getConfigKeys()) {
            NamedRewardRegistry registry = rewardRegistryLoader.getConfig(key);
            registry.forEach((identifier, reward) -> {
                Identifier id = Identifier.fromString(identifier, itemize);
                itemize.register(id, reward.build());
            });

            itemize.log(String.format("&aRegistered %d items from reward registry \"&d%s&a\"", registry.size(), key));
        }
    }
}
