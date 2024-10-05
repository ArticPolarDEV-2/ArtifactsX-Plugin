package org.articpolardev.artifactsx.handlers;

import org.articpolardev.artifactsx.ArtifactsX;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class TorchHandler implements Listener {
    public TorchHandler(ArtifactsX plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onTorchPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() != Material.TORCH) {
            return;
        }

        // Permission Node
        // artifactsx.torch.diamond

        if (!event.getPlayer().hasPermission("artifactsx.torch.diamond")) {
            return;
        }

        event.getBlock().setType(Material.DIAMOND_BLOCK);
    }
}
