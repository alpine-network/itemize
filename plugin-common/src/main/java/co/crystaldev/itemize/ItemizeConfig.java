package co.crystaldev.itemize;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import co.crystaldev.alpinecore.framework.config.object.item.DefinedConfigItem;
import de.exlll.configlib.Comment;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 0.1.0
 */
public final class ItemizeConfig extends AlpineConfig {

    @Getter
    private static ItemizeConfig instance;
    { instance = this; }

    @Override
    public String getFileName() {
        return "config.yml";
    }

    @Comment({
            "Custom Item Registry",
            " ",
            "Example:",
            " ",
            "registry:",
            "  custom_drops:mysterious_essence:",
            "    type: ROTTEN_FLESH",
            "    name: <gold>Mysterious Essence",
            "    lore:",
            "      - This is one seriously <i>mysterious</i> item...",
            "      - Treat it with <u>care</u> or it might explode!",
            "    enchanted: true",
    })
    public Map<String, DefinedConfigItem> registry = new HashMap<>();

    @Comment({
            "",
            "Messages"
    })
    public ConfigMessage invalidDelegateMessage = ConfigMessage.of("<red>Invalid delegate provided");

    @Comment("")
    public ConfigMessage invalidItemMessage = ConfigMessage.of("<red>Unregistered or invalid delegate provided");

    @Comment("")
    public ConfigMessage giveMessage = ConfigMessage.of("<info>Itemize</info> <bracket>»</bracket> Received <highlight>%amount%x %item%</highlight>");

    @Comment("")
    public ConfigMessage giveOtherMessage = ConfigMessage.of("<info>Itemize</info> <bracket>»</bracket> Gave %player_name% <highlight>%amount%x %item%</highlight>");
}
