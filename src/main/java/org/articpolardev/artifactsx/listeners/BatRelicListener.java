package org.articpolardev.artifactsx.listeners;

import org.articpolardev.artifactsx.handlers.NameFormat;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class BatRelicListener {
    private static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey("artifactsx", "relic_identifier");
    private static final String tag = "bat";

    public static ItemStack createBatRelic() {
        ItemStack BatLeatherHelmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta leatherMeta = (LeatherArmorMeta) BatLeatherHelmet.getItemMeta();

        if (leatherMeta != null) {
            leatherMeta.getPersistentDataContainer().set(IDENTIFIER_KEY, PersistentDataType.STRING, tag);
            leatherMeta.addEnchant(Enchantment.PROTECTION, 8, true);
            leatherMeta.setUnbreakable(true);
            leatherMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

            // Definindo a cor cinza
            leatherMeta.setColor(Color.BLACK);

            // Descrição do item
            List<String> lore = new ArrayList<>();
            lore.add("§7Indestrutível");
            leatherMeta.setLore(lore);
            String FName = NameFormat.RelicNameFormat("#993333", "Relíquia do MORCEGO");
            leatherMeta.setDisplayName(FName);
            BatLeatherHelmet.setItemMeta(leatherMeta);
        }
        return BatLeatherHelmet;
    }
}
