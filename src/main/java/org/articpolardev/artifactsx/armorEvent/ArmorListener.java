//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.articpolardev.artifactsx.armorEvent;

import org.articpolardev.artifactsx.armorEvent.ArmorEquipEvent.EquipMethod;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class ArmorListener implements Listener {
	private final List<Material> blockedMaterials;

	public ArmorListener(List<String> blockedMaterialNames) {
		this.blockedMaterials = blockedMaterialNames.stream().map(Material::getMaterial).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@EventHandler(
			priority = EventPriority.HIGHEST,
			ignoreCancelled = true
	)
	public final void onClick(InventoryClickEvent event) {
		boolean shift = false;
		boolean numberkey = false;
		if (event.getAction() != InventoryAction.NOTHING) {
			if (event.getClick().equals(ClickType.SHIFT_LEFT) || event.getClick().equals(ClickType.SHIFT_RIGHT)) {
				shift = true;
			}

			if (event.getClick().equals(ClickType.NUMBER_KEY)) {
				numberkey = true;
			}

			if (event.getClick() == ClickType.SWAP_OFFHAND) {
				numberkey = true;
			}

			if (event.getSlotType() == SlotType.ARMOR || event.getSlotType() == SlotType.QUICKBAR || event.getSlotType() == SlotType.CONTAINER) {
				if (event.getClickedInventory() == null || event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
					if (event.getInventory().getType().equals(InventoryType.CRAFTING) || event.getInventory().getType().equals(InventoryType.PLAYER)) {
						if (event.getWhoClicked() instanceof Player) {
							ArmorType newArmorType = ArmorType.matchType(shift ? event.getCurrentItem() : event.getCursor());
							if (shift || newArmorType == null || event.getRawSlot() == newArmorType.getSlot()) {
								if (shift) {
									newArmorType = ArmorType.matchType(event.getCurrentItem());
									if (newArmorType != null) {
										boolean equipping = event.getRawSlot() != newArmorType.getSlot();

                                        label176: {
											if (newArmorType.equals(ArmorType.HELMET)) {
												if (equipping) {
													if (isEmpty(event.getWhoClicked().getInventory().getHelmet())) {
														break label176;
													}
												} else if (!isEmpty(event.getWhoClicked().getInventory().getHelmet())) {
													break label176;
												}
											}

											if (newArmorType.equals(ArmorType.CHESTPLATE)) {
												if (equipping) {
													if (isEmpty(event.getWhoClicked().getInventory().getChestplate())) {
														break label176;
													}
												} else if (!isEmpty(event.getWhoClicked().getInventory().getChestplate())) {
													break label176;
												}
											}

											if (newArmorType.equals(ArmorType.LEGGINGS)) {
												if (equipping) {
													if (isEmpty(event.getWhoClicked().getInventory().getLeggings())) {
														break label176;
													}
												} else if (!isEmpty(event.getWhoClicked().getInventory().getLeggings())) {
													break label176;
												}
											}

											if (!newArmorType.equals(ArmorType.BOOTS)) {
												return;
											}

											if (equipping) {
												if (!isEmpty(event.getWhoClicked().getInventory().getBoots())) {
													return;
												}
											} else if (isEmpty(event.getWhoClicked().getInventory().getBoots())) {
												return;
											}
										}

										ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player)event.getWhoClicked(), EquipMethod.SHIFT_CLICK, newArmorType, equipping ? null : event.getCurrentItem(), equipping ? event.getCurrentItem() : null);
										Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
										if (armorEquipEvent.isCancelled()) {
											event.setCancelled(true);
										}
									}
								} else {
									ItemStack newArmorPiece = event.getCursor();
									ItemStack oldArmorPiece = event.getCurrentItem();
									if (numberkey) {
										if (event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
											ItemStack hotbarItem = null;
											if (event.getHotbarButton() != -1) {
												hotbarItem = event.getClickedInventory().getItem(event.getHotbarButton());
											} else if (event.getHotbarButton() == -1 && event.getClickedInventory() instanceof PlayerInventory) {
												hotbarItem = ((PlayerInventory)event.getClickedInventory()).getItem(EquipmentSlot.OFF_HAND);
											}

											if (!isEmpty(hotbarItem)) {
												newArmorType = ArmorType.matchType(hotbarItem);
												newArmorPiece = hotbarItem;
												oldArmorPiece = event.getClickedInventory().getItem(event.getSlot());
											} else {
												newArmorType = ArmorType.matchType(!isEmpty(event.getCurrentItem()) ? event.getCurrentItem() : event.getCursor());
											}
										}
									} else if (isEmpty(event.getCursor()) && !isEmpty(event.getCurrentItem())) {
										newArmorType = ArmorType.matchType(event.getCurrentItem());
									}

									if (newArmorType != null && event.getRawSlot() == newArmorType.getSlot()) {
										ArmorEquipEvent.EquipMethod method = EquipMethod.PICK_DROP;
										if (event.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberkey) {
											method = EquipMethod.HOTBAR_SWAP;
										}

										ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player)event.getWhoClicked(), method, newArmorType, oldArmorPiece, newArmorPiece);
										Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
										if (armorEquipEvent.isCancelled()) {
											event.setCancelled(true);
										}
									}
								}

							}
						}
					}
				}
			}
		}
	}

	@EventHandler(
			priority = EventPriority.HIGHEST
	)
	public void onInteract(PlayerInteractEvent event) {
		if (!event.useItemInHand().equals(Result.DENY)) {
			if (event.getAction() != Action.PHYSICAL) {
				if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Player player = event.getPlayer();
					if (!event.useInteractedBlock().equals(Result.DENY) && event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && !player.isSneaking()) {
						Material mat = event.getClickedBlock().getType();
						if (this.blockedMaterials.contains(mat)) {
							return;
						}
					}

					ArmorType newArmorType = ArmorType.matchType(event.getItem());
					if (event.getItem() != null && event.getItem().getType() == Material.CARVED_PUMPKIN) {
						return;
					}

					if (newArmorType == null) {
						return;
					}

					ItemStack oldArmorPiece = null;
					switch (newArmorType) {
						case HELMET:
							oldArmorPiece = player.getInventory().getHelmet();
							break;
						case CHESTPLATE:
							oldArmorPiece = player.getInventory().getChestplate();
							break;
						case LEGGINGS:
							oldArmorPiece = player.getInventory().getLeggings();
							break;
						case BOOTS:
							oldArmorPiece = player.getInventory().getBoots();
					}

					ArmorType oldArmorType = ArmorType.matchType(oldArmorPiece);
					if (isEmpty(oldArmorPiece) || newArmorType.equals(oldArmorType)) {
						ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(player, EquipMethod.HOTBAR, newArmorType, oldArmorPiece, event.getItem());
						Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
						if (armorEquipEvent.isCancelled()) {
							event.setCancelled(true);
							player.updateInventory();
						}
					}
				}

			}
		}
	}

	static boolean isEmpty(ItemStack item) {
		return item == null || item.getType().isAir() || item.getAmount() == 0;
	}

	@EventHandler(
			priority = EventPriority.HIGHEST,
			ignoreCancelled = true
	)
	public void onDrag(InventoryDragEvent event) {
		ArmorType type = ArmorType.matchType(event.getOldCursor());
		if (!event.getRawSlots().isEmpty()) {
			if (type != null && type.getSlot() == event.getRawSlots().stream().findFirst().orElse(0)) {
				ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player)event.getWhoClicked(), EquipMethod.DRAG, type, null, event.getOldCursor());
				Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
				if (armorEquipEvent.isCancelled()) {
					event.setResult(Result.DENY);
					event.setCancelled(true);
				}
			}

		}
	}

	@EventHandler
	public void onBreak(PlayerItemBreakEvent event) {
		ArmorType type = ArmorType.matchType(event.getBrokenItem());
		if (type != null) {
			Player p = event.getPlayer();
			ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, EquipMethod.BROKE, type, event.getBrokenItem(), null);
			Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
			if (armorEquipEvent.isCancelled()) {
				ItemStack i = event.getBrokenItem().clone();
				i.setAmount(1);
				i.setDurability((short)(i.getDurability() - 1));
				if (type.equals(ArmorType.HELMET)) {
					p.getInventory().setHelmet(i);
				} else if (type.equals(ArmorType.CHESTPLATE)) {
					p.getInventory().setChestplate(i);
				} else if (type.equals(ArmorType.LEGGINGS)) {
					p.getInventory().setLeggings(i);
				} else if (type.equals(ArmorType.BOOTS)) {
					p.getInventory().setBoots(i);
				}
			}
		}

	}

	@EventHandler(
			priority = EventPriority.MONITOR,
			ignoreCancelled = true
	)
	public void onDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		if (!event.getKeepInventory()) {
			ItemStack[] var3 = p.getInventory().getArmorContents();
			int var4 = var3.length;

			for(int var5 = 0; var5 < var4; ++var5) {
				ItemStack i = var3[var5];
				if (!isEmpty(i)) {
					Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(p, EquipMethod.DEATH, ArmorType.matchType(i), i, null));
				}
			}

		}
	}
}
