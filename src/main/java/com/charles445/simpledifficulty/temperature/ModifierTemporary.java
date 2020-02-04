package com.charles445.simpledifficulty.temperature;

import java.util.Map;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.temperature.TemporaryModifier;

import net.minecraft.entity.player.EntityPlayer;

public class ModifierTemporary extends ModifierBase
{
	public ModifierTemporary()
	{
		super("Temporary");
	}

	@Override
	public float getPlayerInfluence(EntityPlayer player)
	{
		float sum = 0.0f;
		for(TemporaryModifier tm : SDCapabilities.getTemperatureData(player).getTemporaryModifiers().values())
		{
			sum += tm.temperature;
		}
		return sum;
	}
}
