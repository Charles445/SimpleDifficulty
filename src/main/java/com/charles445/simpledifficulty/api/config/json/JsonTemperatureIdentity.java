package com.charles445.simpledifficulty.api.config.json;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class JsonTemperatureIdentity
{
	public JsonItemIdentity identity;
	
	public float temperature;
	
	public JsonTemperatureIdentity(float temperature, int metadata)
	{
		this(temperature, new JsonItemIdentity(metadata));
	}
	
	public JsonTemperatureIdentity(float temperature, int metadata, String nbt)
	{
		this(temperature, new JsonItemIdentity(metadata, nbt));
	}
	
	public JsonTemperatureIdentity(float temperature, JsonItemIdentity identity)
	{
		this.temperature = temperature;
		this.identity = identity;
	}
	
	
	
	//Identity matching
	
	public boolean matches(ItemStack stack)
	{
		return identity.matches(stack);
	}
	
	public boolean matches(JsonItemIdentity sentIdentity)
	{
		return identity.matches(sentIdentity);
	}
	
	public boolean matches(int metadata)
	{
		return identity.matches(metadata);
	}
	
	public boolean matches(int metadata, @Nullable NBTTagCompound compound)
	{
		return identity.matches(metadata, compound);
	}
}
