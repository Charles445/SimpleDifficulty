package com.charles445.simpledifficulty.register.crafting;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.AbstractBrewingRecipe;
import net.minecraftforge.common.brewing.BrewingOreRecipe;
import net.minecraftforge.oredict.OreDictionary;

public class FixedBrewingOreRecipe extends AbstractBrewingRecipe<ItemStack>
{
	//I wasn't expecting this to actually be a problem but it is
	//BrewingRecipe and BrewingOreRecipe are set up to handle OreDict items well, but not anything NBT related
	
	public FixedBrewingOreRecipe(ItemStack input, ItemStack ingredient, ItemStack output)
	{
		super(input, ingredient, output);
	}

	@Override
	public boolean isInput(@Nonnull ItemStack stack)
	{
		return super.isInput(stack) && ItemStack.areItemStackTagsEqual(getInput(), stack);
	}

	@Override
	public boolean isIngredient(ItemStack ingredient)
	{
		return OreDictionary.itemMatches(this.getIngredient(), ingredient, false);
	}
}
