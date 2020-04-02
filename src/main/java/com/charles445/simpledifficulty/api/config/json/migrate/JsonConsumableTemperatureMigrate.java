package com.charles445.simpledifficulty.api.config.json.migrate;

import net.minecraft.item.ItemStack;

public class JsonConsumableTemperatureMigrate
{
	public int metadata;
	public String group;
	public float temperature;
	public int duration;
	
	public JsonConsumableTemperatureMigrate(String group, float temperature, int metadata, int duration)
	{
		this.temperature = temperature;
		this.metadata = metadata;
		this.duration = duration;
		this.group = group.toLowerCase();
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
