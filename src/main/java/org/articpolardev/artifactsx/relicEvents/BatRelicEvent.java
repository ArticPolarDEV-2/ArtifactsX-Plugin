package org.articpolardev.artifactsx.relicEvents;

import org.articpolardev.artifactsx.armorEvent.ArmorEquipEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static org.articpolardev.artifactsx.listeners.BatRelicListener.createBatRelic;

public class BatRelicEvent implements Listener {
    public BatRelicEvent(JavaPlugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        ItemStack newLeggings = event.getNewArmorPiece();
        ItemStack oldLeggings = event.getOldArmorPiece();

        if (newLeggings != null && newLeggings.isSimilar(createBatRelic())) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 5, false, false));
        }
        if (oldLeggings != null && oldLeggings.isSimilar(createBatRelic())) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }
}
