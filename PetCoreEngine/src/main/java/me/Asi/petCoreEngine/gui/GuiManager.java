package me.Asi.petCoreEngine.gui;

import me.Asi.petCoreEngine.PetCoreEngine;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GuiManager implements Listener {

    private final PetCoreEngine plugin;

    public GuiManager(PetCoreEngine plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player, GuiScreen screen) {
        Inventory inventory = screen.createInventory(player);
        screen.render(player, inventory);
        player.openInventory(inventory);
    }

    public void rerender(Player player) {
        Inventory top = player.getOpenInventory().getTopInventory();
        GuiScreen screen = resolve(top);
        if (screen != null) {
            screen.render(player, top);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        GuiScreen screen = resolve(event.getView().getTopInventory());
        if (screen == null) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory() == null) {
            return;
        }

        if (!event.getClickedInventory().equals(event.getView().getTopInventory())) {
            return;
        }

        screen.handleClick(player, event);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        GuiScreen screen = resolve(event.getView().getTopInventory());
        if (screen != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        GuiScreen screen = resolve(event.getView().getTopInventory());
        if (screen != null) {
            screen.handleClose(player, event);
        }
    }

    public void shutdown() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            GuiScreen screen = resolve(player.getOpenInventory().getTopInventory());
            if (screen != null) {
                player.closeInventory();
            }
        }
    }

    private GuiScreen resolve(Inventory inventory) {
        if (inventory == null) {
            return null;
        }

        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof GuiScreen.GuiHolder guiHolder) {
            return guiHolder.getScreen();
        }

        return null;
    }
}
