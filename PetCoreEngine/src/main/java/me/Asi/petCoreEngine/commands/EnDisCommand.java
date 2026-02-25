package me.Asi.petCoreEngine.commands;

import me.Asi.petCoreEngine.PetCoreEngine;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EnDisCommand implements CommandExecutor {

    private final PetCoreEngine plugin;

    public EnDisCommand(PetCoreEngine plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if (!(commandSender instanceof Player)) return false;

        Player player = (Player) commandSender;

        if (plugin.getActive() == true) {
            plugin.setActive(false);
            player.sendMessage("Deaktiverede systemet");
        } else {
            plugin.setActive(true);
            player.sendMessage("Aktiverede systemet");
        }
        return true;
    }
}
