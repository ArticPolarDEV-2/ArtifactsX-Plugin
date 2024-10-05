package org.articpolardev.artifactsx.relicEvents;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

import static org.articpolardev.artifactsx.listeners.GodOfSnowRelicListener.createGodOfSnowRelic;

public class GodOfSnowRelicEvent implements Listener {
    private static final int COOLDOWN_TIME = 100; // Cooldown em ticks (5 segundos)
    private final JavaPlugin plugin;
    private final Map<Player, Long> cooldowns = new HashMap<>();

    public GodOfSnowRelicEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (isGodOfSnowRelic(item)) {
            if (isCooldownActive(player)) {
                player.sendMessage(ChatColor.RED + "Você precisa esperar antes de usar novamente!");
                return;
            }

            switch (event.getAction()) {
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:
                    // Lançar uma bola de neve na direção do jogador
                    Location eyeLocation = player.getEyeLocation();
                    Vector direction = eyeLocation.getDirection();
                    Snowball snowball = player.getWorld().spawn(eyeLocation.add(direction.multiply(1.5)), Snowball.class);
                    snowball.setVelocity(direction.multiply(1.5)); // Lançar a bola de neve
                    player.sendMessage(ChatColor.RED + "Bola de neve lançada!");

                    // Iniciar cooldown
                    startCooldown(player);
                    break;

                default:
                    break;
            }
        }
    }

    @EventHandler
    public void onSnowballHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Snowball && event.getEntity() instanceof Player target) {
            event.setDamage(4); // Ajuste o dano se necessário

            // Aplicar efeito de lentidão para simular o congelamento
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 600, 1)); // Lentidão por 30 segundos (600 ticks)

            // Adicionar efeito de entorpecimento para criar o efeito de borda visual
            target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 600, 0)); // Entorpecimento por 30 segundos (600 ticks)

            // Criar efeito de partículas de gelo
            Location targetLocation = target.getLocation();
            for (int i = 0; i < 100; i++) {
                double offsetX = (Math.random() - 0.5) * 2;
                double offsetY = (Math.random() - 0.5) * 2;
                double offsetZ = (Math.random() - 0.5) * 2;
                target.getWorld().spawnParticle(Particle.SNOWFLAKE, targetLocation.add(offsetX, offsetY, offsetZ), 1, 0.1, 0.1, 0.1, 0.1);
            }
        }
    }

    private boolean isGodOfSnowRelic(ItemStack item) {
        if (item == null) {
            return false;
        }

        ItemStack relic = createGodOfSnowRelic();

        // Verifica se os dois itens são do mesmo tipo e se têm a mesma meta
        return item.isSimilar(relic);
    }

    private boolean isCooldownActive(Player player) {
        if (cooldowns.containsKey(player)) {
            long lastUsed = cooldowns.get(player);
            long currentTime = System.currentTimeMillis();
            return (currentTime - lastUsed) < COOLDOWN_TIME * 50; // 1 tick = 50 ms
        }
        return false;
    }

    private void startCooldown(Player player) {
        cooldowns.put(player, System.currentTimeMillis());

        new BukkitRunnable() {
            @Override
            public void run() {
                cooldowns.remove(player);
            }
        }.runTaskLater(plugin, COOLDOWN_TIME);
    }
}
