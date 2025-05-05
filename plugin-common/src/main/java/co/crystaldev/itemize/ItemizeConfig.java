package co.crystaldev.itemize;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import de.exlll.configlib.Comment;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public final class ItemizeConfig extends AlpineConfig {

    @Getter
    private static ItemizeConfig instance;
    { instance = this; }

    @Override
    public @NotNull String getFileName() {
        return "config.yml";
    }

    @Comment({
            "",
            "Messages"
    })
    public ConfigMessage invalidDelegateMessage = ConfigMessage.of(
            "<red>Invalid delegate provided");

    @Comment("")
    public ConfigMessage invalidItemMessage = ConfigMessage.of(
            "<red>Unregistered or invalid delegate provided");

    @Comment("")
    public ConfigMessage identifyMessage = ConfigMessage.of(
            "<info>Itemize</info> <bracket>»</bracket> Resolved: %resolved%");

    @Comment("")
    public ConfigMessage giveMessage = ConfigMessage.of(
            "<info>Itemize</info> <bracket>»</bracket> Received <highlight>%amount%x %item%</highlight>");

    @Comment("")
    public ConfigMessage giveOtherMessage = ConfigMessage.of(
            "<info>Itemize</info> <bracket>»</bracket> Gave %player_name% <highlight>%amount%x %item%</highlight>");

    @Comment("")
    public ConfigMessage rngGiveMessage = ConfigMessage.of(
            "<info>Itemize</info> <bracket>»</bracket> Received <highlight>%amount%x %item%</highlight> " +
                    "<bracket>[<emphasis>%chance%</emphasis>]</bracket>");

    @Comment("")
    public ConfigMessage rngGiveOtherMessage = ConfigMessage.of(
            "<info>Itemize</info> <bracket>»</bracket> Gave %player_name% <highlight>%amount%x %item%</highlight> " +
                    "<bracket>[<emphasis>%chance%</emphasis>]</bracket>");

    @Comment("")
    public ConfigMessage rewardMessage = ConfigMessage.of(
            "<info>Itemize</info> <bracket>»</bracket> Awarded a %reward_name%:",
            "%reward_list%"
    );

    @Comment("")
    public ConfigMessage rewardOtherMessage = ConfigMessage.of(
            "<info>Itemize</info> <bracket>»</bracket> Awarded %player_name% a %reward_name%",
            "%reward_list%"
    );

    @Comment("")
    public ConfigMessage rewardListEntry = ConfigMessage.of(
            "<info> *</info> <emphasis>%item% (%item_count% items)");
}
