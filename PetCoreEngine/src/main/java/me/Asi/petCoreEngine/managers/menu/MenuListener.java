package me.Asi.petCoreEngine.managers.menu;

import me.Asi.petCoreEngine.PetCoreEngine;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuListener implements Listener {

    private final PetCoreEngine plugin;

    public MenuListener(PetCoreEngine plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (!isMenuItem(item)) {
            return;
        }

        event.setCancelled(true);
        plugin.getPetCollectionGui().openFor(event.getPlayer());
    }

    private boolean isMenuItem(ItemStack item) {
        if (item == null || item.getType() != Material.NETHER_STAR) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        String plainDisplay = null;
        if (meta.hasDisplayName()) {
            plainDisplay = ChatColor.stripColor(meta.getDisplayName());
        } else if (meta.hasItemName()) {
            plainDisplay = ChatColor.stripColor(meta.getItemName());
        }

        return plainDisplay != null && plainDisplay.equalsIgnoreCase("Menu");
    }
}
