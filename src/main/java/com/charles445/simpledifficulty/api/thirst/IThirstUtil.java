package com.charles445.simpledifficulty.api.thirst;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IThirstUtil
{
	@Nullable
	public ThirstEnumBlockPos traceWater(EntityPlayer player);
	
	public void takeDrink(EntityPlayer player, int thirst, float saturation, float dirtyChance);
	
	public void takeDrink(EntityPlayer player, int thirst, float saturation);
	
	public void takeDrink(EntityPlayer player, ThirstEnum type);
	
	public ItemStack createPurifiedWaterBucket();
	
}
