package org.articpolardev.artifactsx.menus;

import org.articpolardev.artifactsx.handlers.dbTPConnect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TeleportRelicMenu implements Listener {
    private final String invName = ChatColor.RED + "Relíquia do TELETRANSPORTE";
    private final dbTPConnect tpconn;
    private final JavaPlugin plugin;
    private int[] selectedWarp; // Armazena o warp selecionado

    public TeleportRelicMenu(JavaPlugin plugin, Player player) {
        this.plugin = plugin;
        tpconn = new dbTPConnect();
        try {
            tpconn.connect();
            tpconn.createTable(); // Cria a tabela se ainda não existir
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getLogger().severe("Não foi possível conectar ao banco de dados!");
        }

        Inventory inv = Bukkit.createInventory(null, 9 * 6, invName);

        // Preenche os slots de 0 até 44 com mapas em branco contendo as coordenadas
        List<int[]> warps = tpconn.getWarpsAsArray();
        int index = 0; // Começa a preencher a partir do índice 0
        for (int[] warp : warps) {
            if (index > 44) break; // Garante que não preenche mais que os slots disponíveis
            String name = "x: " + warp[0] + " y: " + warp[1] + " z: " + warp[2];
            inv.setItem(index, getItem(new ItemStack(Material.MAP), name));
            index++;
        }

        // Configurações dos outros itens do inventário
        inv.setItem(45, getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), " ", " "));
        inv.setItem(46, getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), " ", " "));
        inv.setItem(47, getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), " ", " "));
        inv.setItem(48, getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), " ", " "));
        inv.setItem(49, getItem(new ItemStack(Material.COMPASS), "&9Salvar Warp", "Salvar Coordenada Atual do Player"));
        inv.setItem(50, getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), " ", " "));
        inv.setItem(51, getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), " ", " "));
        inv.setItem(52, getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), " ", " "));
        inv.setItem(53, getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), " ", " "));

        // Registra o evento
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        player.openInventory(inv);
    }

    private ItemStack getItem(ItemStack item, String name, String... lore) {
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lores = new ArrayList<>();
        for (String s : lore) {
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lores);

        item.setItemMeta(meta);

        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(invName)) {
            event.setCancelled(true); // Impede mover itens no GUI

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null) {
                if (clickedItem.getType() == Material.COMPASS) {
                    if (clickedItem.getAmount() > 0) {
                        List<int[]> warps = tpconn.getWarpsAsArray();
                        if (warps.size() >= 44) {
                            player.sendMessage(ChatColor.RED + "Limite de 44 warps atingido! Não é possível salvar mais warps.");
                            return;
                        }

                        // Salva as coordenadas atuais do jogador no banco de dados
                        int x = player.getLocation().getBlockX();
                        int y = player.getLocation().getBlockY();
                        int z = player.getLocation().getBlockZ();

                        tpconn.insertWarp(String.valueOf(x), String.valueOf(y), String.valueOf(z));
                        player.sendMessage(ChatColor.GREEN + "Coordenadas salvas: x: " + x + " y: " + y + " z: " + z);

                        // Evita salvar múltiplas vezes alterando a quantidade de itens temporariamente
                        clickedItem.setAmount(0);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            player.closeInventory();
                            new TeleportRelicMenu(plugin, player);
                        }, 1L); // Fecha o inventário e reabre no próximo tick
                    }
                } else if (clickedItem.getType() == Material.MAP) {
                    // Parseia as coordenadas do nome do item
                    String[] parts = ChatColor.stripColor(Objects.requireNonNull(clickedItem.getItemMeta()).getDisplayName()).split(" ");
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[3]);
                    int z = Integer.parseInt(parts[5]);

                    selectedWarp = new int[]{x, y, z};

                    // Evita múltiplas execuções abrindo o menu uma vez
                    clickedItem.setAmount(0);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> openWarpOptionsMenu(player), 1L);
                }
            }
        }
    }

    @EventHandler
    public void onWarpOptionsClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.RED + "Warp Options")) {
            event.setCancelled(true); // Impede mover itens no GUI

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null) {
                if (clickedItem.getType() == Material.RED_DYE) {
                    if (clickedItem.getAmount() > 0) {
                        // Deleta o warp do banco de dados
                        tpconn.deleteWarp(selectedWarp[0], selectedWarp[1], selectedWarp[2]);
                        player.sendMessage(ChatColor.RED + "Warp deletado!");

                        // Evita executar a ação várias vezes
                        clickedItem.setAmount(0);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            player.closeInventory();
                            new TeleportRelicMenu(plugin, player);
                        }, 1L); // Fecha o inventário e reabre no próximo tick
                    }
                } else if (clickedItem.getType() == Material.LIME_DYE) {
                    if (clickedItem.getAmount() > 0) {
                        // Teleporta o jogador para o warp selecionado
                        player.teleport(player.getWorld().getBlockAt(selectedWarp[0], selectedWarp[1], selectedWarp[2]).getLocation());
                        player.sendMessage(ChatColor.GREEN + "Teletransportado para x: " + selectedWarp[0] + " y: " + selectedWarp[1] + " z: " + selectedWarp[2]);

                        // Evita executar a ação várias vezes
                        clickedItem.setAmount(0);
                        Bukkit.getScheduler().runTaskLater(plugin, player::closeInventory, 1L); // Fecha o inventário no próximo tick
                    }
                }
            }
        }
    }

    private void openWarpOptionsMenu(Player player) {
        Inventory warpOptionsInv = Bukkit.createInventory(null, 9 * 3, ChatColor.RED + "Warp Options");

        // Preenche o inventário com painéis de vidro vermelho
        for (int i = 0; i < 9 * 3; i++) {
            warpOptionsInv.setItem(i, getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), " ", " "));
        }

        // Adiciona o botão para deletar o warp
        warpOptionsInv.setItem(11, getItem(new ItemStack(Material.RED_DYE), ChatColor.RED + "Deletar Warp"));

        // Adiciona o botão para teletransportar para o warp
        warpOptionsInv.setItem(15, getItem(new ItemStack(Material.LIME_DYE), ChatColor.GREEN + "Teleportar para Warp"));

        player.openInventory(warpOptionsInv);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals(invName) || event.getView().getTitle().equals(ChatColor.RED + "Warp Options")) {
            event.setCancelled(true); // Impede arrastar itens no GUI
        }
    }
}
