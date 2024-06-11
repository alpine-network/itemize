package co.crystaldev.itemize.api;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * @since 0.1.0
 * @see org.bukkit.NamespacedKey
 */
@Getter
public final class Identifier {

    public static final String MINECRAFT = "minecraft";
    
    private final String namespace;
    
    private final String key;

    public Identifier(@NotNull String namespace, @NotNull String key) {
        this.namespace = namespace;
        this.key = key;

        Preconditions.checkArgument(isValidNamespace(this.namespace),
                "Invalid namespace. Must be [a-z0-9._-]: %s", this.namespace);
        Preconditions.checkArgument(isValidKey(this.key),
                "Invalid key. Must be [a-z0-9/._-]: %s", this.key);
    }

    public Identifier(@NotNull Plugin plugin, @NotNull String key) {
        this.namespace = plugin.getName().toLowerCase(Locale.ROOT);
        this.key = key.toLowerCase(Locale.ROOT);

        Preconditions.checkArgument(isValidNamespace(this.namespace),
                "Invalid namespace. Must be [a-z0-9._-]: %s", this.namespace);
        Preconditions.checkArgument(isValidKey(this.key),
                "Invalid key. Must be [a-z0-9/._-]: %s", this.key);
    }

    @Override
    public int hashCode() {
        int result = this.namespace.hashCode();
        result = (31 * result) + this.key.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Identifier)) {
            return false;
        }
        Identifier key = (Identifier) obj;
        return this.namespace.equals(key.namespace) && this.key.equals(key.key);
    }

    @Override
    public String toString() {
        return this.namespace + ":" + this.key;
    }

    @NotNull
    public static Identifier minecraft(@NotNull String key) {
        return new Identifier(MINECRAFT, key);
    }

    @Nullable
    public static Identifier fromString(@NotNull String string, @Nullable String defaultNamespace) {
        if (string.trim().isEmpty()) {
            return null;
        }

        String[] split = string.split(":", 3);
        if (split.length > 2) {
            return null;
        }

        String namespace = split.length == 2 ? split[0] : defaultNamespace;
        String key = split.length == 2 ? split[1] : split[0];
        if (!isValidNamespace(namespace) || !isValidKey(key)) {
            return null;
        }

        return new Identifier(namespace, key);
    }

    @Nullable
    public static Identifier fromString(@NotNull String string, @Nullable Plugin defaultNamespace) {
        return fromString(string, defaultNamespace == null ? null : defaultNamespace.getName());
    }

    @Nullable
    public static Identifier fromString(@NotNull String key) {
        return fromString(key, (String) null);
    }

    private static boolean isValidNamespaceChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '.' || c == '_' || c == '-';
    }

    private static boolean isValidKeyChar(char c) {
        return isValidNamespaceChar(c) || c == '/';
    }

    private static boolean isValidNamespace(@Nullable String namespace) {
        if (namespace == null) {
            return false;
        }

        int len = namespace.length();
        if (len == 0) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            if (!isValidNamespaceChar(namespace.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static boolean isValidKey(@Nullable String key) {
        if (key == null) {
            return false;
        }

        int len = key.length();
        if (len == 0) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            if (!isValidKeyChar(key.charAt(i))) {
                return false;
            }
        }

        return true;
    }
}
