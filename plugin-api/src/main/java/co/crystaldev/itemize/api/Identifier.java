/*
 * This file is part of Itemize - https://github.com/alpine-network/itemize
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
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
    
    private final @NotNull String namespace;
    
    private final @NotNull String key;

    public Identifier(@NotNull String namespace, @NotNull String key) {
        this.namespace = namespace;
        this.key = key;

        Preconditions.checkArgument(isValidNamespace(this.namespace),
                String.format("Invalid namespace. Must be [a-z0-9._-]: %s", this.namespace));
        Preconditions.checkArgument(isValidKey(this.key),
                String.format("Invalid key. Must be [a-z0-9/._-]: %s", this.key));
    }

    public Identifier(@NotNull Plugin plugin, @NotNull String key) {
        this.namespace = plugin.getName().toLowerCase(Locale.ROOT);
        this.key = key.toLowerCase(Locale.ROOT);

        Preconditions.checkArgument(isValidNamespace(this.namespace),
                String.format("Invalid namespace. Must be [a-z0-9._-]: %s", this.namespace));
        Preconditions.checkArgument(isValidKey(this.key),
                String.format("Invalid key. Must be [a-z0-9/._-]: %s", this.key));
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

    public static Identifier minecraft(@NotNull String key) {
        return new Identifier(MINECRAFT, key);
    }

    public static Identifier fromString(@NotNull String string, @Nullable String defaultNamespace) {
        // ensure a nonempty string was provided
        if (string.trim().isEmpty()) {
            return null;
        }

        // ensure there is only a namespace and key
        String[] split = string.split(":", 3);
        if (split.length > 2) {
            return null;
        }

        // ensure the identifier is valid
        String namespace = split.length == 2 ? split[0] : defaultNamespace;
        String key = split.length == 2 ? split[1] : split[0];
        if (!isValidNamespace(namespace) || !isValidKey(key)) {
            return null;
        }

        return new Identifier(namespace, key);
    }

    public static Identifier fromString(@NotNull String string, @Nullable Plugin defaultNamespace) {
        String pluginNamespace = defaultNamespace == null ? null : defaultNamespace.getName().toLowerCase(Locale.ROOT);
        return fromString(string, pluginNamespace);
    }

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
