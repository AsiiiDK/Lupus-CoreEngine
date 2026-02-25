package me.Asi.petCoreEngine.managers.pet;

import me.Asi.petCoreEngine.PetCoreEngine;
import me.Asi.petCoreEngine.models.PetDefinition;
import me.Asi.petCoreEngine.models.PlayerData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class PetConfigManager {

    private final PetCoreEngine plugin;

    private final Map<String, PetDefinition> definitions = new LinkedHashMap<>();
    private final List<String> starterPets = new ArrayList<>();

    private File file;
    private FileConfiguration config;

    private int baseMaxEquipped = 3;
    private int extraPerRebirth = 1;
    private int hyperMaxEquipped = PlayerData.ABSOLUTE_MAX_EQUIPPED;

    public PetConfigManager(PetCoreEngine plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        file = new File(plugin.getDataFolder(), "pets.yml");

        if (!file.exists()) {
            plugin.saveResource("pets.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);

        loadSettings();
        loadDefinitions();
    }

    private void loadSettings() {
        baseMaxEquipped = config.getInt("settings.base-max-equipped", 3);
        extraPerRebirth = config.getInt("settings.extra-equipped-per-rebirth", 1);
        hyperMaxEquipped = Math.min(
                PlayerData.ABSOLUTE_MAX_EQUIPPED,
                config.getInt("settings.hypermax-max-equipped", PlayerData.ABSOLUTE_MAX_EQUIPPED)
        );

        starterPets.clear();
        starterPets.addAll(config.getStringList("settings.starter-pets"));
    }

    private void loadDefinitions() {
        definitions.clear();

        ConfigurationSection petsSection = config.getConfigurationSection("pets");
        if (petsSection == null) {
            plugin.getLogger().warning("No pets defined in pets.yml");
            return;
        }

        for (String petId : petsSection.getKeys(false)) {
            String path = "pets." + petId;

            String displayName = config.getString(path + ".display-name", petId);
            String textureUrl = config.getString(path + ".texture-url",
                    "http://textures.minecraft.net/texture/26629dfa3fdfef04054024e0156d5e19da5401b1911f59b4bd3982685fe54c2c");

            double baseDamage = config.getDouble(path + ".base-damage", 10.0);
            double levelDamageMultiplier = config.getDouble(path + ".level-damage-multiplier", 3.0);
            double attackRange = config.getDouble(path + ".attack-range", 1.8);
            long attackCooldownTicks = config.getLong(path + ".attack-cooldown-ticks", 15L);
            double moveSpeed = config.getDouble(path + ".move-speed", 0.45);

            PetDefinition definition = new PetDefinition(
                    petId.toLowerCase(Locale.ROOT),
                    displayName,
                    textureUrl,
                    baseDamage,
                    levelDamageMultiplier,
                    attackRange,
                    attackCooldownTicks,
                    moveSpeed
            );

            definitions.put(definition.getId(), definition);
        }

        plugin.getLogger().info("Loaded " + definitions.size() + " pet definitions.");
    }

    public Collection<PetDefinition> getDefinitions() {
        return Collections.unmodifiableCollection(definitions.values());
    }

    public PetDefinition getDefinition(String typeId) {
        if (typeId == null) {
            return null;
        }
        return definitions.get(typeId.toLowerCase(Locale.ROOT));
    }

    public List<String> getStarterPets() {
        if (!starterPets.isEmpty()) {
            return Collections.unmodifiableList(starterPets);
        }
        if (definitions.isEmpty()) {
            return List.of();
        }
        return List.of(definitions.values().iterator().next().getId());
    }

    public int getMaxEquippedSlots(int rebirths) {
        int computed = baseMaxEquipped + (Math.max(0, rebirths) * extraPerRebirth);
        return Math.max(1, Math.min(hyperMaxEquipped, computed));
    }
}
