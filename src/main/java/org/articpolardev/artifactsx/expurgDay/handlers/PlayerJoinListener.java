package org.articpolardev.artifactsx.expurgDay.handlers;

import org.articpolardev.artifactsx.expurgDay.mainExpurg;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final mainExpurg plugin;

    public PlayerJoinListener(mainExpurg plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Se a textura estiver ativa, aplique-a para o jogador que acabou de entrar, se não estiver ativa, remove-la ao player entrar
        if (plugin.isExpurgDayActived()) {
            plugin.applyTextureToPlayer(event.getPlayer());
        } else {
            plugin.removeTextureFromPlayer(event.getPlayer());
        }
    }
}
