package org.articpolardev.artifactsx.expurgDay;

import org.articpolardev.artifactsx.expurgDay.commands.ExpurgCommands;
import org.articpolardev.artifactsx.expurgDay.handlers.PlayerJoinListener;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("ALL")
public class mainExpurg implements Listener {
    private static final long SECONDS_IN_HOUR = 3600; // Segundos em uma hora
    private static final long SECONDS_IN_DAY = 12 * SECONDS_IN_HOUR; // 12 horas por padrão
    private final Set<Item> processedItems = new HashSet<>(); // Para evitar a detecção múltipla
    public JavaPlugin plugin;
    private File activationFile; // Inicializado corretamente no startExpurgPlugin()
    public boolean isExpurgDayActived = false;
    private BossBar expurgBossBar;
    private long remainingTime; // Tempo restante em segundos
    private long currentMaxDuration; // Máximo de tempo configurado para a BossBar ativa
    private long limitTime; // Tempo máximo permitido pelo config.yml (definido no onEnable)
    private BukkitRunnable countdownTask;

    public mainExpurg(JavaPlugin plg) {
        this.plugin = plg;
    }

    public void startExpurgPlugin() {
        Bukkit.getServer().getLogger().info("[ExpurgDay] ExpurgDay has been loaded!");

        // Restaura o estado anterior do expurgo (se o servidor fechar durante o expurgo)
        restoreExpurgState();

        activationFile = new File(plugin.getDataFolder(), "activated_players.txt");
        if (!activationFile.exists()) {
            try {
                activationFile.getParentFile().mkdirs();
                activationFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getLogger().severe("Não foi possível criar o arquivo de jogadores ativados!");
            }
        }

        plugin.saveDefaultConfig();
        limitTime = plugin.getConfig().getLong("limit") * SECONDS_IN_HOUR; // Carregar limite de tempo do config
        if (limitTime <= 0) {
            limitTime = SECONDS_IN_DAY; // Defina um valor padrão se o valor no config for inválido
        }

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), plugin);
        Objects.requireNonNull(this.plugin.getCommand("expurgtime")).setExecutor(new ExpurgCommands(this));
        Objects.requireNonNull(this.plugin.getCommand("isexpurgday")).setExecutor(new ExpurgCommands(this));
    }

    public void stopExpurgPlugin() {
        saveExpurgState(); // Salva o estado do expurgo
        Bukkit.getServer().getLogger().info("[ExpurgDay] ExpurgDay has been unloaded!");
    }

    // Aplica o pacote de textura a um jogador específico
    public void applyTextureToPlayer(Player player) {
        String textureJavaLink = "https://raw.githubusercontent.com/ArticPolarDEV-2/ExpurgDay-Spigot/main/BloodMoonJava.zip";
        String hash = "66e031dc095d05c00ae637ca07bd1393f22f201e";
        byte[] hashBytes = convertHexToByteArray(hash);
        player.setResourcePack(textureJavaLink, hashBytes);  // Adiciona a textura

        // Usa um atraso para garantir que a vinheta desapareça antes de mostrar a mensagem
        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendTitle(
                        "§c☠ DIA DO EXPURGO ATIVADO ☠",   // Título grande, em vermelho com o emoji de caveira
                        "§fO SERVER ESTÁ SEM LEIS",        // Texto menor, em branco claro
                        10,  // Fade in
                        70,  // Tempo visível
                        20   // Fade out
                );
            }
        }.runTaskLater(plugin, 140L); // Atraso de 140 ticks (7 segundos)
    }

    // Remove o pacote de textura de um jogador específico
    public void removeTextureFromPlayer(Player player) {
        String defaultTexture = "https://raw.githubusercontent.com/ArticPolarDEV-2/ExpurgDay-Spigot/main/EmptyTexture.zip";
        player.setResourcePack(defaultTexture); // Define a textura vazia
    }

    private void activateEternalNight() {
        for (World world : Bukkit.getWorlds()) {
            world.setTime(18000L); // Define a hora para a noite
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        }
    }

    private void deactivateEternalNight() {
        for (World world : Bukkit.getWorlds()) {
            world.setTime(0);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        }
    }

    private byte[] convertHexToByteArray(String hash) {
        int length = hash.length();
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hash.charAt(i), 16) << 4)
                    + Character.digit(hash.charAt(i + 1), 16));
        }
        return data;
    }

    public boolean isExpurgDayActived() {
        return isExpurgDayActived;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        // Verifica se o inventário aberto é um Ender Chest
        if (event.getInventory().getType() == InventoryType.ENDER_CHEST && isExpurgDayActived) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("Você não pode abrir o Ender Chest no momento!");
        }
    }

    @EventHandler
    private void onItemBurn(EntityCombustByBlockEvent e) throws IOException {
        if (e.getEntity() instanceof Item itemEntity) {
            if (processedItems.contains(itemEntity)) return;

            processedItems.add(itemEntity);

            ItemStack itemStack = itemEntity.getItemStack();
            UUID playerUUID = itemEntity.getThrower(); // Pega o UUID do jogador que jogou o item

            if (playerUUID == null) {
                Bukkit.getLogger().warning("Nenhum jogador associado ao item.");
                return;
            }

            if (itemStack.getType() == Material.NETHER_STAR) {
                e.setCancelled(true);
                handleNetherStarBurn(playerUUID, itemEntity);
            } else if (itemStack.getType() == Material.WITHER_SKELETON_SKULL) {
                e.setCancelled(true);
                handleWitherSkullBurn();
            }

            itemEntity.remove(); // Remove o item queimado
        }
    }

    private void handleNetherStarBurn(UUID playerUUID, Item itemEntity) throws IOException {
        if (!hasPlayerActivatedExpurgo(playerUUID.toString())) {
            remainingTime = Math.min(remainingTime + SECONDS_IN_DAY, limitTime);

            // Tempo que o jogador ativou a BossBar
            currentMaxDuration = remainingTime;
            setPlayerActivatedExpurgo(playerUUID.toString());

            if (!isExpurgDayActived) {
                Location burnLocation = itemEntity.getLocation();
                startBossBar();
                activateTextureForAllPlayers();
                playEnderDragonSound(burnLocation);
                summonLighting(burnLocation);
                activateEternalNight();
                Bukkit.getLogger().info("Dia do Expurgo ativado!");
            } else {
                updateBossBar();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle("§c☠ DIA DO EXPURGO ☠", "§fO TEMPO FOI EXTENDIDO", 10, 70, 20);
                }
            }
        } else if (isExpurgDayActived) {
            remainingTime = Math.min(remainingTime + SECONDS_IN_DAY * 2, limitTime);
            updateBossBar();
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle("§c☠ DIA DO EXPURGO ☠", "§fO TEMPO FOI EXTENDIDO", 10, 70, 20);
            }
        }
    }

    private void handleWitherSkullBurn() {
        if (isExpurgDayActived) {
            remainingTime = Math.max(remainingTime - SECONDS_IN_HOUR, 0);
            updateBossBar();

            if (remainingTime <= 0) {
                removeBossBar();
                deactivateTextureForAllPlayers();
                deactivateEternalNight();
                Bukkit.getLogger().info("Tempo do Expurgo zerado!");
            } else {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendTitle("§c☠ DIA DO EXPURGO ☠", "§fO TEMPO FOI REDUZIDO", 10, 70, 20);
                }
            }
        }
    }

    private void startCountdown() {
        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                remainingTime--;

                if (remainingTime <= 0) {
                    removeBossBar();
                    deactivateTextureForAllPlayers();
                    deactivateEternalNight();
                    clearTabFooterForAllPlayers();
                    cancel(); // Cancela a tarefa
                    return;
                }

                updatePlayerTabTimes();
                updateBossBar();
            }
        };

        countdownTask.runTaskTimer(plugin, 0L, 20L); // Executa a cada segundo (20 ticks)
    }

    private void startBossBar() {
        expurgBossBar = Bukkit.createBossBar(
                "§c☠ DIA DO EXPURGO ATIVADO ☠",
                BarColor.RED,
                BarStyle.SEGMENTED_10
        );

        for (Player p : Bukkit.getOnlinePlayers()) {
            expurgBossBar.addPlayer(p);
        }

        expurgBossBar.setProgress(1.0);
        isExpurgDayActived = true;
        startCountdown();
    }

    private void updateBossBar() {
        if (expurgBossBar != null) {
            double remainingFraction = (double) remainingTime / currentMaxDuration;
            expurgBossBar.setProgress(Math.max(0.0, Math.min(1.0, remainingFraction)));
        }
    }

    private void removeBossBar() {
        if (expurgBossBar != null) {
            expurgBossBar.removeAll();
            expurgBossBar = null;
        }
        isExpurgDayActived = false;
        if (countdownTask != null) {
            countdownTask.cancel();
        }
        clearTabFooterForAllPlayers(); // Limpa o rodapé aqui também, caso o expurgo termine por outro motivo
    }

    private void setPlayerActivatedExpurgo(String playerUUID) throws IOException {
        List<String> lines = Files.readAllLines(activationFile.toPath());
        lines.add(playerUUID);
        Files.write(activationFile.toPath(), lines);
    }

    private boolean hasPlayerActivatedExpurgo(String playerUUID) throws IOException {
        List<String> lines = Files.readAllLines(activationFile.toPath());
        return lines.contains(playerUUID);
    }

    private void summonLighting(Location location) {
        Objects.requireNonNull(location.getWorld()).strikeLightningEffect(location);
    }

    private void playEnderDragonSound(Location location) {
        Objects.requireNonNull(location.getWorld()).playSound(location, Sound.ENTITY_ENDER_DRAGON_DEATH, 10.0F, 0.8F);
    }

    public void activateTextureForAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            applyTextureToPlayer(player);
        }
    }

    public void deactivateTextureForAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            removeTextureFromPlayer(player);
        }
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    // Método para formatar o tempo restante em horas, minutos e segundos
    private String formatRemainingTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    // Método para atualizar o tempo restante no tab de todos os jogadores
    private void updatePlayerTabTimes() {
        String remainingTimeFormatted = formatRemainingTime(remainingTime);
        String tabFooter = "§c☠ Expurgo Ativado ☠\n§fTempo Restante: §e" + remainingTimeFormatted;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setPlayerListHeaderFooter(null, tabFooter); // Atualiza apenas o rodapé
        }
    }

    // Chama esse método no onEnable ou quando necessário para limpar o rodapé ao término do expurgo
    private void clearTabFooterForAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setPlayerListHeaderFooter(null, null); // Remove o rodapé quando o Expurgo termina
        }
    }

    public void saveExpurgState() {
        File stateFile = new File(plugin.getDataFolder(), "state.yml");

        try {
            if (!stateFile.exists()) {
                stateFile.getParentFile().mkdirs();
                stateFile.createNewFile();
            }

            FileWriter writer = new FileWriter(stateFile);
            writer.write("isExpurgDayActived: " + isExpurgDayActived + "\n");
            writer.write("remainingTime: " + remainingTime + "\n");
            writer.write("currentMaxDuration: " + currentMaxDuration + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restoreExpurgState() {
        File stateFile = new File(plugin.getDataFolder(), "state.yml");

        if (stateFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(stateFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(": ");
                    if (parts.length == 2) {
                        switch (parts[0]) {
                            case "isExpurgDayActived":
                                isExpurgDayActived = Boolean.parseBoolean(parts[1]);
                                break;
                            case "remainingTime":
                                remainingTime = Long.parseLong(parts[1]);
                                break;
                            case "currentMaxDuration":
                                currentMaxDuration = Long.parseLong(parts[1]);
                                break;
                        }
                    }
                }

                // Se o expurgo estava ativado, restaure os estados visuais
                if (isExpurgDayActived) {
                    startBossBar(); // Recria a BossBar
                    activateTextureForAllPlayers(); // Aplica a textura novamente
                    activateEternalNight(); // Ativa a noite eterna novamente
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
