package org.articpolardev.artifactsx.listeners;

import org.articpolardev.artifactsx.handlers.NameFormat;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class InvaderRelicListener implements Listener {
    private static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey("artifactsx", "relic_identifier");
    private static final String tag = "invader";

    public static ItemStack createInvaderRelic() {
        ItemStack InvaderLeatherLeggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta leatherMeta = (LeatherArmorMeta) InvaderLeatherLeggings.getItemMeta();

        if (leatherMeta != null) {
            leatherMeta.getPersistentDataContainer().set(IDENTIFIER_KEY, PersistentDataType.STRING, tag);
            leatherMeta.addEnchant(Enchantment.PROTECTION, 8, true);
            leatherMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
            leatherMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
            leatherMeta.setUnbreakable(true);
            leatherMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

            // Definindo a cor cinza
            leatherMeta.setColor(Color.fromRGB(71, 79, 82));

            // Descrição do item
            List<String> lore = new ArrayList<>();
            lore.add("§7Indestrutível");
            leatherMeta.setLore(lore);

            String FName = NameFormat.RelicNameFormat("#666666", "Relíquia do INVASOR");
            leatherMeta.setDisplayName(FName);
            InvaderLeatherLeggings.setItemMeta(leatherMeta);
        }
        return InvaderLeatherLeggings;
    }
}
