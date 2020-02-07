package com.charles445.simpledifficulty.api.config.json;

import net.minecraft.item.ItemStack;

public class JsonTemperatureMetadata
{
	public int metadata;
	public float temperature;
	
	public JsonTemperatureMetadata(int metadata, float temperature)
	{
		this.metadata = metadata;
		this.temperature = temperature;
	}
	
	public boolean matches(ItemStack stack)
	{
		return metadata == -1 || metadata == 32767 || metadata == stack.getMetadata();
	}
	
	public boolean matches(int meta)
	{
		return metadata == -1 || metadata == 32767 || metadata == meta;
	}
}
