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

public class HarvestRelicListener {
    private static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey("artifactsx", "relic_identifier");
    private static final String tag = "harvest";

    public static ItemStack createHarvestRelic() {
        ItemStack NetheriteHoe = new ItemStack(Material.NETHERITE_HOE);
        ItemMeta meta = NetheriteHoe.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(IDENTIFIER_KEY, PersistentDataType.STRING, tag);
            meta.addEnchant(Enchantment.FORTUNE, 4, true);
            meta.addEnchant(Enchantment.EFFICIENCY, 10, true);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

            // Descrição do Item
            List<String> lore = new ArrayList<>();
            lore.add("§7Indestrutível");
            meta.setLore(lore);

            String FName = NameFormat.RelicNameFormat("#669933", "Relíquia da COLHEITA");
            meta.setDisplayName(FName);
            NetheriteHoe.setItemMeta(meta);
        }
        return NetheriteHoe;
    }
}
