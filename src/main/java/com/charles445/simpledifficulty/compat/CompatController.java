package com.charles445.simpledifficulty.compat;

import javax.annotation.Nullable;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCompatibility;
import com.charles445.simpledifficulty.api.temperature.ITemperatureDynamicModifier;
import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureRegistry;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.CompatUtil;

import net.minecraftforge.fml.common.Loader;

public class CompatController
{
	//private static final String pack = "com.charles445.simpledifficulty.";
	private static final String compatMod = "com.charles445.simpledifficulty.compat.mod.";
	
	//Compatibility is set up to be completely detached from the rest of the project
	//So this mod's own compatibility classes are only interacted with via reflection
	//This will allow for the use of hard dependencies if needed at a later time
	
	
	
	//Dependency Type Quick Reference
	//(None, Reflection, Import)
	
	//AUW - None (Imitation)
	//HarvestFestival - Reflection (API)
	//OreExcavation - Reflection (SubscribeEvent)
	//SereneSeasons - Reflection (API)
	
	
	//postInit
	public static void setupCommon()
	{
		//Create compatibility objects
		Object auwDynamicModifier = newCompatObject(ModNames.AUW, compatMod + "AUWDynamicModifier");
		Object auwModifier = newCompatObject(ModNames.AUW, compatMod + "AUWModifier");
		Object harvestFestivalModifier = newCompatObject(ModNames.HARVESTFESTIVAL, compatMod + "HarvestFestivalModifier");
		Object oreExcavationHandler = newCompatObject(ModNames.OREEXCAVATION, compatMod + "OreExcavationHandler");
		Object sereneSeasonsModifier = newCompatObject(ModNames.SERENESEASONS, compatMod + "SereneSeasonsModifier");
		
		
		if(auwDynamicModifier instanceof ITemperatureDynamicModifier && auwModifier instanceof ITemperatureModifier)
		{
			SimpleDifficulty.logger.info("Armor Underwear Modifiers Enabled");
			TemperatureRegistry.registerDynamicModifier((ITemperatureDynamicModifier)auwDynamicModifier);
			TemperatureRegistry.registerModifier((ITemperatureModifier)auwModifier);
		}
		
		if(harvestFestivalModifier instanceof ITemperatureModifier)
		{
			SimpleDifficulty.logger.info("Harvest Festival Modifier Enabled");
			TemperatureRegistry.registerModifier((ITemperatureModifier)harvestFestivalModifier);
		}
		
		if(oreExcavationHandler != null)
		{
			SimpleDifficulty.logger.info("OreExcavation Handler Enabled");
		}
		
		if(sereneSeasonsModifier instanceof ITemperatureModifier)
		{
			SimpleDifficulty.logger.info("Serene Seasons Modifier Enabled");
			TemperatureRegistry.registerModifier((ITemperatureModifier)sereneSeasonsModifier);
		}
	}
	
	public static void setupClient()
	{
		//This worked, but I decided it wasn't a good idea
		//In safer built-in compatibilities, SimpleDifficulty would invoke functions from the mod.
		//In this one, ClassicBar would be invoking things from SimpleDifficulty
		//That means if something went wrong, a crash couldn't be avoided.
		
		
		/*
		Object classicBarProxy = newCompatObject(ModNames.CLASSICBAR, compatMod + "ClassicBarProxy");
		
		if(classicBarProxy != null)
		{
			SimpleDifficulty.logger.info("ClassicBarProxy Enabled");
		}
		*/
	}
	
	@Nullable
	public static Object newCompatObject(String modid, String clazzpath)
	{
		if(CompatUtil.canUseMod(modid))
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
