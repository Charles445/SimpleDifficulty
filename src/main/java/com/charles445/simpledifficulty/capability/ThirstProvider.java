package com.charles445.simpledifficulty.capability;

import com.charles445.simpledifficulty.api.thirst.IThirstCapability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class ThirstProvider implements ICapabilitySerializable<NBTBase>
{
	final Capability<IThirstCapability> capability;
	final IThirstCapability instance;
	
	public ThirstProvider(Capability<IThirstCapability> newcapability)
	{
		this.capability = newcapability;
		this.instance = capability.getDefaultInstance();
	}
	
	@Override
	public boolean hasCapability(Capability<?> requestedcapability, EnumFacing facing)
	{
		return (requestedcapability==this.capability);
	}

	@Override
	public <T> T getCapability(Capability<T> requestedcapability, EnumFacing facing)
	{
		if(requestedcapability==this.capability)
		{
			return capability.cast(this.instance);
		}
		
		return null;
	}

	@Override
	public NBTBase serializeNBT()
	{
		return this.capability.writeNBT(this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt)
	{
		this.capability.readNBT(this.instance, null, nbt);
	}
	
	
}
