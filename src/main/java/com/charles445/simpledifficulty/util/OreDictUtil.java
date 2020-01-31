package com.charles445.simpledifficulty.util;

import javax.annotation.Nonnull;

import com.charles445.simpledifficulty.debug.DebugUtil;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictUtil
{
	public static final NonNullList<ItemStack> logWood = OreDictionary.getOres("logWood");
	public static final NonNullList<ItemStack> stick = OreDictionary.getOres("stickWood");
	
	public static boolean isOre(NonNullList<ItemStack> stackList, ItemStack stackCheck)
	{
		return OreDictUtil.containsMatch(false,stackList,stackCheck);
	}
	
	public static boolean containsMatch(boolean strict, NonNullList<ItemStack> inputs, @Nonnull ItemStack... targets)
	{
		for (ItemStack input : inputs)
		{
			for (ItemStack target : targets)
			{
				//SWAPPED INPUT AND TARGET
				//Why is it like this
				if (OreDictionary.itemMatches(input, target, strict))
				{
					return true;
				}
			}
		}
		return false;
	}
}
