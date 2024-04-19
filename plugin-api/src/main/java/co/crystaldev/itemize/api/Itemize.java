package co.crystaldev.itemize.api;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Defines the primary interface for interacting with the Itemize API.
 *
 * @since 1.0.0
 */
public interface Itemize {

    /**
     * Retrieves the singleton instance of Itemize.
     *
     * @return The Itemize instance.
     */
    @NotNull
    static Itemize get() {
        return Reference.ITEMIZE;
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
     * Retrieves an item associated with the given identifier.
     *
     * @param identifier The unique identifier for the item.
     * @return An {@link Optional} containing the {@link ItemStack} if present.
     */
    @NotNull
    Optional<ItemStack> lookup(@NotNull Identifier identifier);

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

    /**
     * Retrieves an iterable collection of all registered items.
     *
     * @return All registered items.
     */
    @NotNull
    Iterable<ItemStack> values();
}
