package org.articpolardev.artifactsx.listeners;

import org.articpolardev.artifactsx.handlers.NameFormat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ImmortalityRelicListener {
    private static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey("artifactsx", "relic_identifier");
    private static final String tag = "immmortality";

    public static ItemStack createImmortalityRelic() {
        ItemStack immortalityRelic = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta meta = immortalityRelic.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().set(IDENTIFIER_KEY, PersistentDataType.STRING, tag);
            String FName = NameFormat.RelicNameFormat("#66ff99", "Relíquia da IMORTALIDADE");
            meta.setDisplayName(FName);
            List<String> lore = new ArrayList<>();
            lore.add("§7Um totem que não se quebra, mas possui cooldown.");
            meta.setLore(lore);
            immortalityRelic.setItemMeta(meta);
        }

        return immortalityRelic;
    }
}
