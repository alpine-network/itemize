package co.crystaldev.itemize.api;

import com.cryptomorin.xseries.XMaterial;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.SerializeWith;
import de.exlll.configlib.Serializer;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents a configuration item used in the Itemize system.
 *
 * @since 0.1.0
 */
@NoArgsConstructor
@Configuration @SerializeWith(serializer = ConfigItemizeItem.Adapter.class)
public final class ConfigItemizeItem {

    private Object value;

    private ItemizeItem cachedItem;

    ConfigItemizeItem(@NotNull Object value) {
        this.value = value;
    }

    /**
     * Retrieves the {@link ItemizeItem} defined by this ConfigItemizeItem.
     *
     * @return The ItemizeItem defined by this ConfigItemizeItem.
     * @throws IllegalStateException If the item type is unsupported or invalid.
     */
    @NotNull
    public ItemizeItem get() {
        if (this.cachedItem != null) {
            return this.cachedItem;
        }

        if (this.value instanceof Identifier) {
            Optional<ItemizeItem> resolved = Itemize.get().get((Identifier) this.value);
            if (resolved.isPresent()) {
                return this.cachedItem = resolved.get();
            }
        }
        else if (this.value instanceof XMaterial) {
            ItemStack itemStack = ((XMaterial) this.value).parseItem();
            if (itemStack != null) {
                return this.cachedItem = new WrappedItemStack(itemStack);
            }
        }

        throw new IllegalStateException("unsupported or invalid ItemizeItem type \"" + this.value + "\"");
    }

    /**
     * Retrieves the associated ItemStack for this item.
     *
     * @return The ItemStack representation of this item.
     * @throws IllegalStateException If the item type is unsupported or invalid.
     */
    @NotNull
    public ItemStack getItem() {
        return this.get().getItem();
    }

    /**
     * Retrieves the identifier of the item.
     *
     * @return The identifier of the item.
     * @throws IllegalStateException If the item type is unsupported or invalid.
     */
    @NotNull
    public Identifier getIdentifier() {
        if (this.value instanceof Identifier) {
            return (Identifier) this.value;
        }
        else if (this.value instanceof XMaterial) {
            return Identifier.minecraft(((XMaterial) this.value).name().toLowerCase());
        }
        else {
            throw new IllegalStateException("unsupported ItemizeItem type");
        }
    }

    /**
     * Retrieves the identifier of the item.
     *
     * @return The identifier of the item.
     * @throws IllegalStateException If the item type is unsupported or invalid.
     */
    @NotNull
    public String asString() {
        if (this.value instanceof Identifier) {
            return this.value.toString();
        }
        else if (this.value instanceof XMaterial) {
            return "minecraft:" + ((XMaterial) this.value).name().toLowerCase();
        }
        else {
            throw new IllegalStateException("unsupported ItemizeItem type");
        }
    }

    @NotNull
    public static ConfigItemizeItem of(@NotNull Identifier key) {
        return new ConfigItemizeItem(key);
    }

    @NotNull
    public static ConfigItemizeItem of(@NotNull String key) {
        if (key.startsWith("minecraft:")) {
            XMaterial resolved = XMaterial
                    .matchXMaterial(key.substring("minecraft:".length()).toUpperCase())
                    .map(v -> !v.isSupported() ? null : v)
                    .orElseThrow(() -> new IllegalStateException("Unknown or unsupported type: " + key));
            return of(resolved);
        }

        return Optional
                .ofNullable(Identifier.fromString(key))
                .map(ConfigItemizeItem::of)
                .orElseThrow(() -> new IllegalStateException("Invalid identifier: " + key));
    }

    @NotNull
    public static ConfigItemizeItem of(@NotNull XMaterial material) {
        return new ConfigItemizeItem(material);
    }

    @NotNull
    public static ConfigItemizeItem of(@NotNull Material material) {
        return of(XMaterial.matchXMaterial(material));
    }

    @NotNull
    public static ConfigItemizeItem of(@NotNull ItemStack itemStack) {
        return of(XMaterial.matchXMaterial(itemStack));
    }

    static final class Adapter implements Serializer<ConfigItemizeItem, String> {
        @Override
        public String serialize(ConfigItemizeItem element) {
            return element.asString();
        }

        @Override
        public ConfigItemizeItem deserialize(String element) {
            if (element.startsWith("minecraft:")) {
                element = element.substring("minecraft:".length());
            }

            if (!element.contains(":")) {
                Optional<XMaterial> resolved = XMaterial.matchXMaterial(element.toUpperCase());
                if (!resolved.isPresent()) {
                    throw new IllegalStateException("unable to resolve ItemizeItem type \"" + element + "\"");
                }

                return new ConfigItemizeItem(resolved.get());
            }
            else {
                Identifier key = Identifier.fromString(element);
                if (key == null) {
                    throw new IllegalStateException("unable to resolve malformed ItemizeItem key \"" + element + "\"");
                }

                return new ConfigItemizeItem(key);
            }
        }
    }
}
