package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonTemperature;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModifierDimension extends ModifierBase
{
	public ModifierDimension()
	{
		super("Dimension");
	}

	@Override
	public float getWorldInfluence(World world, BlockPos pos)
	{
		JsonTemperature tempInfo = JsonConfig.dimensionTemperature.get(""+world.provider.getDimension());
		if(tempInfo!=null)
		{
			return tempInfo.temperature;
		}
		
		//TODO something with dimension names for ease of use
		
		return 0.0f;
	}
}
