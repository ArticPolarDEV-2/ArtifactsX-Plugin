package org.articpolardev.artifactsx.relicEvents;

import org.articpolardev.artifactsx.armorEvent.ArmorEquipEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static org.articpolardev.artifactsx.listeners.InvisibilityRelicListener.createInvisibilityRelic;

public class InvisibilityRelicEvent implements Listener {
    private final JavaPlugin plugin;

    public InvisibilityRelicEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        ItemStack newBoots = event.getNewArmorPiece();
        ItemStack oldBoots = event.getOldArmorPiece();
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        // Verifica se as botas invisíveis foram equipadas
        if (newBoots != null && newBoots.isSimilar(createInvisibilityRelic())) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));
            // Esconder Armadura
            String hideCommand = "hiddenarmor:hiddenarmor hide" + player.getName();
            Bukkit.dispatchCommand(console, hideCommand);
        }

        // Verifica se as botas invisíveis foram desequipadas
        if (oldBoots != null && oldBoots.isSimilar(createInvisibilityRelic())) {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            // Mostrar Armadura
            String showCommand = "hiddenarmor:hiddenarmor show" + player.getName();
            Bukkit.dispatchCommand(console, showCommand);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
    }
}
