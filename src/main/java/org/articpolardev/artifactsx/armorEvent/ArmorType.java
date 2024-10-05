package org.articpolardev.artifactsx.armorEvent;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ArmorType {
	HELMET(5),
	CHESTPLATE(6),
	LEGGINGS(7),
	BOOTS(8);

	private final int slot;

	ArmorType(int slot) {
		this.slot = slot;
	}

	public static ArmorType matchType(ItemStack itemStack) {
		if (ArmorListener.isEmpty(itemStack)) {
			return null;
		} else {
			Material type = itemStack.getType();
			String typeName = type.name();
			if (!typeName.endsWith("_HELMET") && !typeName.endsWith("_SKULL") && !typeName.endsWith("_HEAD") && type != Material.CARVED_PUMPKIN) {
				if (!typeName.endsWith("_CHESTPLATE") && !typeName.equals("ELYTRA")) {
					if (typeName.endsWith("_LEGGINGS")) {
						return LEGGINGS;
					} else {
						return typeName.endsWith("_BOOTS") ? BOOTS : null;
					}
				} else {
					return CHESTPLATE;
				}
			} else {
				return HELMET;
			}
		}
	}

	public int getSlot() {
		return this.slot;
	}
}
