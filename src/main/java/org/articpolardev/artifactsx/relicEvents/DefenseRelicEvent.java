package org.articpolardev.artifactsx.relicEvents;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Random;

import static org.articpolardev.artifactsx.listeners.DefenseRelicListener.createDefenseRelic;

public class DefenseRelicEvent implements Listener {
    private static final Random random = new Random();
    private final JavaPlugin plugin;

    public DefenseRelicEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        Entity defenderEntity = event.getEntity();

        // Verifica se a entidade atacada é um jogador ou um mob
        if (defenderEntity instanceof Player || defenderEntity instanceof LivingEntity) {
            LivingEntity defender = (LivingEntity) defenderEntity;
            ItemStack mainHandItem = Objects.requireNonNull(defender.getEquipment()).getItemInMainHand();
            ItemStack offHandItem = defender.getEquipment().getItemInOffHand();

            // Verifica se o defensor está segurando a Relíquia da Defesa na mão principal ou secundária
            if ((isDefenseRelic(mainHandItem) || isDefenseRelic(offHandItem)) && defender instanceof Player && ((Player) defender).isBlocking()) {
                Entity damager = event.getDamager();

                // Aplica dano de Thorns e bloqueia o dano de qualquer ataque
                if (damager instanceof LivingEntity) {
                    applyThornsDamage((LivingEntity) damager, 5);
                    event.setCancelled(true); // Cancela o dano recebido pelo jogador
                }

                // Verifica se o atacante é um projétil (flecha, besta, etc.)
                if (damager instanceof Projectile projectile) {

                    // Cancela o dano do projétil
                    event.setCancelled(true);

                    // Aplica dano ao atacante que lançou o projétil
                    if (projectile.getShooter() instanceof LivingEntity) {
                        applyThornsDamage((LivingEntity) projectile.getShooter(), 5);
                    }

                    // Remove o projétil do mundo
                    projectile.remove();
                }
            }
        }
    }

    // Verifica se o item é a Relíquia da Defesa
    private boolean isDefenseRelic(ItemStack item) {
        if (item == null) {
            return false;
        }

        ItemStack relic = createDefenseRelic();

        // Verifica se os dois itens são do mesmo tipo e se têm a mesma meta
        return item.isSimilar(relic);
    }

    // Método para aplicar o dano do Thorns
    private void applyThornsDamage(LivingEntity entity, int thornsLevel) {
        int chance = random.nextInt(100);
        int thornsChance = thornsLevel * 15; // Thorns V tem 75% de chance

        if (chance < thornsChance) {
            double damage = 1.0 + random.nextDouble() * 2.0; // Dano entre 1 e 3
            entity.damage(damage);
        }
    }
}
