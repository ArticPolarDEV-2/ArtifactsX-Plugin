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

public class WoodcutterRelicListener {
    private static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey("artifactsx", "relic_identifier");
    private static final String tag = "woodcutter";

    public static ItemStack createWoodcutterRelic() {
        ItemStack NetheriteAxe = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta meta = NetheriteAxe.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(IDENTIFIER_KEY, PersistentDataType.STRING, tag);
            meta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
            meta.addEnchant(Enchantment.EFFICIENCY, 10, true);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

            // Descrição do Item
            List<String> lore = new ArrayList<>();
            lore.add("§7Indestrutível");
            meta.setLore(lore);

            String FName = NameFormat.RelicNameFormat("#666600", "Relíquia do LENHADOR");
            meta.setDisplayName(FName);
            NetheriteAxe.setItemMeta(meta);
        }
        return NetheriteAxe;
    }
}
