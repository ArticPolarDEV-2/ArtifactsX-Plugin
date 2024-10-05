package org.articpolardev.artifactsx.listeners;

import org.articpolardev.artifactsx.handlers.NameFormat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpyRelicListener implements Listener {
    private static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey("artifactsx", "relic_identifier");
    private static final String tag = "spy";
    private final JavaPlugin plugin;
    private final Team hiddenNameTagTeam;

    public SpyRelicListener(JavaPlugin plugin) {
        this.plugin = plugin;
        Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();

        // Tentativa de obter a equipe existente
        Team existingTeam = scoreboard.getTeam("hidden_names");

        // Se a equipe não existir, criamos uma nova
        if (existingTeam == null) {
            this.hiddenNameTagTeam = scoreboard.registerNewTeam("hidden_names");
            this.hiddenNameTagTeam.setNameTagVisibility(NameTagVisibility.NEVER);
        } else {
            this.hiddenNameTagTeam = existingTeam;
        }
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static boolean isSpecialCookie(ItemStack item) {
        if (item == null) {
            return false;
        }

        ItemStack relic = createSpyRelic();

        // Verifica se os dois itens são do mesmo tipo e se têm a mesma meta
        return item.isSimilar(relic);
    }

    public static ItemStack createSpyRelic() {
        ItemStack cookie = new ItemStack(Material.COOKIE);
        ItemMeta meta = cookie.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(IDENTIFIER_KEY, PersistentDataType.STRING, tag);
            meta.addEnchant(Enchantment.UNBREAKING, 3, true);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

            // Descrição do Item
            List<String> lore = new ArrayList<>();
            lore.add("§7Indestrutível");
            meta.setLore(lore);
            String FName = NameFormat.RelicNameFormat("#ff9900", "Relíquia do ESPIÃO");
            meta.setDisplayName(FName);
            cookie.setItemMeta(meta);
        }
        return cookie;
    }

    private void hidePlayerName(Player player) {
        // Adiciona o jogador ao Team que oculta os nomes
        hiddenNameTagTeam.addEntry(player.getName());
    }

    private void showPlayerName(Player player) {
        // Remove o jogador do Team para mostrar o nome novamente
        hiddenNameTagTeam.removeEntry(player.getName());
    }

    private void applyShrunkenEffect(Player player) {
        // Marca o jogador como encolhido
        player.setMetadata("shrunken", new FixedMetadataValue(plugin, true));

        // Esconder Nick
        hidePlayerName(player);

        // Encolher o jogador via comando
        String command = "attribute " + player.getName() + " minecraft:generic.scale base set 0.01";
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        Bukkit.dispatchCommand(console, command);

        // Agenda a remoção do efeito após 8 minutos
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // Restaurar tamanho original
            String resetCommand = "attribute " + player.getName() + " minecraft:generic.scale base set 1.0";
            Bukkit.dispatchCommand(console, resetCommand);
            player.removeMetadata("shrunken", plugin);

            // Mostrar Nick novamente
            showPlayerName(player);

            player.sendMessage("§aVocê voltou ao tamanho normal.");
        }, 8 * 60 * 20L); // 8 minutos
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player player) {

            // Verifica se o jogador está encolhido
            if (player.hasMetadata("shrunken")) {
                // Cancela o alvo do mob no jogador encolhido
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        if (isSpecialCookie(event.getItem())) {
            event.setCancelled(true); // Impede que o cookie seja consumido
            Player player = event.getPlayer();

            if (!player.hasMetadata("shrunken")) {
                applyShrunkenEffect(player);
                event.getPlayer().sendMessage("§aVocê comeu a §bRelíquia do Espião§a! Agora você está extremamente pequeno por 8 minutos. Para voltar ao tamanho normal, beba um balde de leite.");
            } else {
                event.getPlayer().sendMessage("§cVocê já está encolhido!");
            }
        }
    }

    @EventHandler
    public void onPlayerDrink(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        // Verifica se o jogador está bebendo um balde de leite
        if (event.getItem().getType() == Material.MILK_BUCKET) {
            // Verifica se o jogador está encolhido
            if (player.hasMetadata("shrunken")) {
                player.removeMetadata("shrunken", plugin);

                // Restaurar tamanho original
                String resetCommand = "attribute " + player.getName() + " minecraft:generic.scale base set 1.0";
                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                Bukkit.dispatchCommand(console, resetCommand);

                // Mostrar Nick novamente
                showPlayerName(player);

                player.sendMessage("§aVocê bebeu um balde de leite e voltou ao tamanho normal.");
            }
        }
    }
}
