package me.Asi.petCoreEngine;

import me.Asi.petCoreEngine.commands.EnDisCommand;
import me.Asi.petCoreEngine.commands.ZoneDebugCommand;
import me.Asi.petCoreEngine.events.JoinEvent;
import me.Asi.petCoreEngine.events.LeaveEvent;
import me.Asi.petCoreEngine.managers.coin.CoinManager;
import me.Asi.petCoreEngine.managers.coin.CoinSpawnManager;
import me.Asi.petCoreEngine.managers.pet.PetVisualManager;
import me.Asi.petCoreEngine.managers.PlayerManager;
import me.Asi.petCoreEngine.managers.coin.VisualManager;
import me.Asi.petCoreEngine.managers.zone.ZoneManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class PetCoreEngine extends JavaPlugin {

    private static PetCoreEngine instance;

    // Managers
    private CoinManager coinManager;
    private PlayerManager playerManager;
    private PetVisualManager petVisualManager;
    private ZoneManager zoneManager;
    private CoinSpawnManager coinSpawnManager;

    // FOR TESTING PORPUSE::
    private boolean active;

    @Override
    public void onEnable() {
        instance = this;

        active = true;

        saveDefaultConfig();

        // Init services
        initManagers();

        getServer().getPluginManager().registerEvents(coinManager, instance);
        getServer().getPluginManager().registerEvents(playerManager, instance);

        getServer().getPluginManager().registerEvents(new JoinEvent(this), this);

        this.getCommand("zonedebug").setExecutor(new ZoneDebugCommand(this));
        this.getCommand("zoneactive").setExecutor(new EnDisCommand(this));

        VisualManager.init(this);

        getLogger().info("PetSimCore enabled!");
    }

    @Override
    public void onDisable() {

        if (playerManager != null) {
            playerManager.saveAll();
        }

        VisualManager.shutdown();
        petVisualManager.shutdown();

        getLogger().info("PetSimCore disabled!");
    }

    private void initManagers() {

        this.playerManager = new PlayerManager(this);
        this.coinManager = new CoinManager(this);
        this.petVisualManager = new PetVisualManager(this);
        this.zoneManager = new ZoneManager(this);

        // Starts coin spawning task when initialized in constructor
        this.coinSpawnManager = new CoinSpawnManager(this);

    }

    public static PetCoreEngine get() {
        return instance;
    }

    public CoinManager getCoinManager() {
        return coinManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public PetVisualManager getPetVisualManager() {
        return petVisualManager;
    }

    public ZoneManager getZoneManager() {return zoneManager;}


    // FOR TESTING #####
    public boolean getActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /* TODO
    "VISION BOARD"

    - Lav systemet så pets er virkelig nemme at lave f.eks du skal lave dem i config (eksempel config i config.yml)

     */


}
