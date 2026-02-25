package me.Asi.petCoreEngine.models;

import java.util.Objects;
import java.util.UUID;

public class Pet {

    private final UUID uniqueId;
    private final String typeId;
    private final String source;
    private int level;

    public Pet(String typeId, int level, String source) {
        this.uniqueId = UUID.randomUUID();
        this.typeId = Objects.requireNonNull(typeId, "Pet type id cannot be null");
        this.level = Math.max(1, level);
        this.source = source == null ? "unknown" : source;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getTypeId() {
        return typeId;
    }

    public int getLevel() {
        return level;
    }

    public void levelUp() {
        level++;
    }

    public String getSource() {
        return source;
    }
}
