package org.articpolardev.artifactsx.relicEvents;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static org.articpolardev.artifactsx.listeners.LuckRelicListener.createLuckRelic;

public class LuckRelicEvent implements Listener {
    public LuckRelicEvent(JavaPlugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());

        // Verifica se a nova item na mão secundária é a Luck Relic
        if (isLuckRelic(newItem)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, Integer.MAX_VALUE, 10, false, false));
        }

        // Se o jogador não está segurando a Luck Relic, remova o efeito
        if (isLuckRelic(oldItem)) {
            player.removePotionEffect(PotionEffectType.LUCK);
        }
    }

    private boolean isLuckRelic(ItemStack item) {
        if (item == null) {
            return false;
        }

        ItemStack relic = createLuckRelic();

        // Verifica se os dois itens são do mesmo tipo e se têm a mesma meta
        return item.isSimilar(relic);
    }
}
