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

import static org.articpolardev.artifactsx.listeners.NetherRelicListener.createNetherRelic;

public class NetherRelicEvent implements Listener {
    private final JavaPlugin plugin;

    public NetherRelicEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        ItemStack newBoots = event.getNewArmorPiece();
        ItemStack oldBoots = event.getOldArmorPiece();

        // Verifica se as botas invisíveis foram equipadas
        if (newBoots != null && newBoots.isSimilar(createNetherRelic())) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1, false, false));

        }

        // Verifica se as botas invisíveis foram desequipadas
        if (oldBoots != null && oldBoots.isSimilar(createNetherRelic())) {
            player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        }
    }

    /* @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
    } */
}
