package co.crystaldev.example;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.example.item.EntityRemovalWandItem;
import co.crystaldev.itemize.api.Itemize;

public final class ItemizeExamplePlugin extends AlpinePlugin {

    @Override
    public void onStart() {
        // Register your items on server startup

        // example:entity_removal_wand
        Itemize.get().register(EntityRemovalWandItem.ID, new EntityRemovalWandItem(this));
    }
}
