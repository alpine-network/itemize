package co.crystaldev.itemize.api;

import com.cryptomorin.xseries.XMaterial;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.SerializeWith;
import de.exlll.configlib.Serializer;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @since 1.0.1
 */
@NoArgsConstructor
@SerializeWith(serializer = ConfigItemizeItem.Adapter.class) @Configuration
public final class ConfigItemizeItem {

    private Object value;

    private ItemizeItem cachedItem;

    private ConfigItemizeItem(@NotNull Object value) {
        this.value = value;
    }

    @NotNull
    public ItemStack getItem() {
        if (this.cachedItem != null) {
            return this.cachedItem.getItem();
        }

        if (this.value instanceof NamespacedKey key) {
            Optional<ItemizeItem> resolved = Itemize.get().get(key);
            if (resolved.isPresent()) {
                return (this.cachedItem = resolved.get()).getItem();
            }
        }
        else if (this.value instanceof XMaterial type) {
            ItemStack itemStack = type.parseItem();
            if (itemStack != null) {
                return (this.cachedItem = new WrappedItemStack(itemStack)).getItem();
            }
        }

        throw new IllegalStateException("unsupported or invalid ItemizeItem type \"" + this.value + "\"");
    }

    @NotNull
    public static ConfigItemizeItem of(@NotNull NamespacedKey key) {
        return new ConfigItemizeItem(key);
    }

    @NotNull
    public static ConfigItemizeItem of(@NotNull String key) {
        return Optional.ofNullable(NamespacedKey.fromString(key)).map(ConfigItemizeItem::of).orElseThrow();
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
            if (element.value instanceof NamespacedKey key) {
                return key.asString();
            }
            else if (element.value instanceof XMaterial type) {
                return "minecraft:" + type.name().toLowerCase();
            }
            else {
                throw new IllegalStateException("unsupported ItemizeItem type");
            }
        }

        @Override
        public ConfigItemizeItem deserialize(String element) {
            if (element.startsWith("minecraft:")) {
                element = element.substring("minecraft:".length());
            }

            if (!element.contains(":")) {
                Optional<XMaterial> resolved = XMaterial.matchXMaterial(element.toUpperCase());
                if (resolved.isEmpty()) {
                    throw new IllegalStateException("unable to resolve ItemizeItem type \"" + element + "\"");
                }

                return new ConfigItemizeItem(resolved.get());
            }
            else {
                NamespacedKey key = NamespacedKey.fromString(element);
                if (key == null) {
                    throw new IllegalStateException("unable to resolve malformed ItemizeItem key \"" + element + "\"");
                }

                return new ConfigItemizeItem(key);
            }
        }
    }
}
