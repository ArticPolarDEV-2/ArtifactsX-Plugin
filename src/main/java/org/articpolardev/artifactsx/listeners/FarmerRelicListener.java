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

public class FarmerRelicListener implements Listener {
    private static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey("artifactsx", "relic_identifier");
    private static final String tag = "farmer";

    public static ItemStack createFarmerRelic() {
        ItemStack FarmerLeatherLeggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemMeta meta = FarmerLeatherLeggings.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(IDENTIFIER_KEY, PersistentDataType.STRING, tag);
            meta.addEnchant(Enchantment.PROTECTION, 8, true);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

            // Descricao do Item
            List<String> lore = new ArrayList<>();
            lore.add("§7Indestrutível");
            meta.setLore(lore);

            String FName = NameFormat.RelicNameFormat("#006400", "Relíquia do FAZENDEIRO");
            meta.setDisplayName(FName);
            FarmerLeatherLeggings.setItemMeta(meta);
        }
        return FarmerLeatherLeggings;
    }
}
