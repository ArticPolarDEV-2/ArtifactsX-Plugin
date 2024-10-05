package org.articpolardev.artifactsx.relicEvents;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import static org.articpolardev.artifactsx.listeners.GodOfFireRelicListener.createGodOfFireRelic;

public class GodOfFireRelicEvent implements Listener {

    public GodOfFireRelicEvent(JavaPlugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (isGodOfFireRelic(item)) {
            switch (event.getAction()) {
                case RIGHT_CLICK_AIR:
                    // Spawnar e lançar uma Fireball imediatamente
                    Location eyeLocation = player.getEyeLocation();
                    Vector direction = eyeLocation.getDirection();
                    Fireball fireball = player.getWorld().spawn(eyeLocation.add(direction.multiply(1.5)), Fireball.class);
                    fireball.setShooter(player);
                    fireball.setVelocity(direction.multiply(2)); // Lançar imediatamente com velocidade ajustada
                    break;

                default:
                    break;
            }
        }
    }

    private boolean isGodOfFireRelic(ItemStack item) {
        if (item == null) {
            return false;
        }

        ItemStack relic = createGodOfFireRelic();

        // Verifica se os dois itens são do mesmo tipo e se têm a mesma meta
        return item.isSimilar(relic);
    }
}
