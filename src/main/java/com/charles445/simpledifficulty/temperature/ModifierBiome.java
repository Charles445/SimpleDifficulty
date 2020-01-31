package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.debug.DebugUtil;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class ModifierBiome extends ModifierBase
{
	public ModifierBiome()
	{
		super("Biome");
	}
	
	@Override
	public float getWorldInfluence(World world, BlockPos pos)
	{
		//Takes 9 points in an octagon shape around the player (and where they're standing) and averages the biome temperature
		//This allows for biome temperatures to blend at their borders
		
		float biomeAverage = 
				(getTempForBiome(world.getBiome(pos.add(10,0,0))) +
				getTempForBiome(world.getBiome(pos.add(-10,0,0))) +
				getTempForBiome(world.getBiome(pos.add(0,0,10))) +
				getTempForBiome(world.getBiome(pos.add(0,0,-10)))+
				getTempForBiome(world.getBiome(pos.add(7,0,7)))+
				getTempForBiome(world.getBiome(pos.add(7,0,-7)))+
				getTempForBiome(world.getBiome(pos.add(-7,0,7)))+
				getTempForBiome(world.getBiome(pos.add(-7,0,-7)))+
				getTempForBiome(world.getBiome(pos)))/9.0f;
		
		//Turn the range 0 - 1 into the range -1 - 1 and apply the config multiplier
		return applyUndergroundEffect(normalizeToPlusMinus(biomeAverage) * ModConfig.server.temperature.biomeMultiplier, world, pos);
		
	}
}
