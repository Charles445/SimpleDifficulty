package com.charles445.simpledifficulty.api.config.json;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class JsonConsumableThirst
{
	public JsonItemIdentity identity;
	
	public int amount;
	public float saturation;
	public float thirstyChance;
	
	public JsonConsumableThirst(int amount, float saturation, float thirstyChance, int metadata)
	{
		this(amount, saturation, thirstyChance, new JsonItemIdentity(metadata));
	}
	
	public JsonConsumableThirst(int amount, float saturation, float thirstyChance, int metadata, String nbt)
	{
		this(amount, saturation, thirstyChance, new JsonItemIdentity(metadata, nbt));
	}
	
	public JsonConsumableThirst(int amount, float saturation, float thirstyChance, JsonItemIdentity identity)
	{
		this.amount = amount;
		this.saturation = saturation;
		this.thirstyChance = thirstyChance;
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
