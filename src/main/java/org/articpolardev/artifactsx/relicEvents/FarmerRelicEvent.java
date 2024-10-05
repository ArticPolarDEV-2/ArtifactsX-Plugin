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

import static org.articpolardev.artifactsx.listeners.FarmerRelicListener.createFarmerRelic;

public class FarmerRelicEvent implements Listener {
    private final JavaPlugin plugin;

    public FarmerRelicEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        ItemStack newLeggings = event.getNewArmorPiece();
        ItemStack oldLeggings = event.getOldArmorPiece();

        if (newLeggings != null && newLeggings.isSimilar(createFarmerRelic())) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, Integer.MAX_VALUE, 1, false, false));
        }
        if (oldLeggings != null && oldLeggings.isSimilar(createFarmerRelic())) {
            player.removePotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE);
        }
    }
}
