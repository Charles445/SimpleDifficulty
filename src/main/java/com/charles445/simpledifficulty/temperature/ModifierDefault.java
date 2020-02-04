package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModifierDefault extends ModifierBase
{
	public ModifierDefault()
	{
		super("Default");
	}
	
	@Override
	public float getWorldInfluence(World world, BlockPos pos)
	{
		//Default
		return defaultTemperature;
	}
	
}
