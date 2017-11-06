package com.creativemd.playerrevive.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IRevival extends INBTSerializable<NBTTagCompound> {

    boolean isHealty();

    void stopBleeding();

    void startBleeding();

    float getProgress();

    boolean isRevived();

    boolean isDead();

    int getTimeLeft();

    void kill();
}
