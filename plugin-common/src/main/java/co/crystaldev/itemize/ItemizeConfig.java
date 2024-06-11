package co.crystaldev.itemize;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import de.exlll.configlib.Comment;
import lombok.Getter;

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

    public ConfigMessage invalidDelegateMessage = ConfigMessage.of("<red>Invalid delegate provided");

    @Comment("")
    public ConfigMessage invalidItemMessage = ConfigMessage.of("<red>Unregistered or invalid delegate provided");
}
