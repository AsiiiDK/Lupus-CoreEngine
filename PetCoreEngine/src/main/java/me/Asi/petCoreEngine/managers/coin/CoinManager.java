package me.Asi.petCoreEngine.managers.coin;

import me.Asi.petCoreEngine.PetCoreEngine;
import me.Asi.petCoreEngine.models.Coin;
import me.Asi.petCoreEngine.models.CoinZone;
import me.Asi.petCoreEngine.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

    public CoinManager(PetCoreEngine plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void spawnCoin(Location loc, double hp, double reward, CoinZone zone) {
        if (loc == null || loc.getWorld() == null || zone == null) return;

        Coin coin = new Coin(loc, hp, reward, zone);
        coins.put(coin.getId(), coin);
        zone.incrementActive();

        VisualManager.spawnCoin(coin);
    }

    public void damageCoin(Player player, Coin coin, double damage) {
        if (coin == null) return;

        coin.damage(damage);
        VisualManager.updateCoin(coin);

        if (coin.getHealth() <= 0) {
            PlayerData data = plugin.getPlayerManager().get(player.getUniqueId());
            if (data != null) {
                data.addCoins(coin.getReward());
            }

            removeCoin(coin);
        }
    }

    private void removeCoin(Coin coin) {
        coins.remove(coin.getId());

        if (coin.getZone() != null) {
            coin.getZone().decrementActive();
        }

        VisualManager.removeCoin(coin);
    }

    public Coin getNearestCoin(Location loc, double radius) {
        if (loc == null || loc.getWorld() == null) {
            return null;
        }

        Coin closest = null;
        double best = radius * radius;

        for (Coin coin : coins.values()) {
            if (coin.getLocation().getWorld() == null || !coin.getLocation().getWorld().equals(loc.getWorld())) {
                continue;
            }

            double dist = coin.getLocation().distanceSquared(loc);
            if (dist < best) {
                best = dist;
                closest = coin;
            }
        }

        return closest;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;

        Location clickLoc = e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5);
        Coin coin = getNearestCoin(clickLoc, 1.6);

        if (coin != null) {
            damageCoin(e.getPlayer(), coin, 10);
        }
    }

    public void shutdown() {
        for (Coin coin : coins.values()) {
            VisualManager.removeCoin(coin);
            if (coin.getZone() != null) {
                coin.getZone().decrementActive();
            }
        }
        coins.clear();
    }
}
