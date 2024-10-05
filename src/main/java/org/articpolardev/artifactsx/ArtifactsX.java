package org.articpolardev.artifactsx;

import org.articpolardev.artifactsx.armorEvent.ArmorEquipEvent;
import org.articpolardev.artifactsx.commands.ClearEnderChestCommand;
import org.articpolardev.artifactsx.expurgDay.mainExpurg;
import org.articpolardev.artifactsx.listeners.*;
import org.articpolardev.artifactsx.relicEvents.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.bukkit.Bukkit.getOfflinePlayer;

//@SuppressWarnings("ALL")
public class ArtifactsX extends JavaPlugin implements Listener {
    public static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey("artifactsx", "relic_identifier");
    private final List<ItemStack> specialItems = new ArrayList<>();
    private final mainExpurg mainexpurg = new mainExpurg(this);
    private File playerDataFile;
    private FileConfiguration playerData;
    private File offlineRelicsFile;
    private FileConfiguration offlineRelicsConfig;

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("[ArtifactsX] ArtifactsX has Stopped!");
        mainexpurg.stopExpurgPlugin();
    }

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("[ArtifactsX] ArtifactsX has Started!");
        playerDataFile = new File(getDataFolder(), "playerdata.yml");
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.getParentFile().mkdirs();
                playerDataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);

        // Criando ou carregando o arquivo de relíquias offline
        offlineRelicsFile = new File(getDataFolder(), "offline_relics.yml");
        if (!offlineRelicsFile.exists()) {
            try {
                offlineRelicsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        offlineRelicsConfig = YamlConfiguration.loadConfiguration(offlineRelicsFile);

        getServer().getPluginManager().registerEvents(this, this);
        registerEvents();
        initializeSpecialItems();
        mainexpurg.startExpurgPlugin();

        Objects.requireNonNull(this.getCommand("addrelics")).setExecutor(this);
        //Objects.requireNonNull(this.getCommand("removerelic")).setExecutor(this);
        Objects.requireNonNull(this.getCommand("clearenderchest")).setExecutor(new ClearEnderChestCommand(this));
    }

    private void initializeSpecialItems() {
        // Carregue todas as relíquias aqui
        specialItems.addAll(Arrays.asList(
                WarriorRelicListener.createWarriorRelic(),
                EndRelicListener.createEndRelic(),
                SpyRelicListener.createSpyRelic(),
                EeyoreRelicListener.createEeyoreRelic(),
                OceanRelicListener.createOceanRelic(),
                MinerRelicListener.createMinerRelic(),
                HunterRelicListener.createHunterRelic(),
                InvisibilityRelicListener.createInvisibilityRelic(),
                NetherRelicListener.createNetherRelic(),
                FarmerRelicListener.createFarmerRelic(),
                InvaderRelicListener.createInvaderRelic(),
                GodOfFireRelicListener.createGodOfFireRelic(),
                ChallengeRelicListener.createChallengeRelic(),
                HungerRelicListener.createHungerRelic(),
                WoodcutterRelicListener.createWoodcutterRelic(),
                HarvestRelicListener.createHarvestRelic(),
                GodOfFishingListener.createGodOfFishingRelic(),
                BatRelicListener.createBatRelic(),
                MasterTurtleRelicListener.createMasterTurtleRelic(),
                LuckRelicListener.createLuckRelic(),
                ImmortalityRelicListener.createImmortalityRelic(),
                EndermanRelicListener.createEndermanRelic(),
                WindRelicListener.createWindRelic(),
                DefenseRelicListener.createDefenseRelic(),
                TeleportRelicListener.createTeleportRelic(),
                SniperRelicListener.createSniperRelic()));
    }


    private void registerEvents() {
        new SpyRelicListener(this);
        Bukkit.getPluginManager().registerEvents(new InvisibilityRelicEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new FarmerRelicEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new NetherRelicEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new InvaderRelicEvent(this), this);
        new GodOfFireRelicEvent(this);
        new HungerRelicEvent(this);
        new BatRelicEvent(this);
        new MasterTurtleRelicEvent(this);
        ArmorEquipEvent.registerListener(this);
        new LuckRelicEvent(this);
        Bukkit.getServer().getPluginManager().registerEvents(new ImmortalityRelicEvent(this), this);
        new EndermanRelicEvent(this);
        new WindRelicEvent(this);
        new DefenseRelicEvent(this);
        new TeleportRelicEvent(this);
        // Sistema anti-roubo comentado
        // Bukkit.getPluginManager().registerEvents(new RelicAntiTheftListener(this), this);
    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event) {
        ItemStack item = event.getEntity().getItemStack();
        if (specialItems.contains(item)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Item itemEntity) {
            ItemStack itemStack = itemEntity.getItemStack();
            if (specialItems.contains(itemStack)) {
                EntityDamageEvent.DamageCause cause = event.getCause();
                if (EnumSet.of(EntityDamageEvent.DamageCause.FIRE, EntityDamageEvent.DamageCause.LAVA, EntityDamageEvent.DamageCause.VOID, EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, EntityDamageEvent.DamageCause.BLOCK_EXPLOSION).contains(cause)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (!playerData.contains(playerUUID.toString())) {
            if (!hasRelicInEnderChest(player)) {
                ItemStack specialItem = getUniqueSpecialItem();
                if (specialItem != null) {
                    player.getEnderChest().addItem(specialItem);
                    playerData.set(playerUUID.toString(), getRelicIdentifier(specialItem));
                    savePlayerData();
                    registerRelicOwnership(specialItem);
                    player.sendMessage("§6Bem-vindo ao servidor! Você recebeu uma relíquia no seu Ender Chest.");
                } else {
                    player.sendMessage("§cTodas as relíquias já foram distribuídas!");
                }
            }
        } else {
            player.sendMessage("§6Bem-vindo de volta! Sua relíquia já foi registrada.");
        }
    }

    private void registerRelicOwnership(ItemStack relic) {
        String identifier = getRelicIdentifier(relic);
        offlineRelicsConfig.set(identifier, true);
        saveOfflineRelicsConfig();
    }

    private ItemStack getUniqueSpecialItem() {
        for (ItemStack item : specialItems) {
            String identifier = getRelicIdentifier(item);
            if (!offlineRelicsConfig.contains(identifier)) {
                return item;
            }
        }
        return null; // Todas as relíquias foram distribuídas
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("addrelics")) {
            if (sender instanceof Player player) {
                player.getEnderChest().addItem(specialItems.toArray(new ItemStack[0]));
                player.sendMessage("§aTodos os itens especiais foram adicionados ao seu Ender Chest.");
                return true;
            } else {
                sender.sendMessage("§cEste comando só pode ser executado por um jogador.");
                return false;
            }
        }

        if (command.getName().equalsIgnoreCase("removerelic")) {
            if (args.length != 1) {
                sender.sendMessage("§cUso correto: /removerelic <jogador>");
                return false;
            }

            String playerName = args[0];
            OfflinePlayer target = getOfflinePlayer(playerName);

            UUID playerUUID = target.getUniqueId();

            if (playerData.contains(playerUUID.toString())) {
                playerData.set(playerUUID.toString(), null);
                savePlayerData();
                sender.sendMessage("§aOs dados de relíquia de " + playerName + " foram removidos.");
            } else {
                sender.sendMessage("§cO jogador " + playerName + " não tem uma relíquia registrada.");
            }

            return true;
        }

        return false;
    }

    private void savePlayerData() {
        try {
            playerData.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean hasRelicInEnderChest(Player player) {
        for (ItemStack item : player.getEnderChest().getContents()) {
            if (item != null && isRelic(item)) {
                return true;
            }
        }
        return false;
    }

    private boolean isRelic(ItemStack item) {
        String identifier = getRelicIdentifier(item);
        return identifier != null && specialItems.stream().anyMatch(i -> identifier.equals(getRelicIdentifier(i)));
    }

    private void saveOfflineRelicsConfig() {
        try {
            offlineRelicsConfig.save(offlineRelicsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ItemStack getRandomSpecialItem() {
        Random random = new Random();
        return specialItems.get(random.nextInt(specialItems.size()));
    }

    private String getRelicIdentifier(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.getPersistentDataContainer().has(IDENTIFIER_KEY, PersistentDataType.STRING)) {
            return meta.getPersistentDataContainer().get(IDENTIFIER_KEY, PersistentDataType.STRING);
        }
        return null;
    }

    /*@EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        Player player = event.getPlayer();

        // Verifica se o item é uma relíquia
        if (isRelic(droppedItem)) {
            // Verifica se o Dia do Expurgo está ativo
            if (mainexpurg.isExpurgDayActived()) {
                // Permite que o jogador drope a relíquia durante o Dia do Expurgo
                player.sendMessage("§aVocê dropou uma relíquia durante o Dia do Expurgo.");
            } else {
                // Cancela o evento se o Dia do Expurgo não estiver ativo
                event.setCancelled(true);
                player.sendMessage("§cVocê não pode dropar uma relíquia fora do Dia do Expurgo!");
            }
        }
    }*/

    @EventHandler
    public void onAnvilUse(PrepareAnvilEvent event) {
        AnvilInventory anvilInventory = event.getInventory();
        ItemStack firstItem = anvilInventory.getItem(0);
        ItemStack secondItem = anvilInventory.getItem(1);

        if ((firstItem != null && isRelic(firstItem)) || (secondItem != null && isRelic(secondItem))) {
            event.setResult(null);
        }
    }

    private UUID getRelicOwner(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.getPersistentDataContainer().has(IDENTIFIER_KEY, PersistentDataType.STRING)) {
            String identifier = meta.getPersistentDataContainer().get(IDENTIFIER_KEY, PersistentDataType.STRING);
            assert identifier != null;
            return UUID.fromString(identifier); // Assumindo que o UUID do dono está armazenado como string
        }
        return null;
    }
}