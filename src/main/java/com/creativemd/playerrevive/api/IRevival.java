package com.creativemd.playerrevive.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

public interface IRevival extends INBTSerializable<NBTTagCompound> {

    void tick();

    boolean isHealty();

    void stopBleeding();

    void startBleeding(DamageSource source);

    float getProgress();

    boolean isRevived();

    boolean isDead();

    int getTimeLeft();

    void kill();
    
    DamageSource getSource();

    List<EntityPlayer> getRevivingPlayers();
}
