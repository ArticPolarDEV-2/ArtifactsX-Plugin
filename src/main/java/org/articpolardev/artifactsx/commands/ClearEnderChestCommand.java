package org.articpolardev.artifactsx.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ClearEnderChestCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public ClearEnderChestCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Uso correto: /clearenderchest <nome_do_jogador> ou /clearenderchest AllPlayerEnderChest");
            return false;
        }

        // Limpa os baús do Ender de todos os jogadores online
        if (args[0].equalsIgnoreCase("AllPlayerEnderChest")) {
            clearEnderChestForAllOnlinePlayers(sender);
            return true;
        }

        // Limpa o baú do Ender de um jogador específico online
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Jogador não encontrado ou não está online.");
            return false;
        }

        target.getEnderChest().clear();
        sender.sendMessage(ChatColor.GREEN + "O baú do Ender de " + target.getName() + " foi limpo.");
        target.sendMessage(ChatColor.RED + "Seu baú do Ender foi limpo por um administrador.");

        return true;
    }

    private void clearEnderChestForAllOnlinePlayers(CommandSender sender) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.getEnderChest().clear();
            onlinePlayer.sendMessage(ChatColor.RED + "Seu baú do Ender foi limpo por um administrador.");
        }

        sender.sendMessage(ChatColor.GREEN + "Todos os baús do Ender de jogadores online foram limpos.");
    }
}
