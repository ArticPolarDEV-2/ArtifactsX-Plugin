package org.articpolardev.artifactsx.relicEvents;

import org.articpolardev.artifactsx.menus.TeleportRelicMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import static org.articpolardev.artifactsx.listeners.TeleportRelicListener.createTeleportRelic;

public class TeleportRelicEvent implements Listener {
    private final JavaPlugin plugin;

    public TeleportRelicEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (isTeleportRelic(item)) {
            switch (event.getAction()) {
                case RIGHT_CLICK_BLOCK:
                    break;
                case LEFT_CLICK_AIR:
                case LEFT_CLICK_BLOCK:
                    // Lançar todas as Fireballs no raio de 1 chunk (16 blocos)
                    new TeleportRelicMenu(plugin, player);
                    break;

                case RIGHT_CLICK_AIR:
                    break;
                case PHYSICAL:
                    break;
                default:
                    break;
            }
        }
    }

    private boolean isTeleportRelic(ItemStack item) {
        if (item == null) {
            return false;
        }

        ItemStack relic = createTeleportRelic();

        // Verifica se os dois itens são do mesmo tipo e se têm a mesma meta
        return item.isSimilar(relic);
    }
}
