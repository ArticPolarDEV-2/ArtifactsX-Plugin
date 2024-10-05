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

public class EndermanRelicListener {
    private static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey("artifactsx", "relic_identifier");
    private static final String tag = "enderman";

    public static ItemStack createEndermanRelic() {
        ItemStack endermanRelic = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = endermanRelic.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().set(IDENTIFIER_KEY, PersistentDataType.STRING, tag);
            String FName = NameFormat.RelicNameFormat("#6600cc", "Relíquia do ENDERMAN");
            meta.setDisplayName(FName);
            meta.addEnchant(Enchantment.UNBREAKING, 5, true);
            List<String> lore = new ArrayList<>();
            lore.add("§7Indestrutível");
            lore.add("§7Uma pérola infinita que não desaparece.");
            meta.setLore(lore);
            endermanRelic.setItemMeta(meta);
        }

        return endermanRelic;
    }
}
