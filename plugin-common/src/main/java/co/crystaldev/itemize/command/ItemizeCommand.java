/*
 * This file is part of Itemize - https://github.com/alpine-network/itemize
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.itemize.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.alpinecore.util.ItemHelper;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.alpinecore.util.SimpleTimer;
import co.crystaldev.itemize.ItemizeConfig;
import co.crystaldev.itemize.ItemizePlugin;
import co.crystaldev.itemize.api.*;
import co.crystaldev.itemize.api.loot.Chance;
import co.crystaldev.itemize.registry.NamedItemLoader;
import com.cryptomorin.xseries.XMaterial;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import dev.rollczi.litecommands.annotations.permission.Permission;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * @since 0.1.0
 */
@Command(name = "itemize", aliases = "i")
@Description("The primary command for Itemize.")
@Permission("itemize.command")
final class ItemizeCommand extends AlpineCommand {

    private static final int MAX_GIVE_AMOUNT = 2304;

    ItemizeCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute(name = "reload")
    @Description("Reloads all registered items.")
    public void reload(@Context CommandSender sender) {
        this.plugin.log("&e=== RELOAD &aSTART &e===");

        SimpleTimer timer = new SimpleTimer();
        timer.start();
        NamedItemLoader.load(ItemizePlugin.getInstance());

        long duration = timer.stop();
        this.plugin.log(String.format("&e=== RELOAD &aCOMPLETE&e (&d%dms&e) ===", duration));

        // Notify the sender
        if (sender instanceof Player) {
            ItemizeConfig config = ItemizeConfig.getInstance();

            Messaging.send(sender, config.reload.build(this.plugin, (Player) sender,
                    "duration", duration));
        }
    }

    //   /itemize <item_id> [amount=1]
    @Execute
    public void execute(
            @Context Player player,
            @Arg("type") @Key("itemizeItem") ItemizeItem item,
            @Arg("amount") Optional<Integer> amount
    ) {
        this.execute(player, player, item, amount);
    }

    //   /itemize <recipient> <item_id> [amount=1]
    @Execute
    public void execute(
            @Context CommandSender sender,
            @Arg("recipient") Player recipient,
            @Arg("type") @Key("itemizeItem") ItemizeItem item,
            @Arg("amount") Optional<Integer> amount
    ) {
        // retrieve the items
        int giveAmount = Math.min(MAX_GIVE_AMOUNT, amount.orElse(1));
        ItemStack[] itemArray = item.getItem(giveAmount).toArray(new ItemStack[0]);

        // give the items to the player
        PlayerInventory inventory = recipient.getInventory();
        HashMap<Integer, ItemStack> lostItems = inventory.addItem(itemArray);
        if (!lostItems.isEmpty()) {
            int sum = lostItems.values().stream().map(ItemStack::getAmount).reduce(0, Integer::sum);
            giveAmount -= sum;
        }

        // notify the sender
        if (sender instanceof Player) {
            ItemizeConfig config = ItemizeConfig.getInstance();
            Component message = (sender.equals(recipient) ? config.giveMessage : config.giveOtherMessage).build(this.plugin,
                    recipient, (Player) sender,
                    "amount", giveAmount,
                    "item", ItemHelper.createHoverComponent(item.getDisplayItem()));
            Messaging.send(sender, message);
        }
    }

    //   /itemize identify
    @Execute(name = "identify")
    @Description("Identifies the held item.")
    public void identify(@Context Player player) {
        ItemizeConfig config = ItemizeConfig.getInstance();

        ItemStack itemInHand = XMaterial.supports(9) ? player.getItemInHand()
                : player.getInventory().getItemInMainHand();

        String id = Itemize.get().get(itemInHand).map(Identifier::toString).orElse("unknown");
        Messaging.send(player, config.identifyMessage.build(this.plugin, player, "resolved", id));
    }

    //   /itemize reward <reward_id> [chance=1]
    @Execute(name = "reward")
    @Description("Retrieve a registered ItemizeReward.")
    public void reward(
            @Context Player player,
            @Arg("type") @Key("itemizeReward") ItemizeReward reward,
            @OptionalArg("amount") @Key("itemizeChance") Chance chance
    ) {
        this.reward(player, player, reward, chance);
    }

    //   /itemize reward <recipient> <reward_id> [chance=1]
    @Execute(name = "reward")
    @Description("Give a registered ItemizeReward to a recipient player.")
    public void reward(
            @Context CommandSender sender,
            @Arg("recipient") Player recipient,
            @Arg("type") @Key("itemizeReward") ItemizeReward reward,
            @OptionalArg("amount") @Key("itemizeChance") Chance chance
    ) {
        if (chance == null) {
            chance = Chance.literal(1);
        }
        List<ResultingReward> rewards = reward.execute(this.plugin, recipient, chance);

        // give the items to the recipient
        rewards.forEach(r -> r.addToInventoryOrDrop(recipient));

        // notify the sender
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemizeConfig config = ItemizeConfig.getInstance();
            Component rewardsList = rewards.stream()
                    .map(r -> config.rewardListEntry.build(this.plugin, recipient,
                            "item", r.getDisplayName(),
                            "item_count", r.getCount()))
                    .collect(Component.toComponent(Component.newline()));

            Component message = (player.equals(recipient) ? config.rewardMessage : config.rewardOtherMessage).build(this.plugin,
                    recipient,
                    "reward_name", reward.getDisplayName(),
                    "item", ItemHelper.createHoverComponent(reward.getDisplayItem()),
                    "reward_list", rewardsList,
                    "chance", chance.toString());
            Messaging.send(player, message);
        }
    }

    //   /itemize rng <item_id> <probability>
    @Execute(name = "rng")
    @Description("Retrieve a random number of a registered ItemizeItem.")
    public void rng(
            @Context Player player,
            @Arg("type") @Key("itemizeItem") ItemizeItem item,
            @Arg("amount") @Key("itemizeChance") Chance chance
    ) {
        this.rng(player, player, item, chance);
    }

    //   /itemize rng <recipient> <item_id> <probability>
    @Execute(name = "rng")
    @Description("Give a registered ItemizeItem to a recipient player.")
    public void rng(
            @Context CommandSender sender,
            @Arg("recipient") Player recipient,
            @Arg("type") @Key("itemizeItem") ItemizeItem item,
            @Arg("amount") @Key("itemizeChance") Chance chance
    ) {
        // retrieve the items
        List<ItemStack> items = new ArrayList<>();
        int giveAmount = Math.min(MAX_GIVE_AMOUNT, chance.getCount());
        int remaining = giveAmount;
        while (remaining > 0) {
            int stackSize = Math.min(remaining, item.getMaxStackSize());
            remaining -= stackSize;

            ItemStack builtItem = item.getItem();
            builtItem.setAmount(stackSize);
            items.add(builtItem);
        }

        // give the items to the player
        PlayerInventory inventory = recipient.getInventory();
        ItemStack[] itemArray = items.toArray(new ItemStack[0]);
        HashMap<Integer, ItemStack> lostItems = inventory.addItem(itemArray);
        if (!lostItems.isEmpty()) {
            int sum = lostItems.values().stream().map(ItemStack::getAmount).reduce(0, Integer::sum);
            giveAmount -= sum;
        }

        // notify the sender
        if (sender instanceof Player) {
            ItemizeConfig config = ItemizeConfig.getInstance();
            Component message = (sender.equals(recipient) ? config.rngGiveMessage : config.rngGiveOtherMessage).build(this.plugin,
                    recipient, (Player) sender,
                    "amount", giveAmount,
                    "item", ItemHelper.createHoverComponent(item.getDisplayItem()),
                    "chance", chance.toString());
            Messaging.send(sender, message);
        }
    }
}
