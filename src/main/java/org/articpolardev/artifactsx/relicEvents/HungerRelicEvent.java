package org.articpolardev.artifactsx.relicEvents;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import static org.articpolardev.artifactsx.listeners.HungerRelicListener.createHungerRelic;

public class HungerRelicEvent implements Listener {

    private final Plugin plugin;

    public HungerRelicEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();

        if (isHungerRelic(item)) {
            // Recuperar toda a fome do jogador
            event.getPlayer().setFoodLevel(20);
            event.getPlayer().sendMessage("§aSua fome foi completamente restaurada!");

            // Impedir que o item seja consumido
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if (event.getHand() == EquipmentSlot.HAND) {
                    event.getPlayer().getInventory().setItemInMainHand(createHungerRelic());
                } else if (event.getHand() == EquipmentSlot.OFF_HAND) {
                    event.getPlayer().getInventory().setItemInOffHand(createHungerRelic());
                }
            }, 0L);
        }
    }

    private boolean isHungerRelic(ItemStack item) {
        if (item == null) {
            return false;
        }

        ItemStack relic = createHungerRelic();

        // Verifica se os dois itens são do mesmo tipo e se têm a mesma meta
        return item.isSimilar(relic);
    }
}