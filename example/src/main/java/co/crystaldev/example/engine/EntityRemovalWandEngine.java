package co.crystaldev.example.engine;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.engine.AlpineEngine;
import co.crystaldev.alpinecore.util.LocaleHelper;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.example.config.ExampleConfig;
import co.crystaldev.example.item.EntityRemovalWandItem;
import co.crystaldev.itemize.api.Identifier;
import co.crystaldev.itemize.api.Itemize;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class EntityRemovalWandEngine extends AlpineEngine {
    EntityRemovalWandEngine(@NotNull AlpinePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        Entity clickedEntity = event.getRightClicked();

        // Do not remove clicked players
        if (clickedEntity instanceof Player) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Ensure that the player is holding an item
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            return;
        }

        Itemize itemize = Itemize.get();
        Optional<Identifier> identifier = itemize.get(itemInHand);

        // Ensure that the player is holding the Entity Removal Wand
        if (!EntityRemovalWandItem.ID.equals(identifier.orElse(null))) {
            return;
        }

        // Remove the entity
        clickedEntity.remove();

        // Notify the player
        ExampleConfig config = this.plugin.getConfiguration(ExampleConfig.class);
        Component entityName = LocaleHelper.getTranslation(clickedEntity);
        Component notification = config.removalMessage.build(this.plugin, player,
                "entity_type", entityName);

        Messaging.send(player, notification);
    }
}
