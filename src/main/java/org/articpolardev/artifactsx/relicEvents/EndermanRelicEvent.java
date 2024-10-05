package org.articpolardev.artifactsx.relicEvents;

import org.bukkit.Bukkit;
import org.bukkit.entity.Endermite;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import static org.articpolardev.artifactsx.listeners.EndermanRelicListener.createEndermanRelic;

public class EndermanRelicEvent implements Listener {
    private final JavaPlugin plugin;

    public EndermanRelicEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if (isEndermanRelic(item)) {
            // Impedir que o item seja consumido
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if (event.getHand() == EquipmentSlot.HAND) {
                    event.getPlayer().getInventory().setItemInMainHand(createEndermanRelic());
                } else if (event.getHand() == EquipmentSlot.OFF_HAND) {
                    event.getPlayer().getInventory().setItemInOffHand(createEndermanRelic());
                }
                event.getPlayer().setCooldown(item.getType(), 0); // Remover cooldown
            }, 1L);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        // Verifica se o teletransporte foi causado pelo uso de uma Ender Pearl
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            event.setCancelled(false); // Permite o teletransporte sem causar dano

            // Remove o Endermite se ele for gerado
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                event.getPlayer().getWorld().getEntitiesByClass(Endermite.class).forEach(Endermite::remove);
            }, 1L);
        }
    }

    private boolean isEndermanRelic(ItemStack item) {
        if (item == null) {
            return false;
        }

        ItemStack relic = createEndermanRelic();

        // Verifica se os dois itens são do mesmo tipo e se têm a mesma meta
        return item.isSimilar(relic);
    }
}
