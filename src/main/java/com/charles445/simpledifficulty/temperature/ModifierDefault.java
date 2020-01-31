package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModifierDefault extends ModifierBase
{
	private final float defaultTemperature;
	
	public ModifierDefault()
	{
		super("Default");
		this.defaultTemperature = (TemperatureEnum.NORMAL.getUpperBound() + TemperatureEnum.COLD.getUpperBound()) / 2;
	}
	
	@Override
	public float getWorldInfluence(World world, BlockPos pos)
	{
		//Default
		return defaultTemperature;
	}
	
}
