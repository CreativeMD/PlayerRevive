package team.creative.playerrevive.cap;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import team.creative.playerrevive.api.IBleeding;

public class BleedingStorage implements IStorage<IBleeding> {
    
    @Override
    public INBT writeNBT(Capability<IBleeding> capability, IBleeding instance, Direction side) {
        return instance.serializeNBT();
    }
    
    @Override
    public void readNBT(Capability<IBleeding> capability, IBleeding instance, Direction side, INBT nbt) {
        instance.deserializeNBT((CompoundNBT) nbt);
    }
}
