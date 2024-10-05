package org.articpolardev.artifactsx.listeners;

import org.articpolardev.artifactsx.handlers.NameFormat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class HungerRelicListener {
    private static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey("artifactsx", "relic_identifier");
    private static final String tag = "hunger";

    public static ItemStack createHungerRelic() {
        ItemStack hungerRelic = new ItemStack(Material.COOKED_BEEF);
        ItemMeta meta = hungerRelic.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().set(IDENTIFIER_KEY, PersistentDataType.STRING, tag);
            String FName = NameFormat.RelicNameFormat("#ff6666", "Relíquia da FOME");
            meta.setDisplayName(FName);
            meta.addEnchant(Enchantment.UNBREAKING, 5, true);
            List<String> lore = new ArrayList<>();
            lore.add("§7Um bife infinito que sacia sua fome.");
            meta.setLore(lore);
            hungerRelic.setItemMeta(meta);
        }

        return hungerRelic;
    }
}
