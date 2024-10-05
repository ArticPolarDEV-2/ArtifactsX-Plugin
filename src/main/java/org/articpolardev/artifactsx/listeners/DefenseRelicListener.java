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

public class DefenseRelicListener {
    private static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey("artifactsx", "relic_identifier");
    private static final String tag = "defense";

    // Método para criar a Relíquia da Defesa
    public static ItemStack createDefenseRelic() {
        ItemStack shield = new ItemStack(Material.SHIELD);
        ItemMeta meta = shield.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().set(IDENTIFIER_KEY, PersistentDataType.STRING, tag);
            String FName = NameFormat.RelicNameFormat("#808080", "Relíquia da DEFESA");
            meta.setDisplayName(FName);
            meta.addEnchant(Enchantment.UNBREAKING, 5, true);
            // meta.addEnchant(Enchantment.THORNS, 5, true);
            meta.setUnbreakable(true);
            List<String> lore = new ArrayList<>();
            lore.add("§7Indestrutível");
            lore.add("§7Um Escudo com Thorns 5.");
            meta.setLore(lore);
            shield.setItemMeta(meta);
        }

        return shield;
    }
}
