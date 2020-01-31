package com.charles445.simpledifficulty.register;

import com.charles445.simpledifficulty.api.SDItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ModCreativeTab extends CreativeTabs
{
	public static final ModCreativeTab instance = new ModCreativeTab(CreativeTabs.getNextID(), "tabSimpleDifficulty");
	public ModCreativeTab(int index, String label)
	{
		super(index, label);
	}
	
	@Override
	public ItemStack getTabIconItem()
	{
		//TODO better looking icon
		return new ItemStack(SDItems.purifiedWaterBottle);
	}
}
