package com.charles445.simpledifficulty.capability;

import com.charles445.simpledifficulty.api.thirst.IThirstCapability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
public class ThirstStorage implements IStorage<IThirstCapability>
{
	private static final String thirstExhaustion = "thirstExhaustion";
	private static final String thirstLevel = "thirstLevel";
	private static final String thirstSaturation = "thirstSaturation";
	private static final String thirstTickTimer = "thirstTickTimer";
	private static final String thirstDamageCounter = "thirstDamageCounter";
	

	@Override
	public NBTBase writeNBT(Capability<IThirstCapability> capability, IThirstCapability instance, EnumFacing side)
	{
		NBTTagCompound compound = new NBTTagCompound();
		compound.setFloat(thirstExhaustion, instance.getThirstExhaustion());
		compound.setInteger(thirstLevel, instance.getThirstLevel());
		compound.setFloat(thirstSaturation,instance.getThirstSaturation());
		compound.setInteger(thirstTickTimer,instance.getThirstTickTimer());
		compound.setInteger(thirstDamageCounter,instance.getThirstDamageCounter());
		return compound;
	}

	@Override
	public void readNBT(Capability<IThirstCapability> capability, IThirstCapability instance, EnumFacing side, NBTBase nbt)
	{
		if(nbt instanceof NBTTagCompound)
		{
			NBTTagCompound compound = (NBTTagCompound)nbt;
			
			if(compound.hasKey(thirstExhaustion))
				instance.setThirstExhaustion(compound.getFloat(thirstExhaustion));
			if(compound.hasKey(thirstLevel))
				instance.setThirstLevel(compound.getInteger(thirstLevel));
			if(compound.hasKey(thirstSaturation))
				instance.setThirstSaturation(compound.getFloat(thirstSaturation));
			if(compound.hasKey(thirstTickTimer))
				instance.setThirstTickTimer(compound.getInteger(thirstTickTimer));
			if(compound.hasKey(thirstDamageCounter))
				instance.setThirstDamageCounter(compound.getInteger(thirstDamageCounter));
		}
	}

}
