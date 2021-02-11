package net.pl3x.map.plugin.api;

import net.pl3x.map.plugin.Pl3xMapPlugin;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerManager implements net.pl3x.map.api.PlayerManager {
    public static final NamespacedKey key = new NamespacedKey(Pl3xMapPlugin.getInstance(), "hidden");

    private final Set<UUID> tempHidden = new HashSet<>();

    @Override
    public void hide(final @NonNull UUID uuid, boolean persistent) {
        hide(player(uuid), persistent);
    }

    public void hide(final @NonNull Player player, boolean persistent) {
        if (persistent) {
            pdc(player).set(key, PersistentDataType.BYTE, (byte) 1);
        }
        tempHidden.add(player.getUniqueId());
    }

    @Override
    public void show(final @NonNull UUID uuid, boolean persistent) {
        show(player(uuid), persistent);
    }

    public void show(final @NonNull Player player, boolean persistent) {
        if (persistent) {
            pdc(player).set(key, PersistentDataType.BYTE, (byte) 0);
        }
        tempHidden.remove(player.getUniqueId());
    }

    @Override
    public void hidden(@NonNull UUID uuid, final boolean hide, boolean persistent) {
        hidden(player(uuid), hide, persistent);
    }

    public void hidden(@NonNull Player player, final boolean hide, boolean persistent) {
        if (hide) {
            this.hide(player, persistent);
        } else {
            this.show(player, persistent);
        }
    }

    @Override
    public boolean hidden(final @NonNull UUID uuid) {
        return hidden(player(uuid));
    }

    public boolean hidden(final @NonNull Player player) {
        return tempHidden.contains(player.getUniqueId()) ||
                pdc(player).getOrDefault(key, PersistentDataType.BYTE, (byte) 0) != (byte) 0;
    }

    private static Player player(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            throw new IllegalArgumentException("Player is not online");
        }
        return player;
    }

    private static PersistentDataContainer pdc(Player player) {
        return player.getPersistentDataContainer();
    }
}
