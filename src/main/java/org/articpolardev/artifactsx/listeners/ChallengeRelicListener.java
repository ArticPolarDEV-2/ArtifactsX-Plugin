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

public class ChallengeRelicListener implements Listener {
    private static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey("artifactsx", "relic_identifier");
    private static final String tag = "challenge";

    public static ItemStack createChallengeRelic() {
        ItemStack mace = new ItemStack(Material.MACE);
        ItemMeta meta = mace.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().set(IDENTIFIER_KEY, PersistentDataType.STRING, tag);
            meta.addEnchant(Enchantment.WIND_BURST, 2, true);
            meta.addEnchant(Enchantment.DENSITY, 8, true);
            meta.addEnchant(Enchantment.BREACH, 5, true);
            meta.addEnchant(Enchantment.FIRE_ASPECT, 3, true);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

            String FName = NameFormat.RelicNameFormat("#dddddd", "Relíquia do DESAFIO");
            meta.setDisplayName(FName);

            List<String> lore = new ArrayList<>();
            lore.add("§7Indestrutível");
            meta.setLore(lore);

            mace.setItemMeta(meta);
        }
        return mace;
    }
}
