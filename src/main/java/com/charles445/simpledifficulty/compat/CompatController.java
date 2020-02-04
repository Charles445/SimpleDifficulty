package com.charles445.simpledifficulty.compat;

import javax.annotation.Nullable;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCompatibility;
import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureRegistry;

import net.minecraftforge.fml.common.Loader;

public class CompatController
{
	private static final String pack = "com.charles445.simpledifficulty.";
	
	public static void setup()
	{
		//Create compatibility objects
		Object sereneSeasonsModifier = newCompatObject(ModNames.SERENESEASONS, pack + "temperature.ModifierSeason");
		
		
		
		if(sereneSeasonsModifier instanceof ITemperatureModifier)
		{
			SimpleDifficulty.logger.info("Serene Seasons Modifier Enabled");
			TemperatureRegistry.registerModifier((ITemperatureModifier)sereneSeasonsModifier);
		}
	}
	
	@Nullable
	public static Object newCompatObject(String modid, String clazzpath)
	{
		if(Loader.isModLoaded(modid) && !SDCompatibility.disabledCompletely.contains(modid))
		{
			try
			{
				Object o = Class.forName(clazzpath).newInstance();
				
				return o;
			}
			catch (Exception e)
			{
				SimpleDifficulty.logger.error("Mod "+modid+" was loaded but object "+clazzpath+" was not accessible!", e);
			}
		}
		
		return null;
	}
}
