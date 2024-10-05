package org.articpolardev.artifactsx.relicEvents;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import static org.articpolardev.artifactsx.listeners.WindRelicListener.createWindRelic;

public class WindRelicEvent implements Listener {
    private final JavaPlugin plugin;

    public WindRelicEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if (isWindRelic(item)) {
            // Atrasar a substituição do item para evitar que ele seja consumido
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if (event.getHand() == EquipmentSlot.HAND) {
                    event.getPlayer().getInventory().setItemInMainHand(createWindRelic());
                } else if (event.getHand() == EquipmentSlot.OFF_HAND) {
                    event.getPlayer().getInventory().setItemInOffHand(createWindRelic());
                }
                // Remover cooldown apenas após a substituição do item
                event.getPlayer().setCooldown(item.getType(), 0); // Remover cooldown
            }, 2L); // Atraso de 2 ticks para evitar problemas com a remoção do item
        }
    }

    private boolean isWindRelic(ItemStack item) {
        if (item == null) {
            return false;
        }

        ItemStack relic = createWindRelic();

        // Verifica se os dois itens são do mesmo tipo e se têm a mesma meta
        return item.isSimilar(relic);
    }
}
