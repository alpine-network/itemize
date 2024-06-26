package co.crystaldev.itemize.api;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
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
    @NotNull
    static Itemize get() {
        return (Itemize) Bukkit.getPluginManager().getPlugin("Itemize");
    }

    /**
     * Registers an item with the given identifier.
     *
     * @param identifier The unique identifier for the item.
     * @param item       The item properties to be registered.
     */
    void register(@NotNull Identifier identifier, @NotNull ItemizeItem item);

    /**
     * Retrieves an item associated with the given identifier.
     *
     * @param identifier The unique identifier for the item.
     * @return An {@link Optional} containing the item if present.
     */
    @NotNull
    Optional<ItemizeItem> get(@NotNull Identifier identifier);

    /**
     * Retrieves the {@link Identifier} associated with the given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} to retrieve the identifier for.
     * @return The identifier if present.
     */
    @NotNull
    Optional<Identifier> get(@NotNull ItemStack itemStack);

    /**
     * Retrieves an item associated with the given identifier.
     *
     * @param identifier The unique identifier for the item.
     * @return The item if present.
     */
    @Nullable
    ItemizeItem fetch(@NotNull Identifier identifier);

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
    @NotNull
    Iterable<Identifier> keys();
}
