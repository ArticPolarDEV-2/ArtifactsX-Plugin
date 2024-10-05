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

public class LuckRelicListener {
    private static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey("artifactsx", "relic_identifier");
    private static final String tag = "luck";

    public static ItemStack createLuckRelic() {
        ItemStack RabbitFoot = new ItemStack(Material.RABBIT_FOOT);
        ItemMeta meta = RabbitFoot.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(IDENTIFIER_KEY, PersistentDataType.STRING, tag);
            meta.addEnchant(Enchantment.UNBREAKING, 5, true);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

            // Descrição do Item
            List<String> lore = new ArrayList<>();
            lore.add("§7Indestrutível");
            lore.add("§7Botão Esquerdo do mouse: Aplica Efeito de SORTE");
            lore.add("§7Botão Direto do mouse: Remove Efeito de SORTE");
            meta.setLore(lore);

            String FName = NameFormat.RelicNameFormat("#08f26e", "Relíquia da SORTE");
            meta.setDisplayName(FName);
            RabbitFoot.setItemMeta(meta);
        }
        return RabbitFoot;
    }
}
