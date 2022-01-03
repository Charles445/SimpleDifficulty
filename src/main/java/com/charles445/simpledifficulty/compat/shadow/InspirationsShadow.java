package com.charles445.simpledifficulty.compat.shadow;

import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;

public class InspirationsShadow
{
	public static interface ICauldronRecipe
	{
		boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state);
		
		ItemStack getResult(ItemStack stack, boolean boiling, int level, CauldronState state);
		
		ItemStack getContainer(ItemStack stack);
		
		SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state);
		
		int getLevel(int level);
		
		public class CauldronState
		{
			public Fluid getFluid()
			{
				return null;
			}
		}
	}
}
