package me.Asi.petCoreEngine;

import me.Asi.petCoreEngine.commands.EnDisCommand;
import me.Asi.petCoreEngine.commands.ZoneDebugCommand;
import me.Asi.petCoreEngine.events.JoinEvent;
import me.Asi.petCoreEngine.events.LeaveEvent;
import me.Asi.petCoreEngine.gui.GuiManager;
import me.Asi.petCoreEngine.gui.PetCollectionGui;
import me.Asi.petCoreEngine.managers.PlayerManager;
import me.Asi.petCoreEngine.managers.coin.CoinManager;
import me.Asi.petCoreEngine.managers.coin.CoinSpawnManager;
import me.Asi.petCoreEngine.managers.coin.VisualManager;
import me.Asi.petCoreEngine.managers.menu.MenuListener;
import me.Asi.petCoreEngine.managers.pet.PetConfigManager;
import me.Asi.petCoreEngine.managers.pet.PetManager;
import me.Asi.petCoreEngine.managers.pet.PetVisualManager;
import me.Asi.petCoreEngine.managers.zone.ZoneManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class PetCoreEngine extends JavaPlugin {

    private static PetCoreEngine instance;

    private CoinManager coinManager;
    private PlayerManager playerManager;
    private PetVisualManager petVisualManager;
    private PetConfigManager petConfigManager;
    private PetManager petManager;
    private ZoneManager zoneManager;
    private CoinSpawnManager coinSpawnManager;

    private GuiManager guiManager;
    private PetCollectionGui petCollectionGui;

    private boolean active;

    @Override
    public void onEnable() {
        instance = this;
        active = true;

        saveDefaultConfig();

        initManagers();

        if (this.getCommand("zonedebug") != null) {
            this.getCommand("zonedebug").setExecutor(new ZoneDebugCommand(this));
        }

        if (this.getCommand("zoneactive") != null) {
            this.getCommand("zoneactive").setExecutor(new EnDisCommand(this));
        }

        VisualManager.init(this);

        getLogger().info("PetCoreEngine enabled.");
    }

    @Override
    public void onDisable() {
        if (guiManager != null) {
            guiManager.shutdown();
        }

        if (playerManager != null) {
            playerManager.saveAll();
        }

        if (coinSpawnManager != null) {
            coinSpawnManager.shutdown();
        }

        if (coinManager != null) {
            coinManager.shutdown();
        }

        if (petManager != null) {
            petManager.shutdown();
        }

        if (petVisualManager != null) {
            petVisualManager.shutdown();
        }

        VisualManager.shutdown();

        getLogger().info("PetCoreEngine disabled.");
    }

    private void initManagers() {
        this.playerManager = new PlayerManager(this);
        this.coinManager = new CoinManager(this);
        this.zoneManager = new ZoneManager(this);
        this.coinSpawnManager = new CoinSpawnManager(this);

        this.petVisualManager = new PetVisualManager(this);
        this.petConfigManager = new PetConfigManager(this);
        this.petManager = new PetManager(this, petConfigManager, petVisualManager);

        this.guiManager = new GuiManager(this);
        this.petCollectionGui = new PetCollectionGui(this);
        new MenuListener(this);

        getServer().getPluginManager().registerEvents(new JoinEvent(this), this);
        getServer().getPluginManager().registerEvents(new LeaveEvent(this), this);
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

    public PetConfigManager getPetConfigManager() {
        return petConfigManager;
    }

    public PetManager getPetManager() {
        return petManager;
    }

    public ZoneManager getZoneManager() {
        return zoneManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public PetCollectionGui getPetCollectionGui() {
        return petCollectionGui;
    }

    public boolean getActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
