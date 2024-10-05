package org.articpolardev.artifactsx.relicEvents;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.articpolardev.artifactsx.listeners.ImmortalityRelicListener.createImmortalityRelic;

public class ImmortalityRelicEvent implements Listener {

    private final JavaPlugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>(); // Armazena o cooldown por UUID do jogador

    public ImmortalityRelicEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();

        boolean mainHandRelic = isImmortalityRelic(itemInMainHand);
        boolean offHandRelic = isImmortalityRelic(itemInOffHand);

        if (!mainHandRelic && !offHandRelic) {
            return;
        }

        UUID playerId = player.getUniqueId();

        // Verifica se a relíquia está em cooldown
        if (isInCooldown(playerId)) {
            player.sendMessage("§cA Relíquia da Imortalidade está em cooldown e não pôde salvar você!");
            return;
        }

        double finalDamage = event.getFinalDamage();

        // Verifica se o dano resultaria na morte do jogador
        if (player.getHealth() - finalDamage > 0) {
            return;
        }

        // Cancela o evento de dano para evitar a morte
        event.setCancelled(true);

        // Restaura a vida do jogador para evitar que ele fique com 0 de vida
        player.setHealth(1.0); // Define a vida do jogador para 1

        // Simula a animação do Totem da Imortalidade
        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation(), 100, 1, 1, 1);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 1.0f);

        // Aplica os efeitos do Totem da Imortalidade
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1, true, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 1, true, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 40, 0, true, true));

        // Coloca a relíquia em cooldown
        startCooldown(playerId, 20 * 20); // 20 segundos
    }

    @EventHandler
    public void onPlayerResurrect(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();

        // Verifica se o item na mão principal ou offhand é a relíquia e se está em cooldown
        if (isImmortalityRelic(itemInMainHand) || isImmortalityRelic(itemInOffHand)) {
            // Cancela a ressurreição se a relíquia estiver em cooldown
            event.setCancelled(true);
            player.sendMessage("§cA Relíquia da Imortalidade está em cooldown e não pode ser usada para ressuscitar!");
        }
    }

    private boolean isImmortalityRelic(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemStack relic = createImmortalityRelic();

        return item.isSimilar(relic);
    }

    private boolean isInCooldown(UUID playerId) {
        return cooldowns.containsKey(playerId) && cooldowns.get(playerId) > System.currentTimeMillis();
    }

    private void startCooldown(UUID playerId, int cooldownTicks) {
        long cooldownEndTime = System.currentTimeMillis() + (cooldownTicks * 50L); // Converte ticks para milissegundos
        cooldowns.put(playerId, cooldownEndTime);

        // Notifica o jogador quando o cooldown termina
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            cooldowns.remove(playerId);
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.sendMessage("§aA Relíquia da Imortalidade voltou ao normal!");
            }
        }, cooldownTicks);
    }
}
