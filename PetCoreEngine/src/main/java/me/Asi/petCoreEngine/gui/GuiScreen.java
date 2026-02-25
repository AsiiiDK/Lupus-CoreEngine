package me.Asi.petCoreEngine.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class GuiScreen {

    private final String title;
    private final int rows;

    protected GuiScreen(String title, int rows) {
        this.title = title;
        this.rows = rows;
    }

    public final Inventory createInventory(Player player) {
        return Bukkit.createInventory(new GuiHolder(this), rows * 9, title);
    }

    public abstract void render(Player player, Inventory inventory);

    public abstract void handleClick(Player player, InventoryClickEvent event);

    public void handleClose(Player player, InventoryCloseEvent event) {
        // default: no-op
    }

    public static final class GuiHolder implements InventoryHolder {
        private final GuiScreen screen;

        public GuiHolder(GuiScreen screen) {
            this.screen = screen;
        }

        public GuiScreen getScreen() {
            return screen;
        }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
