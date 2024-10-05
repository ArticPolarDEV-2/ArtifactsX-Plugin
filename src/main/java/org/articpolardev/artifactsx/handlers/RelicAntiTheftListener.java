package org.articpolardev.artifactsx.handlers;

import org.articpolardev.artifactsx.expurgDay.mainExpurg;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("ALL")
public class RelicAntiTheftListener implements Listener {

    private final JavaPlugin plugin;
    private final File offlineRelicsFile;
    private final FileConfiguration offlineRelicsConfig;
    private final mainExpurg mainexpurg;

    // Mapeia relíquias para os donos
    private final Map<UUID, List<ItemStack>> playerRelics = new HashMap<>();

    public RelicAntiTheftListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.mainexpurg = new mainExpurg(plugin);

        // Criando ou carregando o arquivo offline_relics.yml
        offlineRelicsFile = new File(plugin.getDataFolder(), "offline_relics.yml");
        if (!offlineRelicsFile.exists()) {
            try {
                boolean created = offlineRelicsFile.createNewFile();
                if (created) {
                    plugin.getLogger().info("Created offline_relics.yml");
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create offline_relics.yml: " + e.getMessage());
            }
        }
        offlineRelicsConfig = YamlConfiguration.loadConfiguration(offlineRelicsFile);
    }

    @EventHandler
    public void onPlayerPickupRelic(PlayerPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        Player player = event.getPlayer();

        if (item == null || item.getType() == Material.AIR) {
            return; // Evita erros com itens inválidos
        }

        UUID ownerUUID = getRelicOwner(item);
        boolean isExpurgDayActive = mainexpurg.isExpurgDayActived();

        if (ownerUUID == null || isExpurgDayActive) {
            // Se for o "Dia do Expurgo" ou a relíquia não tiver dono, o jogador se torna o dono
            setRelicOwner(item, player.getUniqueId());
            addRelicToPlayer(player, item);
            player.sendMessage("§aVocê conquistou uma relíquia!");
        } else if (!player.getUniqueId().equals(ownerUUID)) {
            // Se o jogador não for o dono e o expurgo não estiver ativo
            if (!isExpurgDayActive) {
                // Cancelar a coleta e proteger a relíquia
                event.setCancelled(true);
                event.getItem().remove();

                // Verifica se o dono está online
                Player owner = Bukkit.getPlayer(ownerUUID);
                if (owner != null) {
                    placeRelicInEnderChest(owner, item);
                    player.sendMessage("§cVocê não pode pegar essa relíquia! Ela pertence a " + owner.getName() + ".");
                } else {
                    saveRelicForOfflinePlayer(ownerUUID, item);
                    player.sendMessage("§cVocê não pode pegar essa relíquia! Ela pertence a um jogador offline.");
                }
            }
        }
    }

    private void addRelicToPlayer(Player player, ItemStack relic) {
        UUID playerUUID = player.getUniqueId();
        playerRelics.putIfAbsent(playerUUID, new ArrayList<>());

        List<ItemStack> relics = playerRelics.get(playerUUID);
        if (relics.size() < 2) {
            relics.add(relic);
            player.sendMessage("§aVocê agora tem " + relics.size() + " relíquia(s) protegida(s).");
        } else {
            player.sendMessage("§cVocê já possui o limite de 2 relíquias protegidas.");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        List<ItemStack> relics = playerRelics.get(playerUUID);
        if (relics != null) {
            for (ItemStack relic : relics) {
                placeRelicInEnderChest(player, relic);
            }
            playerRelics.remove(playerUUID); // Remove relíquias após restaurá-las
        }
    }

    private UUID getRelicOwner(ItemStack item) {
        // Verifica no arquivo offline_relics.yml quem é o dono da relíquia
        for (String key : offlineRelicsConfig.getKeys(false)) {
            if (offlineRelicsConfig.contains(key + ".type") && offlineRelicsConfig.getString(key + ".type").equals(item.getType().name())) {
                return UUID.fromString(key); // Retorna o UUID do dono
            }
        }
        return null; // Nenhum dono encontrado
    }

    private void setRelicOwner(ItemStack item, UUID ownerUUID) {
        // Salva a relíquia no arquivo de configuração
        String path = ownerUUID.toString();
        offlineRelicsConfig.set(path + ".type", item.getType().name());
        offlineRelicsConfig.set(path + ".amount", item.getAmount());

        try {
            offlineRelicsConfig.save(offlineRelicsFile);
            plugin.getLogger().info("Relic owner set: " + ownerUUID);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save relic owner: " + e.getMessage());
        }
    }

    private void placeRelicInEnderChest(Player owner, ItemStack relic) {
        Inventory enderChest = owner.getEnderChest();
        for (int i = 0; i < enderChest.getSize(); i++) {
            if (enderChest.getItem(i) == null || Objects.requireNonNull(enderChest.getItem(i)).getType() == Material.AIR) {
                enderChest.setItem(i, relic);
                owner.sendMessage("§aSua relíquia foi retornada ao seu Baú do Fim.");
                return;
            }
        }
        owner.sendMessage("§cSeu Baú do Fim está cheio! A relíquia foi destruída.");
    }

    private void saveRelicForOfflinePlayer(UUID ownerUUID, ItemStack relic) {
        String path = ownerUUID.toString() + ".offline";
        offlineRelicsConfig.set(path + ".type", relic.getType().name());
        offlineRelicsConfig.set(path + ".amount", relic.getAmount());

        try {
            offlineRelicsConfig.save(offlineRelicsFile);
            plugin.getLogger().info("Relic saved for offline player: " + ownerUUID);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save relic for offline player: " + e.getMessage());
        }
    }
}
