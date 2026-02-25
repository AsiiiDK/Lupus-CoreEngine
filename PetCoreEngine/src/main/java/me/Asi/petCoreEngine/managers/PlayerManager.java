package me.Asi.petCoreEngine.managers;

import me.Asi.petCoreEngine.PetCoreEngine;
import me.Asi.petCoreEngine.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager implements Listener {

    private final Map<UUID, PlayerData> cache = new HashMap<>();
    private final PetCoreEngine plugin;

    public PlayerManager(PetCoreEngine plugin) {

        this.plugin = plugin;

        Bukkit.getPluginManager()
                .registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        UUID uuid = e.getPlayer().getUniqueId();

        if (!uuid.equals("d739fd33-9b34-4df1-88c1-c0139e1752bd")) {
            ItemStack item = new ItemStack(Material.NETHER_STAR);

            ItemMeta meta = item.getItemMeta();
            meta.setItemName(ChatColor.YELLOW + "Menu");

            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "------------------");
            lore.add(ChatColor.BLUE + "Right Click" + ChatColor.WHITE + " to open the main menu!");

            meta.setLore(lore);

            item.setItemMeta(meta);

            e.getPlayer().getInventory().setItem(8, item);
        }


        PlayerData data = load(uuid);

        cache.put(uuid, data);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        UUID uuid = e.getPlayer().getUniqueId();

        PlayerData data = cache.remove(uuid);

        if (data != null) {
            save(data);
        }
    }

    public PlayerData get(UUID uuid) {
        return cache.get(uuid);
    }

    private PlayerData load(UUID uuid) {
        return new PlayerData(uuid); // DB later
    }

    private void save(PlayerData data) {
        // DB later
    }

    public void saveAll() {
        cache.values().forEach(this::save);
    }
}
