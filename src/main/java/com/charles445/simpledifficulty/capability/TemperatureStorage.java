package com.charles445.simpledifficulty.capability;

import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class TemperatureStorage implements IStorage<ITemperatureCapability>
{
	private static final String temperatureLevel = "temperatureLevel";
	private static final String temperatureTickTimer = "temperatureTickTimer";
	
	//TODO External Modifiers
	
	@Override
	public NBTBase writeNBT(Capability<ITemperatureCapability> capability, ITemperatureCapability instance, EnumFacing side)
	{
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger(temperatureLevel, instance.getTemperatureLevel());
		compound.setInteger(temperatureTickTimer,instance.getTemperatureTickTimer());
		return compound;
	}

	@Override
	public void readNBT(Capability<ITemperatureCapability> capability, ITemperatureCapability instance, EnumFacing side, NBTBase nbt)
	{
		//TODO failsafe?
		if(nbt instanceof NBTTagCompound)
		{
			NBTTagCompound compound = (NBTTagCompound)nbt;
			
			if(compound.hasKey(temperatureLevel))
				instance.setTemperatureLevel(compound.getInteger(temperatureLevel));
			if(compound.hasKey(temperatureTickTimer))
				instance.setTemperatureTickTimer(compound.getInteger(temperatureTickTimer));
		}
	}
}
