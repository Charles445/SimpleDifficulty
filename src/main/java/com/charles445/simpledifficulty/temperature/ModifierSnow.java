package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.config.ModConfig;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class ModifierSnow extends ModifierBase
{
	public ModifierSnow()
	{
		super("Snow");
	}
	
	//TODO I didn't check Serene Seasons compatibility with this, I should get around to that
	
	@Override
	public float getWorldInfluence(World world, BlockPos pos)
	{
		if(world.isRaining() && world.canSeeSky(pos))
		{
			Biome biome = world.getBiome(pos);
			if(biome.getEnableSnow())
			{
				//Snow enabled variant
				return ModConfig.server.temperature.snowValue;
			}
			else
			{
				//Relies on canSnowAt instead
				if(world.canSnowAt(pos, false))
					return ModConfig.server.temperature.snowValue;
				else
					return 0.0f;
			}
		}
		else
		{
			return 0.0f;
		}
	}
}
