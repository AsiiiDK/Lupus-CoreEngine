package me.Asi.petCoreEngine.util;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Material;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.UUID;



public class HeadUtil {

    public static ItemStack fromTextureURL(String textureURL) {

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta == null) return head;

        try {
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());

            PlayerTextures textures = profile.getTextures();
            textures.setSkin(new URL(textureURL));
            profile.setTextures(textures);

            meta.setOwnerProfile(profile);

        } catch (Exception e) {
            e.printStackTrace();
        }

        head.setItemMeta(meta);
        return head;
    }
}