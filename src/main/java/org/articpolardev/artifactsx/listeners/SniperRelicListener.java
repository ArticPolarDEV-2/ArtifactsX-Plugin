package org.articpolardev.artifactsx.listeners;

import org.articpolardev.artifactsx.handlers.NameFormat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class SniperRelicListener {
    private static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey("artifactsx", "relic_identifier");
    private static final String tag = "sniper";

    public static ItemStack createSniperRelic() {
        ItemStack SniperRelic = new ItemStack(Material.BOW);
        ItemMeta meta = SniperRelic.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(IDENTIFIER_KEY, PersistentDataType.STRING, tag);
            meta.addEnchant(Enchantment.POWER, 10, true);
            meta.addEnchant(Enchantment.FLAME, 1, true);
            meta.addEnchant(Enchantment.PUNCH, 4, true);
            meta.addEnchant(Enchantment.INFINITY, 1, true);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

            // Descrição do Item
            List<String> lore = new ArrayList<>();
            lore.add("§7Indestrutível");
            meta.setLore(lore);

            String FName = NameFormat.RelicNameFormat("#f4998d", "Relíquia do ATIRADOR");
            meta.setDisplayName(FName);
            SniperRelic.setItemMeta(meta);
        }
        return SniperRelic;
    }
}
