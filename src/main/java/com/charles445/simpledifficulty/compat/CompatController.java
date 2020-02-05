package com.charles445.simpledifficulty.compat;

import javax.annotation.Nullable;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCompatibility;
import com.charles445.simpledifficulty.api.temperature.ITemperatureDynamicModifier;
import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureRegistry;

import net.minecraftforge.fml.common.Loader;

public class CompatController
{
	//private static final String pack = "com.charles445.simpledifficulty.";
	private static final String compatMod = "com.charles445.simpledifficulty.compat.mod.";
	
	public static void setup()
	{
		//Create compatibility objects
		Object auwDynamicModifier = newCompatObject(ModNames.AUW, compatMod + "AUWDynamicModifier");
		Object auwModifier = newCompatObject(ModNames.AUW, compatMod + "AUWModifier");
		Object sereneSeasonsModifier = newCompatObject(ModNames.SERENESEASONS, compatMod + "SereneSeasonsModifier");
		
		
		
		if(sereneSeasonsModifier instanceof ITemperatureModifier)
		{
			SimpleDifficulty.logger.info("Serene Seasons Modifier Enabled");
			TemperatureRegistry.registerModifier((ITemperatureModifier)sereneSeasonsModifier);
		}
		
		if(auwDynamicModifier instanceof ITemperatureDynamicModifier && auwModifier instanceof ITemperatureModifier)
		{
			SimpleDifficulty.logger.info("Armor Underwear Modifiers Enabled");
			TemperatureRegistry.registerDynamicModifier((ITemperatureDynamicModifier)auwDynamicModifier);
			TemperatureRegistry.registerModifier((ITemperatureModifier)auwModifier);
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
