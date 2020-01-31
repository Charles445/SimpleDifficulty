package com.charles445.simpledifficulty.register.crafting;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.BrewingOreRecipe;
import net.minecraftforge.oredict.OreDictionary;

public class FixedBrewingOreRecipe extends BrewingOreRecipe
{
	//I wasn't expecting this to actually be a problem but it is
	//BrewingRecipe and BrewingOreRecipe are set up to handle OreDict items well, but not anything NBT related
	
	public FixedBrewingOreRecipe(@Nonnull ItemStack input, @Nonnull String ingredient, @Nonnull ItemStack output)
	{
		super(input, ingredient, output);
	}
	
	public FixedBrewingOreRecipe(@Nonnull ItemStack input, @Nonnull ItemStack ingredient, @Nonnull ItemStack output)
	{
		super(input, Arrays.asList(new ItemStack[]{ingredient}), output);
	}
	
	public FixedBrewingOreRecipe(@Nonnull ItemStack input, @Nonnull List<ItemStack> ingredient, @Nonnull ItemStack output)
	{
		super(input, ingredient, output);
	}

	@Override
	public boolean isInput(@Nonnull ItemStack stack)
	{
		return super.isInput(stack) && ItemStack.areItemStackTagsEqual(getInput(), stack);
	}
}
