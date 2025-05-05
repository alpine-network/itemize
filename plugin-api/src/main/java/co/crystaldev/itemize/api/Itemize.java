package co.crystaldev.itemize.api;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Defines the primary interface for interacting with the Itemize API.
 *
 * @since 0.1.0
 */
public interface Itemize {

    /**
     * Retrieves the singleton instance of Itemize.
     *
     * @return The Itemize instance.
     */
    static @NotNull Itemize get() {
        return (Itemize) Bukkit.getPluginManager().getPlugin("Itemize");
    }

    // region Item

    /**
     * Registers an item with the given identifier.
     *
     * @param identifier The unique identifier for the item.
     * @param item       The item to be registered.
     */
    void register(@NotNull Identifier identifier, @NotNull ItemizeItem item);

    /**
     * Registers an item with the given identifier.
     *
     * @param key  The unique identifier for the item.
     * @param item The item to be registered.
     */
    default void register(@NotNull String key, @NotNull Plugin plugin, @NotNull ItemizeItem item) {
        Identifier identifier = new Identifier(plugin, key);
        this.register(identifier, item);
    }

    /**
     * Retrieves an item associated with the given identifier.
     *
     * @param identifier The unique identifier for the item.
     * @return An {@link Optional} containing the item if present.
     */
    @NotNull Optional<ItemizeItem> get(@NotNull Identifier identifier);

    /**
     * Retrieves the {@link Identifier} associated with the given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} to retrieve the identifier for.
     * @return The identifier if present.
     */
    @NotNull Optional<Identifier> get(@NotNull ItemStack itemStack);

    /**
     * Retrieves an item associated with the given identifier.
     *
     * @param identifier The unique identifier for the item.
     * @return The item if present.
     */
    @Nullable ItemizeItem fetch(@NotNull Identifier identifier);

    /**
     * Checks if the item associated with the given identifier matches the provided item.
     *
     * @param item The {@link ItemStack} to check.
     * @return Whether the provided item matches the registered item.
     */
    boolean matches(@NotNull Identifier identifier, @NotNull ItemStack item);

    /**
     * Checks if there is an item registered with the given identifier.
     *
     * @param identifier The unique identifier to query.
     * @return Whether there is an item associated with the provided identifier.
     */
    boolean contains(@NotNull Identifier identifier);

    /**
     * Retrieves an iterable collection of all registered item identifiers.
     *
     * @return All registered item identifiers.
     */
    @NotNull Iterable<Identifier> keys();

    // endregion Item

    // region Reward

    /**
     * Registers a reward with the given identifier.
     *
     * @param identifier The unique identifier for the reward.
     * @param reward     The reward to be registered.
     */
    void register(@NotNull Identifier identifier, @NotNull ItemizeReward reward);

    /**
     * Retrieves the unique identifier associated with the given reward.
     *
     * @param reward The {@link ItemizeReward} to retrieve the identifier for.
     * @return The identifier if present.
     */
    @NotNull Optional<Identifier> getReward(@NotNull ItemizeReward reward);

    /**
     * Retrieves a reward associated with the given identifier.
     *
     * @param identifier The unique identifier for the reward.
     * @return An {@link Optional} containing the reward if present.
     */
    @NotNull Optional<ItemizeReward> getReward(@NotNull Identifier identifier);

    /**
     * Retrieves a reward associated with the given identifier.
     *
     * @param identifier The unique identifier for the reward.
     * @return The reward if present.
     */
    @Nullable ItemizeReward fetchReward(@NotNull Identifier identifier);

    /**
     * Checks if there is a reward registered with the given identifier.
     *
     * @param identifier The unique identifier to query.
     * @return Whether there is a reward associated with the provided identifier.
     */
    boolean containsReward(@NotNull Identifier identifier);

    /**
     * Retrieves an iterable collection of all registered reward identifiers.
     *
     * @return All registered reward identifiers.
     */
    @NotNull Iterable<Identifier> rewardKeys();

    // endregion Reward
}
