package org.articpolardev.artifactsx.expurgDay.commands;

import org.articpolardev.artifactsx.expurgDay.mainExpurg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpurgCommands implements CommandExecutor {
    private final mainExpurg expurgManager;

    public ExpurgCommands(mainExpurg manager) {
        this.expurgManager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (label.equalsIgnoreCase("expurgtime")) {
                handleExpurgTimeCommand(player);
                return true;
            } else if (label.equalsIgnoreCase("isexpurgday")) {
                handleIsExpurgDayCommand(player);
                return true;
            }
        }
        return false;
    }

    // Comando para mostrar o tempo restante do Expurgo
    private void handleExpurgTimeCommand(Player player) {
        if (expurgManager.isExpurgDayActived()) {
            long remainingTime = expurgManager.getRemainingTime();
            long hours = remainingTime / 3600;
            long minutes = (remainingTime % 3600) / 60;
            long seconds = remainingTime % 60;

            String timeMessage = String.format("§c☠ TEMPO RESTANTE DO EXPURGO ☠\n§f%02d:%02d:%02d (horas:minutos:segundos)", hours, minutes, seconds);
            player.sendMessage(timeMessage);
        } else {
            player.sendMessage("§c☠ EXPURGO NÃO ESTÁ ATIVADO ☠\n§fAtive o expurgo para ver o tempo restante.");
        }
    }

    // Comando para verificar se o Expurgo está ativo
    private void handleIsExpurgDayCommand(Player player) {
        if (expurgManager.isExpurgDayActived()) {
            player.sendMessage("§c☠ DIA DO EXPURGO ESTÁ ATIVO ☠");
        } else {
            player.sendMessage("§c☠ EXPURGO NÃO ESTÁ ATIVO ☠");
        }
    }
}
