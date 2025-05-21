package co.crystaldev.example.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import com.cryptomorin.xseries.XMaterial;

public final class ExampleConfig extends AlpineConfig {

    public DefinedConfigItem entityRemovalWand = DefinedConfigItem.builder(XMaterial.STICK)
            .name("<info>Entity Remover Wand</info>")
            .lore(
                    "<emphasis>Instantly banish entities to the Nether...</emphasis>",
                    "",
                    "<info>(!)</info> <emphasis>Right Click Entity To Remove"
            )
            .enchanted()
            .build();

    public ConfigMessage removalMessage = ConfigMessage.of("<info>(!)</info> <emphasis>Banished this %entity_type%!");
}
