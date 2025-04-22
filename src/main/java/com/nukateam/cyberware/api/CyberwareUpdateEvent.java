package com.nukateam.cyberware.api;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityEvent;

public class CyberwareUpdateEvent extends EntityEvent {
    private final LivingEntity entityLivingBase;
    private final ICyberwareUserData cyberwareUserData;

    public CyberwareUpdateEvent(@Nonnull LivingEntity entityLivingBase, @Nonnull ICyberwareUserData cyberwareUserData) {
        super(entityLivingBase);
        this.entityLivingBase = entityLivingBase;
        this.cyberwareUserData = cyberwareUserData;
    }

    @Nonnull
    public LivingEntity getEntityLiving() {
        return entityLivingBase;
    }

    @Nonnull
    public ICyberwareUserData getCyberwareUserData() {
        return cyberwareUserData;
    }
}
