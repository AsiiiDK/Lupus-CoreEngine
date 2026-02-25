package me.Asi.petCoreEngine.managers.coin;

import me.Asi.petCoreEngine.PetCoreEngine;
import me.Asi.petCoreEngine.models.Coin;
import me.Asi.petCoreEngine.util.HeadUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class VisualManager {

    /* =====================
       CONFIG
       ===================== */

    private static final String COIN_TEXTURE =
            "http://textures.minecraft.net/texture/aaf0d8d794a3a4a5e20a5292ed255134f76d4f3a5556fc7f426d27b247474d67";
    private static final long ROTATE_INTERVAL = 3L;

    /* =====================
       DATA
       ===================== */

    // Main coin
    private static final Map<UUID, ArmorStand> COINS =
            new HashMap<>();

    // Attack point
    private static final Map<UUID, ArmorStand> ATTACK_POINTS =
            new HashMap<>();

    // HP hologram
    private static final Map<UUID, TextDisplay> HOLOGRAMS =
            new HashMap<>();


    private static PetCoreEngine plugin;
    private static BukkitTask rotateTask;

    private VisualManager() {}

    /* =====================
       INIT / SHUTDOWN
       ===================== */

    public static void init(PetCoreEngine pl) {

        plugin = pl;

        startRotation();

        plugin.getLogger().info(
                "VisualManager initialized."
        );
    }

    public static void shutdown() {

        if (rotateTask != null) {
            rotateTask.cancel();
        }

        // Cleanup coins
        for (ArmorStand stand : COINS.values()) {
            if (stand != null && !stand.isDead()) {
                stand.remove();
            }
        }

        for (ArmorStand stand : ATTACK_POINTS.values()) {
            if (stand != null && !stand.isDead()) {
                stand.remove();
            }
        }

        for (TextDisplay display : HOLOGRAMS.values()) {
            if (display != null && !display.isDead()) {
                display.remove();
            }
        }

        COINS.clear();
        ATTACK_POINTS.clear();
        HOLOGRAMS.clear();
    }

    /* =====================
       SPAWN
       ===================== */

    public static void spawnCoin(Coin coin) {

        if (coin == null || coin.getLocation() == null)
            return;

        Location base =
                coin.getLocation()
                        .clone().subtract(0, 0.6, 0);
                        //.add(0.5, 1.0, 0.5);

        /* =====================
           MAIN COIN
           ===================== */

        ArmorStand stand =
                base.getWorld().spawn(base, ArmorStand.class);

        stand.setInvisible(true);
        stand.setMarker(true);
        stand.setGravity(false);
        stand.setSmall(true);
        stand.setBasePlate(false);
        stand.setArms(false);
        stand.setCollidable(false);
        stand.setPersistent(false);

        stand.getEquipment().setHelmet(
                HeadUtil.fromTextureURL(COIN_TEXTURE)
        );

        /* =====================
           ATTACK POINT
           ===================== */

        Location attackLoc =
                base.clone().add(0, 0.3, 0);

        ArmorStand attack =
                base.getWorld().spawn(attackLoc, ArmorStand.class);

        attack.setInvisible(true);
        attack.setMarker(true);
        attack.setSmall(true);
        attack.setGravity(false);
        attack.setBasePlate(false);
        attack.setCollidable(false);
        attack.setPersistent(false);

        /* =====================
           HOLOGRAM
           ===================== */

        Location holoLoc =
                base.clone().add(0, 0.9, 0);

        TextDisplay holo =
                base.getWorld().spawn(holoLoc, TextDisplay.class);

        holo.setBillboard(Display.Billboard.CENTER);
        holo.setShadowed(true);
        holo.setSeeThrough(true);

        updateHologram(holo, coin);

        /* =====================
           SAVE
           ===================== */

        COINS.put(coin.getId(), stand);
        ATTACK_POINTS.put(coin.getId(), attack);
        HOLOGRAMS.put(coin.getId(), holo);
    }

    /* =====================
       REMOVE
       ===================== */

    public static void removeCoin(Coin coin) {

        if (coin == null) return;

        UUID id = coin.getId();

        ArmorStand stand = COINS.remove(id);
        ArmorStand attack = ATTACK_POINTS.remove(id);
        TextDisplay holo = HOLOGRAMS.remove(id);

        if (stand != null && !stand.isDead()) stand.remove();
        if (attack != null && !attack.isDead()) attack.remove();
        if (holo != null && !holo.isDead()) holo.remove();
    }

    /* =====================
       UPDATE (HP + EFFECT)
       ===================== */

    public static void updateCoin(Coin coin) {

        if (coin == null) return;

        UUID id = coin.getId();

        ArmorStand stand = COINS.get(id);
        ArmorStand attack = ATTACK_POINTS.get(id);
        TextDisplay holo = HOLOGRAMS.get(id);

        if (stand == null || stand.isDead()) return;

        /* Update hologram */
        if (holo != null) {
            updateHologram(holo, coin);
        }

        /* Bounce effect */
        Location base =
                coin.getLocation()
                        .clone().subtract(0, 0.6, 0);

        base.add(0, 0.05, 0);

        stand.teleport(base);

        if (attack != null) {
            attack.teleport(
                    base.clone().add(0, 0.3, 0)
            );
        }

        if (holo != null) {
            holo.teleport(
                    base.clone().add(0, 0.9, 0)
            );
        }
    }

    /* =====================
       HOLOGRAM
       ===================== */

    private static void updateHologram(TextDisplay holo, Coin coin) {

        double hp = coin.getHealth();
        double max = coin.getMaxHealth();

        String bar = createBar(hp, max);

        holo.text(Component.text(
                "§6HP: §e" + (int) hp +
                        "§7/§e" + (int) max +
                        "\n" + bar
        ));
    }

    private static String createBar(double hp, double max) {

        int bars = 10;
        int filled = (int) ((hp / max) * bars);

        StringBuilder sb = new StringBuilder("§a");

        for (int i = 0; i < bars; i++) {

            if (i == filled) {
                sb.append("§7");
            }

            sb.append("|");
        }

        return sb.toString();
    }

    /* =====================
       ROTATION
       ===================== */

    private static void startRotation() {

        rotateTask =
                Bukkit.getScheduler()
                        .runTaskTimer(
                                plugin,
                                () -> {

                                    for (ArmorStand stand : COINS.values()) {

                                        if (stand == null || stand.isDead())
                                            continue;

                                        Location loc =
                                                stand.getLocation();

                                        loc.setYaw(
                                                loc.getYaw() + 6f
                                        );

                                        stand.teleport(loc);
                                    }

                                },
                                1L,
                                ROTATE_INTERVAL
                        );
    }
}