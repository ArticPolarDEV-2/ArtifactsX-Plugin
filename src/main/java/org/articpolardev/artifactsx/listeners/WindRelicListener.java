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

public class WindRelicListener {
    private static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey("artifactsx", "relic_identifier");
    private static final String tag = "wind";

    public static ItemStack createWindRelic() {
        ItemStack WindRelic = new ItemStack(Material.WIND_CHARGE);
        ItemMeta meta = WindRelic.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().set(IDENTIFIER_KEY, PersistentDataType.STRING, tag);
            String FName = NameFormat.RelicNameFormat("#ffffff", "Relíquia do DEUS DO VENTO");
            meta.setDisplayName(FName);
            meta.addEnchant(Enchantment.UNBREAKING, 5, true);
            List<String> lore = new ArrayList<>();
            lore.add("§7Indestrutível");
            lore.add("§7Uma Bola de Vento infinita que não desaparece.");
            meta.setLore(lore);
            WindRelic.setItemMeta(meta);
        }
        return WindRelic;
    }
}
