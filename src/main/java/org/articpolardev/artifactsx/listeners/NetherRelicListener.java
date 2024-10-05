package org.articpolardev.artifactsx.listeners;

import org.articpolardev.artifactsx.handlers.NameFormat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class NetherRelicListener implements Listener {
    private static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey("artifactsx", "relic_identifier");
    private static final String tag = "nether";

    public static ItemStack createNetherRelic() {
        ItemStack GoldHelmet = new ItemStack(Material.GOLDEN_HELMET);
        ItemMeta meta = GoldHelmet.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(IDENTIFIER_KEY, PersistentDataType.STRING, tag);
            meta.addEnchant(Enchantment.PROTECTION, 8, true);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

            // Descrição do Item
            List<String> lore = new ArrayList<>();
            lore.add("§7Indestrutível");
            meta.setLore(lore);

            String FName = NameFormat.RelicNameFormat("#e5b80b", "Relíquia do NETHER");
            meta.setDisplayName(FName);
            GoldHelmet.setItemMeta(meta);
        }
        return GoldHelmet;
    }
}