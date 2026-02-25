package me.Asi.petCoreEngine.managers.coin;

import me.Asi.petCoreEngine.PetCoreEngine;
import me.Asi.petCoreEngine.models.Coin;
import me.Asi.petCoreEngine.models.CoinZone;
import me.Asi.petCoreEngine.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoinManager implements Listener {

    private final Map<UUID, Coin> coins = new HashMap<>();
    private final PetCoreEngine plugin;

    private static final double BASE_DAMAGE = 5.0;

    public CoinManager(PetCoreEngine plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);

        spawnTestCoins();
    }

    /* =====================
       TEST SPAWN
       ===================== */
    private void spawnTestCoins() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            World world = Bukkit.getWorlds().get(0);
            Location loc = world.getSpawnLocation().add(3, 0, 3);
            //spawnCoin(loc, 100, 25);
        }, 40L);
    }

    /* =====================
       SPAWN COIN
       ===================== */
    public void spawnCoin(Location loc, double hp, double reward, CoinZone zone) {
        if (loc == null || loc.getWorld() == null) return;

        // Create coin and store in cache
        Coin coin = new Coin(loc, hp, reward, zone);
        coins.put(coin.getId(), coin);

        // Delegate visuals to VisualManager
        VisualManager.spawnCoin(coin);
    }

    /* =====================
       DAMAGE SYSTEM
       ===================== */
    public void damageCoin(Player player, Coin coin, double damage) {

        if (coin == null) return;

        coin.damage(damage);

        VisualManager.updateCoin(coin);

        if (coin.getHealth() <= 0) {

            // Give reward to player
            PlayerData data =
                    plugin.getPlayerManager()
                            .get(player.getUniqueId());

            if (data != null) {
                data.addCoins(coin.getReward());
            }

            removeCoin(coin);
        }
    }

    /* =====================
       REMOVE COIN
       ===================== */
    private void removeCoin(Coin coin) {
        coins.remove(coin.getId());

        // Tell VisualManager to remove visual
        VisualManager.removeCoin(coin);
    }

    /* =====================
       GET COIN
       ===================== */

    public Coin getNearestCoin(Location loc, double radius) {
        Coin closest = null;
        double best = radius * radius;
        for (Coin coin : coins.values()) {
            double dist =
                    coin.getLocation()
                            .distanceSquared(loc);
            if (dist < best) {
                best = dist;
                closest = coin;
            }
        }
        return closest;
    }

    /* =====================
       PLAYER INTERACTION For testing purposes
       ===================== */
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;

        Location clickLoc = e.getClickedBlock().getLocation();

        for (Coin coin : coins.values()) {
            if (coin.getLocation().distanceSquared(clickLoc) < 2.5) {
                damageCoin(e.getPlayer(), coin, 10);
                break;
            }
        }
    }

    /* =====================
       CLEANUP
       ===================== */
    public void shutdown() {
        for (Coin coin : coins.values()) {
            VisualManager.removeCoin(coin);
        }
        coins.clear();
    }
}