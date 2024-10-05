package org.articpolardev.artifactsx.relicEvents;

import org.articpolardev.artifactsx.armorEvent.ArmorEquipEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static org.articpolardev.artifactsx.listeners.InvaderRelicListener.createInvaderRelic;

public class InvaderRelicEvent implements Listener {
    public InvaderRelicEvent(JavaPlugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        ItemStack newLeggings = event.getNewArmorPiece();
        ItemStack oldLeggings = event.getOldArmorPiece();

        if (newLeggings != null && newLeggings.isSimilar(createInvaderRelic())) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, Integer.MAX_VALUE, 5, false, false));
        }
        if (oldLeggings != null && oldLeggings.isSimilar(createInvaderRelic())) {
            player.removePotionEffect(PotionEffectType.BAD_OMEN);
        }
    }

    @EventHandler
    public void onEntityTargetPlayer(EntityTargetLivingEntityEvent event) {
        // Verifica se a entidade alvo é um jogador
        if (event.getTarget() instanceof Player player) {
            ItemStack leggings = player.getInventory().getLeggings();

            // Verifica se o jogador está equipado com a Relíquia do Invasor
            if (leggings != null && leggings.isSimilar(createInvaderRelic())) {
                // Verifica se a entidade atacante é um Pillager ou Evoker
                EntityType entityType = event.getEntityType();
                if (entityType == EntityType.PILLAGER || entityType == EntityType.EVOKER || entityType == EntityType.VEX || entityType == EntityType.VINDICATOR || entityType == EntityType.ILLUSIONER || entityType == EntityType.RAVAGER) {
                    // Cancela o evento para impedir que a entidade mire no jogador
                    event.setCancelled(true);
                }
            }
        }
    }
}
