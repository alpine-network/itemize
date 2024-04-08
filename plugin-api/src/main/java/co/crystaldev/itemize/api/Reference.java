package co.crystaldev.itemize.api;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @since 1.0.0
 */
@UtilityClass
final class Reference {
    @NotNull
    static final Itemize ITEMIZE = Optional
            .ofNullable((Itemize) Bukkit.getPluginManager().getPlugin("Itemize"))
            .orElseThrow(() -> new IllegalStateException("Attempted to access Itemize prior to initialization"));
}
