package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.debug.DebugUtil;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModifierTime extends ModifierBase
{
	public ModifierTime()
	{
		super("Time");
	}
	
	@Override
	public float getWorldInfluence(World world, BlockPos pos)
	{
		//Overworld only
		if(!world.provider.isSurfaceWorld())
			return 0.0f;
		
		//0 to 23999
		long time = world.getWorldTime() % 24000;
		
		//Day = 0 - 11999
		//Night = 12000 - 23999
		
		//Noon = 6000
		
		//Midnight = 18000
		
		//Daytime and Nighttime config
		if(time < 12000 && !ModConfig.server.temperature.timeTemperatureDay)
			return 0.0f;
		
		if(time >= 12000 && !ModConfig.server.temperature.timeTemperatureNight)
			return 0.0f;
		
		//time % 12000 = Day and Night both become 0 to 11999
		//time -= 6000 = Range becomes -6000 to 5999
		//time / 6000 = Range becomes -1 to 1 (effectively)
		//Math.abs(time) = Range becomes 1 to 0 to 1
		//time -= 1 = Range becomes 0 to -1 to 0
		
		//Do math and timeMultiplier config
		float timetemperature = (Math.abs(((time % 12000.0f) - 6000.0f)/6000.0f) - 1.0f) * ModConfig.server.temperature.timeMultiplier;
		
		//Daytime sign flip
		if(time < 12000)
			timetemperature *= -1.0f;
		
		//0 to 1 for getTempForBiome, plusminus to -1 to 1, absolute to 1 to 0 to 1
		//Would result in 0 to 1.25
		//So instead, result in 0 to 0.25, and then add 1.0f to get 1 to 1.25
		
		float biomeMultiplier = 1.0f + (Math.abs(normalizeToPlusMinus(getTempForBiome(world.getBiome(pos)))) * ((float)ModConfig.server.temperature.timeBiomeMultiplier - 1.0f));
		timetemperature *= biomeMultiplier;
		
		//Shade calculation, runs AFTER the biome multiplier
		//Should have a value that's -3 to 3, or configured value
		int shadeConf = ModConfig.server.temperature.timeTemperatureShade;
		//TODO don't do it like this
		if(timetemperature > 0 && shadeConf != 0 && !world.canSeeSky(pos) && !world.canSeeSky(pos.up()))
		{
			timetemperature = Math.max(0, timetemperature + shadeConf);
		}
		
		//Underground effect and result
		return applyUndergroundEffect(timetemperature, world, pos);
	}
}
