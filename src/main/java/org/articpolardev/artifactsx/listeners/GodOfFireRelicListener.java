package org.articpolardev.artifactsx.listeners;

import org.articpolardev.artifactsx.handlers.NameFormat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class GodOfFireRelicListener implements Listener {
    private static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey("artifactsx", "relic_identifier");
    private static final String tag = "gofire";

    public static ItemStack createGodOfFireRelic() {
        ItemStack fireballStick = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = fireballStick.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().set(IDENTIFIER_KEY, PersistentDataType.STRING, tag);
            String FName = NameFormat.RelicNameFormat("#ff6600", "Relíquia do DEUS DO FOGO");
            meta.setDisplayName(FName);
            List<String> lore = new ArrayList<>();
            lore.add("§7Clique direito para spawnar uma bola de fogo");
            lore.add("§7Clique esquerdo para lançar todas as bolas de fogo em um raio de 1 chuck na direção da visão do Player");
            meta.setLore(lore);
            fireballStick.setItemMeta(meta);
        }

        return fireballStick;
    }
}
