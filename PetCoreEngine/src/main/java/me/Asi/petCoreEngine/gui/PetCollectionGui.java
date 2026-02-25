package me.Asi.petCoreEngine.gui;

import me.Asi.petCoreEngine.PetCoreEngine;
import me.Asi.petCoreEngine.models.Pet;
import me.Asi.petCoreEngine.models.PetDefinition;
import me.Asi.petCoreEngine.models.PlayerData;
import me.Asi.petCoreEngine.util.HeadUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PetCollectionGui extends GuiScreen {

    private static final int[] PET_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private static final int PREV_SLOT = 45;
    private static final int NEXT_SLOT = 46;
    private static final int INFO_SLOT = 49;
    private static final int CLOSE_SLOT = 53;

    private final PetCoreEngine plugin;
    private final Map<UUID, Integer> pageByPlayer = new HashMap<>();

    public PetCollectionGui(PetCoreEngine plugin) {
        super("§8✦ §6Pet Collection §8✦", 6);
        this.plugin = plugin;
    }

    public void openFor(Player player) {
        pageByPlayer.putIfAbsent(player.getUniqueId(), 0);
        plugin.getGuiManager().open(player, this);
    }

    @Override
    public void render(Player player, Inventory inventory) {
        inventory.clear();

        fillFrame(inventory);

        PlayerData data = plugin.getPlayerManager().get(player.getUniqueId());
        if (data == null) {
            inventory.setItem(INFO_SLOT, createNamed(Material.BARRIER, "§cNo profile loaded"));
            return;
        }

        List<Pet> pets = sortedPets(data);
        int pageSize = PET_SLOTS.length;
        int maxPage = Math.max(0, (int) Math.ceil((double) pets.size() / pageSize) - 1);

        int page = Math.max(0, Math.min(pageByPlayer.getOrDefault(player.getUniqueId(), 0), maxPage));
        pageByPlayer.put(player.getUniqueId(), page);

        int from = page * pageSize;
        int to = Math.min(from + pageSize, pets.size());

        for (int i = from; i < to; i++) {
            Pet pet = pets.get(i);
            int slot = PET_SLOTS[i - from];
            inventory.setItem(slot, createPetIcon(data, pet));
        }

        inventory.setItem(PREV_SLOT, createNamed(Material.ARROW, page > 0 ? "§e◀ Previous page" : "§7◀ Previous page"));
        inventory.setItem(NEXT_SLOT, createNamed(Material.ARROW, page < maxPage ? "§eNext page ▶" : "§7Next page ▶"));

        String info = "§ePage §f" + (page + 1) + "§7/§f" + (maxPage + 1)
                + " §8| §eOwned: §f" + pets.size()
                + " §8| §aEquipped: §f" + data.getEquippedPets().size() + "§7/§f" + data.getMaxEquipped();
        inventory.setItem(INFO_SLOT, createNamed(Material.BOOK, info,
                "§7Left-click: §aEquip",
                "§7Right-click: §cUnequip",
                "§7Middle-click or Shift+Left: §6Favorite"));

        inventory.setItem(CLOSE_SLOT, createNamed(Material.BARRIER, "§cClose"));
    }


    @Override
    public void handleClose(Player player, InventoryCloseEvent event) {
        pageByPlayer.remove(player.getUniqueId());
    }

    @Override
    public void handleClick(Player player, InventoryClickEvent event) {
        int slot = event.getSlot();

        if (slot == CLOSE_SLOT) {
            player.closeInventory();
            return;
        }

        if (slot == PREV_SLOT) {
            pageByPlayer.compute(player.getUniqueId(), (ignored, page) -> Math.max(0, (page == null ? 0 : page) - 1));
            plugin.getGuiManager().rerender(player);
            return;
        }

        if (slot == NEXT_SLOT) {
            pageByPlayer.compute(player.getUniqueId(), (ignored, page) -> Math.max(0, (page == null ? 0 : page) + 1));
            plugin.getGuiManager().rerender(player);
            return;
        }

        int relativeIndex = findRelativePetIndex(slot);
        if (relativeIndex < 0) {
            return;
        }

        PlayerData data = plugin.getPlayerManager().get(player.getUniqueId());
        if (data == null) {
            return;
        }

        List<Pet> pets = sortedPets(data);
        int page = pageByPlayer.getOrDefault(player.getUniqueId(), 0);
        int absoluteIndex = (page * PET_SLOTS.length) + relativeIndex;

        if (absoluteIndex < 0 || absoluteIndex >= pets.size()) {
            return;
        }

        Pet pet = pets.get(absoluteIndex);
        ClickType click = event.getClick();

        if (click == ClickType.LEFT) {
            if (data.equipPet(pet)) {
                player.sendMessage(ChatColor.GREEN + "Equipped " + displayName(pet));
            } else {
                player.sendMessage(ChatColor.RED + "Could not equip " + displayName(pet) + ".");
            }
            plugin.getPetManager().syncPlayer(player);
        } else if (click == ClickType.RIGHT) {
            if (data.unequipPet(pet)) {
                player.sendMessage(ChatColor.YELLOW + "Unequipped " + displayName(pet));
            }
            plugin.getPetManager().syncPlayer(player);
        } else if (click == ClickType.MIDDLE || click == ClickType.SHIFT_LEFT) {
            boolean favorite = data.toggleFavorite(pet);
            player.sendMessage((favorite ? ChatColor.GOLD + "Favorited " : ChatColor.GRAY + "Unfavorited ") + displayName(pet));
        }

        plugin.getGuiManager().rerender(player);
    }

    private int findRelativePetIndex(int slot) {
        for (int i = 0; i < PET_SLOTS.length; i++) {
            if (PET_SLOTS[i] == slot) {
                return i;
            }
        }

        return -1;
    }

    private List<Pet> sortedPets(PlayerData data) {
        List<Pet> pets = new ArrayList<>(data.getPets());
        pets.sort(Comparator
                .comparing((Pet p) -> !data.isFavorite(p))
                .thenComparing((Pet p) -> !data.getEquippedPets().contains(p))
                .thenComparing(Pet::getTypeId)
                .thenComparing(Pet::getLevel));
        return pets;
    }

    private ItemStack createPetIcon(PlayerData data, Pet pet) {
        PetDefinition definition = plugin.getPetConfigManager().getDefinition(pet.getTypeId());

        ItemStack icon;
        if (definition != null) {
            icon = HeadUtil.fromTextureURL(definition.getTextureUrl());
        } else {
            icon = new ItemStack(Material.BONE);
        }

        ItemMeta meta = icon.getItemMeta();
        if (meta == null) {
            return icon;
        }

        boolean equipped = data.getEquippedPets().contains(pet);
        boolean favorite = data.isFavorite(pet);

        meta.setDisplayName((favorite ? "§6★ " : "§e") + displayName(pet));

        List<String> lore = new ArrayList<>();
        lore.add("§7Type: §f" + pet.getTypeId());
        lore.add("§7Level: §f" + pet.getLevel());

        if (definition != null) {
            lore.add("§7Damage: §f" + String.format(Locale.US, "%.1f", definition.calculateDamage(pet)));
        }

        lore.add("§7Status: " + (equipped ? "§aEquipped" : "§cUnequipped"));
        lore.add("§7Favorite: " + (favorite ? "§6Yes" : "§8No"));
        lore.add(" ");
        lore.add("§aLeft-click §7to equip");
        lore.add("§cRight-click §7to unequip");
        lore.add("§6Middle-click §7or §6Shift+Left §7to favorite");

        meta.setLore(lore);
        icon.setItemMeta(meta);
        return icon;
    }

    private String displayName(Pet pet) {
        PetDefinition definition = plugin.getPetConfigManager().getDefinition(pet.getTypeId());
        if (definition == null) {
            return pet.getTypeId();
        }

        return definition.getDisplayName();
    }

    private ItemStack createNamed(Material material, String name, String... loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(name);
        if (loreLines.length > 0) {
            meta.setLore(Arrays.asList(loreLines));
        }
        item.setItemMeta(meta);
        return item;
    }

    private void fillFrame(Inventory inventory) {
        ItemStack panel = createNamed(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, panel);
        }

        for (int slot : PET_SLOTS) {
            inventory.setItem(slot, null);
        }
    }
}
