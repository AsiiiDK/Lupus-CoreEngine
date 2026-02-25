package me.Asi.petCoreEngine.commands;

import me.Asi.petCoreEngine.PetCoreEngine;
import me.Asi.petCoreEngine.models.CoinZone;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ZoneDebugCommand implements CommandExecutor {

    private final PetCoreEngine plugin;

    public ZoneDebugCommand(PetCoreEngine plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {

        if (!(sender instanceof Player player)) return true;

        CoinZone zone =
                plugin.getZoneManager()
                        .getZoneAt(player.getLocation());

        if (zone == null) {
            player.sendMessage("§cYou are not inside a zone.");
            return true;
        }

        player.sendMessage("§6Zone: §e" + zone.getId());
        player.sendMessage("§7Active Coins: §f" + zone.getActiveCoins());
        player.sendMessage("§7Max Coins: §f" + zone.getMaxCoins());
        player.sendMessage("§7Spawn Per Tick: §f" + zone.getSpawnPerTick());

        return true;
    }
}