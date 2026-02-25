package me.Asi.petCoreEngine.models;

import java.util.Objects;

public class PetDefinition {

    private final String id;
    private final String displayName;
    private final String textureUrl;
    private final double baseDamage;
    private final double levelDamageMultiplier;
    private final double attackRange;
    private final long attackCooldownTicks;
    private final double moveSpeed;

    public PetDefinition(
            String id,
            String displayName,
            String textureUrl,
            double baseDamage,
            double levelDamageMultiplier,
            double attackRange,
            long attackCooldownTicks,
            double moveSpeed
    ) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.displayName = Objects.requireNonNull(displayName, "displayName cannot be null");
        this.textureUrl = Objects.requireNonNull(textureUrl, "textureUrl cannot be null");
        this.baseDamage = baseDamage;
        this.levelDamageMultiplier = levelDamageMultiplier;
        this.attackRange = attackRange;
        this.attackCooldownTicks = attackCooldownTicks;
        this.moveSpeed = moveSpeed;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTextureUrl() {
        return textureUrl;
    }

    public double calculateDamage(Pet pet) {
        int effectiveLevel = Math.max(1, pet.getLevel());
        return baseDamage + ((effectiveLevel - 1) * levelDamageMultiplier);
    }

    public double getAttackRange() {
        return attackRange;
    }

    public long getAttackCooldownTicks() {
        return attackCooldownTicks;
    }

    public double getMoveSpeed() {
        return moveSpeed;
    }
}
